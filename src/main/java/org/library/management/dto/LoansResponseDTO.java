package org.library.management.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class LoansResponseDTO {
    private Long memberId;
    private List<BookLoansInfo> bookLoansInfoList;
}
