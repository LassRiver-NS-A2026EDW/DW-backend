package com.lassriver.bookworm.repositories;

import com.lassriver.bookworm.entities.Loan;
import com.lassriver.bookworm.entities.enums.LoanStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {
    boolean existsByBookIdAndStatus(Long bookId, LoanStatus status);

    long countByUserIdAndStatus(Long userId, LoanStatus status);

    long countByUserIdAndStatusIn(Long userId, Collection<LoanStatus> statuses);

    Optional<Loan> findByIdAndUserId(Long id, Long userId);

    List<Loan> findAllByUserIdOrderByLoanDateDesc(Long userId);

    Optional<Loan> findFirstByUserIdAndBookIdAndStatusAndReturnedAtAfterOrderByReturnedAtDesc(
            Long userId,
            Long bookId,
            LoanStatus status,
            java.time.LocalDateTime returnedAt);

    List<Loan> findAllByStatusInAndDueDateBetween(
            Collection<LoanStatus> statuses,
            java.time.LocalDateTime start,
            java.time.LocalDateTime end);

    List<Loan> findAllByStatusInAndDueDateBefore(
            Collection<LoanStatus> statuses,
            java.time.LocalDateTime dateTime);

    boolean existsByUserIdAndBookId(Long userId, Long bookId);

    boolean existsByUserIdAndBookIdAndStatus(Long userId, Long bookId, LoanStatus status);

    boolean existsByUserIdAndBookIdAndStatusIn(Long userId, Long bookId, Collection<LoanStatus> statuses);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select l from Loan l where l.id = :id")
    Optional<Loan> findLockedById(@Param("id") Long id);
}
