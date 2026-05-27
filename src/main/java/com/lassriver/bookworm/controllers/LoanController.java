package com.lassriver.bookworm.controllers;

import com.lassriver.bookworm.dtos.request.LoanCreateRequest;
import com.lassriver.bookworm.dtos.request.LoanRenewRequest;
import com.lassriver.bookworm.dtos.response.LoanRenewalResponse;
import com.lassriver.bookworm.dtos.response.LoanResponse;
import com.lassriver.bookworm.services.LoanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
public class LoanController {

    private final LoanService loanService;

    @GetMapping
    public ResponseEntity<List<LoanResponse>> getAllLoans() {
        return ResponseEntity.ok(loanService.getAllLoans());
    }

    @PostMapping
    public ResponseEntity<LoanResponse> createLoan(
            @Valid @RequestBody LoanCreateRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        LoanResponse response = loanService.createLoan(request, userDetails.getUsername());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}/return")
    public ResponseEntity<LoanResponse> returnLoan(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(loanService.returnLoan(id, userDetails.getUsername()));
    }

    @PostMapping("/{id}/renew")
    public ResponseEntity<LoanResponse> renewLoan(
            @PathVariable Long id,
            @Valid @RequestBody LoanRenewRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(loanService.renewLoan(id, request, userDetails.getUsername()));
    }

    @GetMapping("/{id}/history")
    public ResponseEntity<List<LoanRenewalResponse>> getLoanHistory(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(loanService.getLoanHistory(id, userDetails.getUsername()));
    }

    @GetMapping("/my-loans")
    public ResponseEntity<List<LoanResponse>> getMyLoans(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(loanService.getMyLoans(userDetails.getUsername()));
    }
}
