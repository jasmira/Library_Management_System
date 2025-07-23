package org.library.management.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.library.management.dto.LoansResponseDTO;
import org.library.management.dto.MemberFinesReportDTO;
import org.library.management.dto.MemberRequestDTO;
import org.library.management.entity.Member;
import org.library.management.service.MemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/members")
@Tag(name = "Members", description = "Manage library registered members")
public class MemberController {
    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @Operation(summary = "Register/Add a new member")
    @PostMapping
    ResponseEntity<String> addMember(@RequestBody MemberRequestDTO memberRequestDTO) {
        String response = memberService.addMember(memberRequestDTO);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get all registered members")
    @GetMapping
    ResponseEntity<List<Member>> getAllMembers() {
        List<Member> memberList = memberService.getAllMembers();
        return ResponseEntity.ok(memberList);
    }

    @Operation(summary = "Get a registered member by memberId")
    @GetMapping("/{id}")
    ResponseEntity<Member> getMember(@PathVariable Long id) {
        Member member = memberService.getMember(id);
        return ResponseEntity.ok(member);
    }

    @Operation(summary = "Get a member’s loan history & fines")
    @GetMapping("/{id}/loans")
    ResponseEntity<LoansResponseDTO> getLoansForMember(@PathVariable Long id) {
        LoansResponseDTO loansResponseDTO = memberService.getLoansForMember(id);
        return ResponseEntity.ok(loansResponseDTO);
    }

    @Operation(summary = "Get all members with pending fines")
    @GetMapping("/reports/dues")
    ResponseEntity<List<MemberFinesReportDTO>> getMembersWithPendingFines() {
        List<MemberFinesReportDTO> memberFinesReportDTOList = memberService.getMembersWithPendingFines();
        return ResponseEntity.ok(memberFinesReportDTOList);
    }
}
