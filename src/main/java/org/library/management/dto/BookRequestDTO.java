package org.library.management.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
public class BookRequestDTO {
    private String title;
    private String author;
    private String ISBN;
    private String category;
    private boolean available;
    private LocalDate addedDate;
}
