package org.library.management.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.library.management.dto.LoansResponseDTO;
import org.library.management.dto.MemberFinesReportDTO;
import org.library.management.dto.MemberRequestDTO;
import org.library.management.entity.Book;
import org.library.management.entity.BorrowRecord;
import org.library.management.entity.Member;
import org.library.management.exception.MemberNotFoundException;
import org.library.management.repository.BookRepository;
import org.library.management.repository.BorrowRecordRepository;
import org.library.management.repository.MemberRepository;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MemberServiceTest {
    @Mock
    private MemberRepository memberRepository;
    @Mock private BorrowRecordRepository borrowRecordRepository;
    @Mock private BookRepository bookRepository;

    @InjectMocks
    private MemberService memberService;

    private Member sampleMember;
    private MemberRequestDTO sampleMemberDTO;
    private Book sampleBook;
    private BorrowRecord sampleBorrowRecord;

    @BeforeEach
    void setUp() {
        sampleMember = new Member("Alice", "alice@example.com", LocalDate.now());

        sampleMemberDTO = new MemberRequestDTO("Alice", "alice@example.com", LocalDate.now());

        sampleBook = new Book("BookTitle", "Author", "ISBN", "Fiction", true, LocalDate.now());

        sampleBorrowRecord = new BorrowRecord(
                1L,
                1L,
                LocalDate.now().minusDays(20),
                LocalDate.now().minusDays(6),
                LocalDate.now().minusDays(1),
                new BigDecimal("40")
        );
    }

    @Test
    void testAddMember() {
        when(memberRepository.save(any(Member.class))).thenReturn(sampleMember);

        String result = memberService.addMember(sampleMemberDTO);

        assertTrue(result.contains("Member with ID:") && result.contains("registered successfully."));
        verify(memberRepository).save(any(Member.class));
    }

    @Test
    void testGetAllMembers() {
        when(memberRepository.findAll()).thenReturn(List.of(sampleMember));

        List<Member> members = memberService.getAllMembers();

        assertEquals(1, members.size());
        assertEquals("Alice", members.get(0).getName());
    }

    @Test
    void testGetMemberById() {
        when(memberRepository.findById(1L)).thenReturn(Optional.of(sampleMember));

        Member member = memberService.getMember(1L);

        assertEquals("Alice", member.getName());
    }

    @Test
    void testGetMemberById_NotFound() {
        when(memberRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(MemberNotFoundException.class, () -> memberService.getMember(1L));
    }

    @Test
    void testGetLoansForMember() {
        when(borrowRecordRepository.findByMemberId(1L)).thenReturn(List.of(sampleBorrowRecord));
        when(bookRepository.findById(1L)).thenReturn(Optional.of(sampleBook));

        LoansResponseDTO response = memberService.getLoansForMember(1L);

        assertEquals(1L, response.getMemberId());
        assertEquals(1, response.getBookLoansInfoList().size());
        assertEquals("BookTitle", response.getBookLoansInfoList().get(0).getTitle());
    }

    @Test
    void testGetMembersWithPendingFines() {
        when(borrowRecordRepository.findByFineAmountGreaterThan(BigDecimal.ZERO))
                .thenReturn(List.of(sampleBorrowRecord));
        when(memberRepository.findById(1L)).thenReturn(Optional.of(sampleMember));
        when(bookRepository.findById(1L)).thenReturn(Optional.of(sampleBook));

        List<MemberFinesReportDTO> result = memberService.getMembersWithPendingFines();

        assertEquals(1, result.size());
        assertEquals(new BigDecimal("40"), result.get(0).getTotalFine());
        assertEquals("Alice", result.get(0).getMemberName());
    }
}
