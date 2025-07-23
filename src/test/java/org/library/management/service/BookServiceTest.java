package org.library.management.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.library.management.dto.BookRequestDTO;
import org.library.management.entity.Book;
import org.library.management.exception.BookNotFoundException;
import org.library.management.repository.BookRepository;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookServiceTest {
    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookService bookService;

    private Book sampleBook;
    private BookRequestDTO sampleBookDTO;

    @BeforeEach
    public void setUp() {
        sampleBook = new Book("Title", "Author", "ISBN123", "Fiction", true, LocalDate.now());
        sampleBookDTO = new BookRequestDTO("Title", "Author", "ISBN123", "Fiction", true, LocalDate.now());
    }

    @Test
    public void testAddBook() {
        when(bookRepository.save(any(Book.class))).thenReturn(sampleBook);

        String result = bookService.addBook(sampleBookDTO);

        assertTrue(result.startsWith("Book with ID:") && result.endsWith("added successfully."));
        verify(bookRepository, times(1)).save(any(Book.class));
    }

    @Test
    public void testGetAllBooks() {
        when(bookRepository.findAll()).thenReturn(List.of(sampleBook));

        List<Book> books = bookService.getAllBooks();

        assertEquals(1, books.size());
        assertEquals("Title", books.get(0).getTitle());
    }

    @Test
    public void testGetBookById() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(sampleBook));

        Book book = bookService.getBook(1L);

        assertEquals("Title", book.getTitle());
    }

    @Test
    public void testGetBookById_NotFound() {
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(BookNotFoundException.class, () -> bookService.getBook(1L));
    }

    @Test
    public void testUpdateBook() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(sampleBook));
        when(bookRepository.save(any(Book.class))).thenReturn(sampleBook);

        String result = bookService.updateBook(1L, sampleBookDTO);

        assertTrue(result.startsWith("Book with ID:") && result.endsWith("updated successfully."));
    }

    @Test
    public void testDeleteBook() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(sampleBook));
        doNothing().when(bookRepository).delete(sampleBook);

        bookService.deleteBook(1L);

        verify(bookRepository, times(1)).delete(sampleBook);
    }

    @Test
    public void testDeleteBook_NotFound() {
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(BookNotFoundException.class, () -> bookService.deleteBook(1L));
    }
}
