package com.lassriver.bookworm.repositories;

import com.lassriver.bookworm.entities.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<Book, Long>, JpaSpecificationExecutor<Book> {
    java.util.Optional<Book> findByIsbn(String isbn);
}
