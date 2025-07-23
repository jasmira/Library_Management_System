package org.library.management.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.library.management.dto.BorrowReturnRequestDTO;
import org.library.management.service.BorrowRecordService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/loans")
@Tag(name = "Borrow/Return Records", description = "Manage Borrow & Return book records")
public class BorrowRecordController {
    private final BorrowRecordService borrowRecordService;

    public BorrowRecordController(BorrowRecordService borrowRecordService) {
        this.borrowRecordService = borrowRecordService;
    }

    @Operation(summary = "Borrow a book")
    @PostMapping("/borrow")
    ResponseEntity<String> borrowBook(@RequestBody BorrowReturnRequestDTO borrowReturnRequestDTO) {
        String response = borrowRecordService.borrowBook(borrowReturnRequestDTO);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Return a book")
    @PostMapping("/return")
    ResponseEntity<String> returnBook(@RequestBody BorrowReturnRequestDTO borrowReturnRequestDTO) {
        String response = borrowRecordService.returnBook(borrowReturnRequestDTO);
        return ResponseEntity.ok(response);
    }
}
