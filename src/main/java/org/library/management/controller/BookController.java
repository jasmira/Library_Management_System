package org.library.management.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.library.management.dto.BookRequestDTO;
import org.library.management.entity.Book;
import org.library.management.service.BookService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
@Tag(name = "Books", description = "Manage library books")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @Operation(summary = "Add a new book")
    @PostMapping
    ResponseEntity<String> addBook(@RequestBody BookRequestDTO bookRequestDTO) {
        String response = bookService.addBook(bookRequestDTO);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get all books")
    @GetMapping
    ResponseEntity<List<Book>> getAllBooks() {
        List<Book> bookList = bookService.getAllBooks();
        return ResponseEntity.ok(bookList);
    }

    @Operation(summary = "Get a book by bookId")
    @GetMapping("/{id}")
    ResponseEntity<Book> getBook(@PathVariable Long id) {
        Book book = bookService.getBook(id);
        return ResponseEntity.ok(book);
    }

    @Operation(summary = "Update a book")
    @PutMapping("/{id}")
    ResponseEntity<String> updateBook(@PathVariable Long id,
              @RequestBody BookRequestDTO bookRequestDTO) {
        String response = bookService.updateBook(id, bookRequestDTO);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Delete a book")
    @DeleteMapping("/{id}")
    ResponseEntity<String> deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        return ResponseEntity.ok("Book with ID: " +id+ " deleted successfully.");
    }
}
