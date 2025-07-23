package org.library.management.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class BorrowReturnRequestDTO {
    private Long bookId;
    private Long memberId;
}
