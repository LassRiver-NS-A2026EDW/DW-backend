package com.lassriver.bookworm.repositories;

import com.lassriver.bookworm.entities.BookCopy;
import com.lassriver.bookworm.entities.enums.BookCopyStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookCopyRepository extends JpaRepository<BookCopy, Long> {

    long countByBookId(Long bookId);

    long countByBookIdAndStatus(Long bookId, BookCopyStatus status);

    List<BookCopy> findAllByBookIdOrderByIdAsc(Long bookId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<BookCopy> findAllByBookIdAndStatusOrderByIdAsc(Long bookId, BookCopyStatus status);
}
