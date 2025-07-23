package org.library.management.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.library.management.dto.BorrowReturnRequestDTO;
import org.library.management.service.BorrowRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BorrowRecordController.class)
public class BorrowRecordControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BorrowRecordService borrowRecordService;

    private BorrowReturnRequestDTO borrowRequestDTO;

    @BeforeEach
    void setUp() {
        borrowRequestDTO = new BorrowReturnRequestDTO(1L, 2L); // bookId = 1, memberId = 2
    }

    @Test
    void testBorrowBook() throws Exception {
        when(borrowRecordService.borrowBook(any(BorrowReturnRequestDTO.class)))
                .thenReturn("Book Borrowed Successfully. Your due date to return the book is: " + LocalDate.now().plusDays(14));

        mockMvc.perform(post("/api/loans/borrow")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(borrowRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Book Borrowed Successfully")));
    }

    @Test
    void testReturnBook() throws Exception {
        when(borrowRecordService.returnBook(any(BorrowReturnRequestDTO.class)))
                .thenReturn("Book Returned Successfully. No fines incurred.");

        mockMvc.perform(post("/api/loans/return")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(borrowRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Book Returned Successfully")));
    }

    // Helper method to convert object to JSON
    private static String asJsonString(Object obj) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            return mapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
