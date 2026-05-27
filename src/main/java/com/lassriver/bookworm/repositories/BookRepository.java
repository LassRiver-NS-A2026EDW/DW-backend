package com.lassriver.bookworm.repositories;

import com.lassriver.bookworm.entities.Book;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long>, JpaSpecificationExecutor<Book> {
    java.util.Optional<Book> findByIsbn(String isbn);

    @Query("select distinct b.category from Book b where b.category is not null and b.category <> '' order by b.category")
    List<String> findDistinctCategories();

    @Query("select distinct b.language from Book b where b.language is not null and b.language <> '' order by b.language")
    List<String> findDistinctLanguages();

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select b from Book b where b.id = :id")
    java.util.Optional<Book> findLockedById(@Param("id") Long id);
}
