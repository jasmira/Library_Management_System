package org.library.management.service;

import org.library.management.dto.BookRequestDTO;
import org.library.management.entity.Book;
import org.library.management.exception.BookNotFoundException;
import org.library.management.repository.BookRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class BookService {
    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public String addBook(BookRequestDTO bookRequestDTO) {
        Book book = new Book(bookRequestDTO.getTitle(), bookRequestDTO.getAuthor(), bookRequestDTO.getISBN(), bookRequestDTO.getCategory(), true, LocalDate.now());
        Book newBook = bookRepository.save(book);
        return "Book with ID: " + newBook.getBookId()+ " added successfully.";
    }

    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    public Book getBook(Long bookId) {
        return bookRepository.findById(bookId).orElseThrow(() -> new BookNotFoundException("Book with ID: " +bookId+ " not found in the Library."));
    }

    public String updateBook(Long bookId, BookRequestDTO bookRequestDTO) {
        Book book = bookRepository.findById(bookId).orElseThrow(() -> new BookNotFoundException("Book with ID: " +bookId+ " not found in the Library."));
        book.setTitle(bookRequestDTO.getTitle());
        book.setAuthor(bookRequestDTO.getAuthor());
        book.setISBN(bookRequestDTO.getISBN());
        book.setCategory(bookRequestDTO.getCategory());
        book.setAvailable(bookRequestDTO.isAvailable());
        book.setAddedDate(bookRequestDTO.getAddedDate());
        Book updatedBook = bookRepository.save(book);
        return "Book with ID: " +updatedBook.getBookId()+ " updated successfully.";
    }

    public void deleteBook(Long bookId) {
        Book book = bookRepository.findById(bookId).orElseThrow(() -> new BookNotFoundException("Book with ID: " +bookId+ " not found in the Library."));
        bookRepository.delete(book);
    }
}
