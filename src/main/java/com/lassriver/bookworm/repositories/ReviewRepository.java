package com.lassriver.bookworm.repositories;

import com.lassriver.bookworm.entities.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    boolean existsByUserIdAndBookId(Long userId, Long bookId);

    long countByBookIdAndStatus(Long bookId, String status);

    @Query("select avg(r.rating) from Review r where r.book.id = :bookId and r.status = :status")
    Double averageRatingByBookIdAndStatus(@Param("bookId") Long bookId, @Param("status") String status);

    List<Review> findAllByBookIdAndStatusOrderByCreatedAtDesc(Long bookId, String status);

    List<Review> findAllByStatusOrderByCreatedAtDesc(String status);

    List<Review> findAllByOrderByCreatedAtDesc();

    @Modifying
    @Query("delete from Review r where r.book.id = :bookId")
    int deleteAllByBookId(@Param("bookId") Long bookId);
}
