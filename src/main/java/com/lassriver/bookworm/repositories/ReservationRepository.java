package com.lassriver.bookworm.repositories;

import com.lassriver.bookworm.entities.Reservation;
import com.lassriver.bookworm.entities.enums.ReservationStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    boolean existsByUserIdAndBookIdAndStatus(Long userId, Long bookId, ReservationStatus status);

    boolean existsByBookIdAndStatus(Long bookId, ReservationStatus status);

    long countByBookIdAndStatus(Long bookId, ReservationStatus status);

    long countByBookIdAndStatusAndIdLessThan(Long bookId, ReservationStatus status, Long id);

    List<Reservation> findAllByUserIdOrderByCreatedAtDesc(Long userId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Reservation> findAllByBookIdAndStatusOrderByCreatedAtAsc(Long bookId, ReservationStatus status);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Reservation> findByIdAndStatus(Long id, ReservationStatus status);

    @Modifying
    @Query("delete from Reservation r where r.book.id = :bookId")
    int deleteAllByBookId(@Param("bookId") Long bookId);
}
