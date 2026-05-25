package com.lassriver.bookworm.repositories;

import com.lassriver.bookworm.entities.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {
    boolean existsByBookIdAndStatus(Long bookId, String status);

    long countByUserIdAndStatus(Long userId, String status);

    Optional<Loan> findByIdAndUserId(Long id, Long userId);

    List<Loan> findAllByUserIdOrderByLoanDateDesc(Long userId);

    boolean existsByUserIdAndBookId(Long userId, Long bookId);

    boolean existsByUserIdAndBookIdAndStatus(Long userId, Long bookId, String status);
}
