package com.lassriver.bookworm.services;

import com.lassriver.bookworm.dtos.response.NotificationResponse;
import com.lassriver.bookworm.dtos.response.PageResponse;
import com.lassriver.bookworm.dtos.response.UnreadNotificationsResponse;
import com.lassriver.bookworm.entities.Loan;
import com.lassriver.bookworm.entities.Reservation;
import org.springframework.data.domain.Pageable;

public interface NotificationService {
    PageResponse<NotificationResponse> getMyNotifications(String authenticatedEmail, String status, Pageable pageable);

    UnreadNotificationsResponse getUnreadCount(String authenticatedEmail);

    NotificationResponse markAsRead(Long notificationId, String authenticatedEmail);

    UnreadNotificationsResponse markAllAsRead(String authenticatedEmail);

    void notifyLoanCreated(Loan loan);

    void notifyLoanRenewed(Loan loan);

    void notifyLoanReturned(Loan loan);

    void notifyLoanDueSoon(Loan loan);

    void notifyLoanOverdue(Loan loan);

    void notifyReservationCreated(Reservation reservation);

    void notifyReservationCancelled(Reservation reservation);

    void notifyReservationFulfilled(Reservation reservation);
}
