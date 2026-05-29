package com.lassriver.bookworm.repositories;

import com.lassriver.bookworm.entities.UserFavorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserFavoriteRepository extends JpaRepository<UserFavorite, Long> {
    Optional<UserFavorite> findByUserIdAndBookId(Long userId, Long bookId);

    List<UserFavorite> findAllByUserIdOrderByCreatedAtDesc(Long userId);

    @Modifying
    @Query("delete from UserFavorite f where f.book.id = :bookId")
    int deleteAllByBookId(@Param("bookId") Long bookId);
}
