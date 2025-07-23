package org.library.management.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.library.management.dto.BookLoansInfo;
import org.library.management.dto.LoansResponseDTO;
import org.library.management.dto.MemberFinesReportDTO;
import org.library.management.dto.MemberRequestDTO;
import org.library.management.entity.Member;
import org.library.management.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MemberController.class)
public class MemberControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MemberService memberService;

    private MemberRequestDTO sampleMemberDTO;
    private Member sampleMember;

    @BeforeEach
    void setUp() {
        sampleMemberDTO = new MemberRequestDTO("John Doe", "john@example.com", LocalDate.of(2024, 1, 1));
        sampleMember = new Member("John Doe", "john@example.com", LocalDate.of(2024, 1, 1));
    }

    @Test
    void testAddMember() throws Exception {
        when(memberService.addMember(any(MemberRequestDTO.class)))
                .thenReturn("Member with ID: 1 registered successfully.");

        mockMvc.perform(post("/api/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(sampleMemberDTO)))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("registered successfully")));
    }

    @Test
    void testGetAllMembers() throws Exception {
        when(memberService.getAllMembers()).thenReturn(List.of(sampleMember));

        mockMvc.perform(get("/api/members"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("John Doe"));
    }

    @Test
    void testGetMemberById() throws Exception {
        when(memberService.getMember(1L)).thenReturn(sampleMember);

        mockMvc.perform(get("/api/members/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("john@example.com"));
    }

    @Test
    void testGetLoansForMember() throws Exception {
        LoansResponseDTO responseDTO = new LoansResponseDTO(
                1L,
                List.of(new BookLoansInfo("Book A", "Author A", "ISBN123", "Fiction", false,
                        LocalDate.now(), LocalDate.now().minusDays(10), LocalDate.now().plusDays(4), null, BigDecimal.ZERO))
        );

        when(memberService.getLoansForMember(1L)).thenReturn(responseDTO);

        mockMvc.perform(get("/api/members/1/loans"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.memberId").value(1L));
    }

    @Test
    void testGetMembersWithPendingFines() throws Exception {
        MemberFinesReportDTO report = new MemberFinesReportDTO(
                1L,
                "John Doe",
                BigDecimal.valueOf(30),
                Collections.emptyList()
        );

        when(memberService.getMembersWithPendingFines()).thenReturn(List.of(report));

        mockMvc.perform(get("/api/members/reports/dues"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].memberId").value(1L))
                .andExpect(jsonPath("$[0].totalFine").value(30));
    }

    // Helper to convert object to JSON
    private static String asJsonString(final Object obj) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            return mapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
