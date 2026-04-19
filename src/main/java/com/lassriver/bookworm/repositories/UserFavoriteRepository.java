package com.lassriver.bookworm.repositories;

import com.lassriver.bookworm.entities.UserFavorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserFavoriteRepository extends JpaRepository<UserFavorite, Long> {
    Optional<UserFavorite> findByUserIdAndBookId(Long userId, Long bookId);

    List<UserFavorite> findAllByUserIdOrderByCreatedAtDesc(Long userId);
}
