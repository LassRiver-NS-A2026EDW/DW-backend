package com.lassriver.bookworm.services;

import com.lassriver.bookworm.dtos.request.LoanCreateRequest;
import com.lassriver.bookworm.dtos.request.LoanRenewRequest;
import com.lassriver.bookworm.dtos.response.LoanRenewalResponse;
import com.lassriver.bookworm.dtos.response.LoanResponse;

import java.util.List;

public interface LoanService {
    LoanResponse createLoan(LoanCreateRequest request, String authenticatedEmail);

    LoanResponse returnLoan(Long loanId, String authenticatedEmail);

    LoanResponse renewLoan(Long loanId, LoanRenewRequest request, String authenticatedEmail);

    List<LoanRenewalResponse> getLoanHistory(Long loanId, String authenticatedEmail);

    List<LoanResponse> getMyLoans(String authenticatedEmail);

    List<LoanResponse> getAllLoans();
}
