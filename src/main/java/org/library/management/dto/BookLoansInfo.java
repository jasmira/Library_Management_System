package org.library.management.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
public class BookLoansInfo {
    // book details
    private String title;
    private String author;
    private String ISBN;
    private String category;
    private boolean available;
    private LocalDate addedDate;

    // loans details
    private LocalDate borrowDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private BigDecimal fineAmount;
}
