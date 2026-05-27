package com.lassriver.bookworm.repositories;

import com.lassriver.bookworm.entities.LoanRenewal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoanRenewalRepository extends JpaRepository<LoanRenewal, Long> {

    List<LoanRenewal> findAllByLoanIdOrderByCreatedAtDesc(Long loanId);
}
