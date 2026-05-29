package com.lassriver.bookworm.repositories;

import com.lassriver.bookworm.entities.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    Page<Notification> findAllByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    Page<Notification> findAllByUserIdAndReadAtIsNullOrderByCreatedAtDesc(Long userId, Pageable pageable);

    long countByUserIdAndReadAtIsNull(Long userId);

    boolean existsByDedupeKey(String dedupeKey);

    Optional<Notification> findByIdAndUserId(Long id, Long userId);

    @Modifying
    @Query("""
            update Notification n
            set n.readAt = :readAt
            where n.user.id = :userId
              and n.readAt is null
            """)
    int markAllUnreadAsRead(@Param("userId") Long userId, @Param("readAt") LocalDateTime readAt);
}
