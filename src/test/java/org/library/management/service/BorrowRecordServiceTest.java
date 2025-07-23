package org.library.management.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.library.management.dto.BorrowReturnRequestDTO;
import org.library.management.entity.Book;
import org.library.management.entity.BorrowRecord;
import org.library.management.entity.Member;
import org.library.management.exception.BookLimitExceededException;
import org.library.management.exception.BookUnavailableException;
import org.library.management.exception.BorrowRecordNotFoundException;
import org.library.management.repository.BookRepository;
import org.library.management.repository.BorrowRecordRepository;
import org.library.management.repository.MemberRepository;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BorrowRecordServiceTest {
    @Mock
    private BookRepository bookRepository;
    @Mock private MemberRepository memberRepository;
    @Mock private BorrowRecordRepository borrowRecordRepository;

    @InjectMocks
    private BorrowRecordService borrowRecordService;

    private Book sampleBook;
    private Member sampleMember;
    private BorrowReturnRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        sampleBook = new Book("BookTitle", "Author", "ISBN", "Fiction", true, LocalDate.now());
        //sampleBook.setBookId(100L);

        sampleMember = new Member("Alice", "alice@example.com", LocalDate.now());
        //sampleMember.setMemberId(1L);
        sampleMember.setBookLimit(2); // less than 5

        requestDTO = new BorrowReturnRequestDTO(1L, 1L);
    }

    @Test
    void testBorrowBook_Successful() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(sampleBook));
        when(memberRepository.findById(1L)).thenReturn(Optional.of(sampleMember));
        when(borrowRecordRepository.save(any(BorrowRecord.class))).thenAnswer(i -> i.getArgument(0));

        String result = borrowRecordService.borrowBook(requestDTO);

        assertTrue(result.contains("Book Borrowed Successfully"));
        verify(bookRepository).save(any(Book.class));
        verify(borrowRecordRepository).save(any(BorrowRecord.class));
    }

    @Test
    void testBorrowBook_BookLimitExceeded() {
        sampleMember.setBookLimit(5); // at limit
        when(bookRepository.findById(1L)).thenReturn(Optional.of(sampleBook));
        when(memberRepository.findById(1L)).thenReturn(Optional.of(sampleMember));

        assertThrows(BookLimitExceededException.class, () -> borrowRecordService.borrowBook(requestDTO));
    }

    @Test
    void testBorrowBook_BookUnavailable() {
        sampleBook.setAvailable(false);
        when(bookRepository.findById(1L)).thenReturn(Optional.of(sampleBook));
        when(memberRepository.findById(1L)).thenReturn(Optional.of(sampleMember));

        assertThrows(BookUnavailableException.class, () -> borrowRecordService.borrowBook(requestDTO));
    }

    @Test
    void testReturnBook_OnTime_NoFine() {
        sampleBook.setAvailable(false);
        BorrowRecord record = new BorrowRecord(1L, 1L, LocalDate.now().minusDays(10), LocalDate.now().plusDays(4), null, BigDecimal.ZERO);

        when(bookRepository.findById(1L)).thenReturn(Optional.of(sampleBook));
        when(borrowRecordRepository.findByBookIdAndMemberId(1L, 1L)).thenReturn(Optional.of(record));
        when(borrowRecordRepository.save(any(BorrowRecord.class))).thenReturn(record);
        when(bookRepository.save(any(Book.class))).thenReturn(sampleBook);

        String result = borrowRecordService.returnBook(requestDTO);

        assertTrue(result.contains("No fines incurred"));
        verify(bookRepository).save(any(Book.class));
    }

    @Test
    void testReturnBook_Late_WithFine() {
        sampleBook.setAvailable(false);
        BorrowRecord record = new BorrowRecord(1L, 1L, LocalDate.now().minusDays(20), LocalDate.now().minusDays(6), null, BigDecimal.ZERO);

        when(bookRepository.findById(1L)).thenReturn(Optional.of(sampleBook));
        when(borrowRecordRepository.findByBookIdAndMemberId(1L, 1L)).thenReturn(Optional.of(record));
        when(borrowRecordRepository.save(any(BorrowRecord.class))).thenReturn(record);
        when(bookRepository.save(any(Book.class))).thenReturn(sampleBook);

        String result = borrowRecordService.returnBook(requestDTO);

        assertTrue(result.contains("Incurred a fine of Rs."));
    }

    @Test
    void testReturnBook_RecordNotFound() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(sampleBook));
        when(borrowRecordRepository.findByBookIdAndMemberId(1L, 1L)).thenReturn(Optional.empty());

        assertThrows(BorrowRecordNotFoundException.class, () -> borrowRecordService.returnBook(requestDTO));
    }
}
