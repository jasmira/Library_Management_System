package org.library.management.service;

import org.library.management.dto.BookLoansInfo;
import org.library.management.dto.LoansResponseDTO;
import org.library.management.dto.MemberFinesReportDTO;
import org.library.management.dto.MemberRequestDTO;
import org.library.management.entity.Book;
import org.library.management.entity.BorrowRecord;
import org.library.management.entity.Member;
import org.library.management.exception.BookNotFoundException;
import org.library.management.exception.MemberNotFoundException;
import org.library.management.repository.BookRepository;
import org.library.management.repository.BorrowRecordRepository;
import org.library.management.repository.MemberRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class MemberService {
    private final MemberRepository memberRepository;
    private final BorrowRecordRepository borrowRecordRepository;
    private final BookRepository bookRepository;

    public MemberService(MemberRepository memberRepository, BorrowRecordRepository borrowRecordRepository, BookRepository bookRepository) {
        this.memberRepository = memberRepository;
        this.borrowRecordRepository = borrowRecordRepository;
        this.bookRepository = bookRepository;
    }
    public String addMember(MemberRequestDTO memberRequestDTO) {
        Member member = new Member(memberRequestDTO.getName(), memberRequestDTO.getEmail(), memberRequestDTO.getJoinDate());
        Member newMember = memberRepository.save(member);
        return "Member with ID: " + newMember.getMemberId()+ " registered successfully.";
    }

    public List<Member> getAllMembers() {
        return memberRepository.findAll();
    }

    public Member getMember(Long memberId) {
        return memberRepository.findById(memberId).orElseThrow(() -> new MemberNotFoundException("Member with ID: " +memberId+ " not found."));
    }

    public LoansResponseDTO getLoansForMember(Long memberId) {
        List<BorrowRecord> borrowRecordList = borrowRecordRepository.findByMemberId(memberId);
        LoansResponseDTO loansResponseDTO;
        List<BookLoansInfo> bookLoansInfoList = new ArrayList<>();

        for (BorrowRecord borrowRecord : borrowRecordList) {
            Book book = bookRepository.findById(borrowRecord.getBookId()).orElseThrow(() -> new BookNotFoundException("Book with ID: " + borrowRecord.getBookId() + " not found in the Library."));

            // populate book and loans details
            BookLoansInfo bookLoansInfo = new BookLoansInfo(book.getTitle(), book.getAuthor(), book.getISBN(), book.getCategory(), book.isAvailable(), book.getAddedDate(), borrowRecord.getBorrowDate(), borrowRecord.getDueDate(), borrowRecord.getReturnDate(), borrowRecord.getFineAmount());
            bookLoansInfoList.add(bookLoansInfo);
        }
        // populate response dto
        loansResponseDTO = new LoansResponseDTO(memberId, bookLoansInfoList);
        return loansResponseDTO;
    }

    public List<MemberFinesReportDTO> getMembersWithPendingFines() {
        List<BorrowRecord> overdueReturns = borrowRecordRepository.findByFineAmountGreaterThan(BigDecimal.ZERO);

        // Group overdue borrow records by memberId
        Map<Long, List<BorrowRecord>> recordsGroupedByMember = overdueReturns.stream()
                .collect(Collectors.groupingBy(BorrowRecord::getMemberId));

        List<MemberFinesReportDTO> memberFinesReportDTOList = new ArrayList<>();

        for (Map.Entry<Long, List<BorrowRecord>> entry : recordsGroupedByMember.entrySet()) {
            Long memberId = entry.getKey();
            List<BorrowRecord> memberBorrowRecords = entry.getValue();

            Member member = memberRepository.findById(memberId)
                    .orElseThrow(() -> new MemberNotFoundException("Member with ID: " + memberId + " not found."));

            // Total fine calculation for this member
            BigDecimal totalFine = memberBorrowRecords.stream()
                    .map(BorrowRecord::getFineAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            // Build BookLoansInfo list for this member
            List<BookLoansInfo> overdueBooks = new ArrayList<>();
            for (BorrowRecord record : memberBorrowRecords) {
                Book book = bookRepository.findById(record.getBookId())
                        .orElseThrow(() -> new BookNotFoundException("Book with ID: " + record.getBookId() + " not found."));

                BookLoansInfo bookLoansInfo = new BookLoansInfo(
                        book.getTitle(),
                        book.getAuthor(),
                        book.getISBN(),
                        book.getCategory(),
                        book.isAvailable(),
                        book.getAddedDate(),
                        record.getBorrowDate(),
                        record.getDueDate(),
                        record.getReturnDate(),
                        record.getFineAmount()
                );
                overdueBooks.add(bookLoansInfo);
            }

            MemberFinesReportDTO memberReport = new MemberFinesReportDTO(
                    memberId,
                    member.getName(),
                    totalFine,
                    overdueBooks
            );

            memberFinesReportDTOList.add(memberReport);
        }

        return memberFinesReportDTOList;
    }
}
