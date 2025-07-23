package org.library.management.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
public class MemberRequestDTO {
    private String name;
    private String email;
    private LocalDate joinDate;
}
