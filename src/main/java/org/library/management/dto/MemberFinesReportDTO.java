package org.library.management.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class MemberFinesReportDTO {
    private Long memberId;
    private String memberName;
    private BigDecimal totalFine;
    private List<BookLoansInfo> overdueBooks;
}
