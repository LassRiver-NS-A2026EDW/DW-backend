package com.lassriver.bookworm.repositories;

import com.lassriver.bookworm.entities.BookCopy;
import com.lassriver.bookworm.entities.enums.BookCopyStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookCopyRepository extends JpaRepository<BookCopy, Long> {

    long countByBookId(Long bookId);

    long countByBookIdAndStatusIn(Long bookId, Collection<BookCopyStatus> statuses);

    long countByBookIdAndStatus(Long bookId, BookCopyStatus status);

    List<BookCopy> findAllByBookIdOrderByIdAsc(Long bookId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<BookCopy> findByIdAndBookId(Long id, Long bookId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<BookCopy> findAllByBookIdAndStatusOrderByIdAsc(Long bookId, BookCopyStatus status);
}
