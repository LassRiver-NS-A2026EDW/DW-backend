package com.lassriver.bookworm.repositories;

import com.lassriver.bookworm.entities.LoanRenewal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoanRenewalRepository extends JpaRepository<LoanRenewal, Long> {

    List<LoanRenewal> findAllByLoanIdOrderByCreatedAtDesc(Long loanId);

    @Modifying
    @Query("""
            delete from LoanRenewal lr
            where lr.loan.id in (
                select l.id from Loan l where l.book.id = :bookId
            )
            """)
    int deleteAllByBookId(@Param("bookId") Long bookId);
}
