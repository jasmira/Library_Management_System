package org.library.management.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.library.management.dto.BookRequestDTO;
import org.library.management.entity.Book;
import org.library.management.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookController.class)
public class BookControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookService bookService;

    private Book sampleBook;
    private BookRequestDTO sampleBookDTO;

    @BeforeEach
    void setUp() {
        sampleBook = new Book("Book Title", "Author", "ISBN", "Fiction", true, LocalDate.now());
        sampleBookDTO = new BookRequestDTO("Book Title", "Author", "ISBN", "Fiction", true, LocalDate.now());
    }

    @Test
    void testAddBook() throws Exception {
        when(bookService.addBook(any(BookRequestDTO.class))).thenReturn("Book with ID: 1 added successfully.");

        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(sampleBookDTO)))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Book with ID:")));
    }

    @Test
    void testGetAllBooks() throws Exception {
        when(bookService.getAllBooks()).thenReturn(List.of(sampleBook));

        mockMvc.perform(get("/api/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Book Title"));
    }

    @Test
    void testGetBookById() throws Exception {
        when(bookService.getBook(1L)).thenReturn(sampleBook);

        mockMvc.perform(get("/api/books/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Book Title"));
    }

    @Test
    void testUpdateBook() throws Exception {
        when(bookService.updateBook(eq(1L), any(BookRequestDTO.class)))
                .thenReturn("Book with ID: 1 updated successfully.");

        mockMvc.perform(put("/api/books/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(sampleBookDTO)))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("updated successfully")));
    }

    @Test
    void testDeleteBook() throws Exception {
        doNothing().when(bookService).deleteBook(1L);

        mockMvc.perform(delete("/api/books/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Book with ID: 1 deleted successfully."));
    }

    // Utility method to convert object to JSON string
    private static String asJsonString(final Object obj) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule()); // Support for LocalDate
            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // Optional: use ISO format
            return mapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
