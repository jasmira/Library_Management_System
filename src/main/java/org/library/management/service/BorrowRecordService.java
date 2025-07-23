package org.library.management.service;

import org.library.management.dto.BorrowReturnRequestDTO;
import org.library.management.entity.Book;
import org.library.management.entity.BorrowRecord;
import org.library.management.entity.Member;
import org.library.management.exception.*;
import org.library.management.repository.BookRepository;
import org.library.management.repository.BorrowRecordRepository;
import org.library.management.repository.MemberRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Service
public class BorrowRecordService {
    private final BookRepository bookRepository;
    private final MemberRepository memberRepository;
    private final BorrowRecordRepository borrowRecordRepository;

    public BorrowRecordService(BookRepository bookRepository, MemberRepository memberRepository, BorrowRecordRepository borrowRecordRepository) {
        this.bookRepository = bookRepository;
        this.memberRepository = memberRepository;
        this.borrowRecordRepository = borrowRecordRepository;
    }

    public String borrowBook(BorrowReturnRequestDTO borrowReturnRequestDTO) {
        Book book = bookRepository.findById(borrowReturnRequestDTO.getBookId()).orElseThrow(() -> new BookNotFoundException("Book with ID: " +borrowReturnRequestDTO.getBookId()+ " not found in the Library."));
        Member member = memberRepository.findById(borrowReturnRequestDTO.getMemberId()).orElseThrow(() -> new MemberNotFoundException("Member with ID: " +borrowReturnRequestDTO.getMemberId()+ " not found."));
        String response;

        // Check: book available?
        if(book.isAvailable()) {
            // Check: has member reached borrow limit?
            int maxBookLimitAllowed = 5;
            if(member.getBookLimit() < maxBookLimitAllowed) {
                // Set borrowDate, dueDate;
                BorrowRecord borrowRecord = new BorrowRecord(borrowReturnRequestDTO.getBookId(), borrowReturnRequestDTO.getMemberId(), LocalDate.now(), LocalDate.now().plusDays(14), null, BigDecimal.ZERO);
                borrowRecordRepository.save(borrowRecord);
                // Mark book as unavailable
                book.setAvailable(false);
                bookRepository.save(book);
                response = "Book Borrowed Successfully. Your due date to return the book is: " +borrowRecord.getDueDate();
            } else {
                throw new BookLimitExceededException("Book Limit of 5 exceeded for Member: "+borrowReturnRequestDTO.getMemberId()+ ".");
            }
        } else {
            throw new BookUnavailableException("Book " +borrowReturnRequestDTO.getBookId()+ " is not available at the moment.");
        }
        return response;
    }

    public String returnBook(BorrowReturnRequestDTO borrowReturnRequestDTO) {
        Book book = bookRepository.findById(borrowReturnRequestDTO.getBookId()).orElseThrow(() -> new BookNotFoundException("Book with ID: " +borrowReturnRequestDTO.getBookId()+ " not found in the Library."));
        Optional<BorrowRecord> borrowRecordOptional = borrowRecordRepository.findByBookIdAndMemberId(borrowReturnRequestDTO.getBookId(), borrowReturnRequestDTO.getMemberId());
        BigDecimal fineAmount = BigDecimal.ZERO;
        LocalDate currentDate = LocalDate.now();
        String response;

        if (borrowRecordOptional.isPresent()) {
            BorrowRecord borrowRecord = borrowRecordOptional.get();

            LocalDate dueDate = borrowRecord.getDueDate();
            if(currentDate.isAfter(dueDate)) {
                long daysOverdue = ChronoUnit.DAYS.between(dueDate, currentDate);
                // Calculate fine if overdue
                fineAmount = BigDecimal.valueOf(daysOverdue * 10);
            }

            // Set returnDate, fineAmount
            borrowRecord.setReturnDate(currentDate);
            borrowRecord.setFineAmount(fineAmount);
            borrowRecordRepository.save(borrowRecord);

            // Mark book as available
            book.setAvailable(true);
            bookRepository.save(book);

            if(!fineAmount.equals(BigDecimal.ZERO)) {
                response = "Book Returned Successfully. Incurred a fine of Rs." +fineAmount+ " for late return.";
            } else {
                response = "Book Returned Successfully. No fines incurred.";
            }
        } else {
            throw new BorrowRecordNotFoundException("No Borrow record found for MemberId: " +borrowReturnRequestDTO.getMemberId()+ " and BookId: " +borrowReturnRequestDTO.getBookId());
        }
        return response;
    }
}
