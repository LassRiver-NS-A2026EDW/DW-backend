package com.lassriver.bookworm.services.impl;

import com.lassriver.bookworm.dtos.response.NotificationResponse;
import com.lassriver.bookworm.dtos.response.PageResponse;
import com.lassriver.bookworm.dtos.response.UnreadNotificationsResponse;
import com.lassriver.bookworm.entities.Loan;
import com.lassriver.bookworm.entities.Notification;
import com.lassriver.bookworm.entities.Reservation;
import com.lassriver.bookworm.entities.User;
import com.lassriver.bookworm.entities.enums.NotificationType;
import com.lassriver.bookworm.exceptions.BusinessRuleException;
import com.lassriver.bookworm.exceptions.ResourceNotFoundException;
import com.lassriver.bookworm.repositories.NotificationRepository;
import com.lassriver.bookworm.repositories.UserRepository;
import com.lassriver.bookworm.services.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private static final String TARGET_LOANS = "loans";
    private static final String TARGET_RESERVATIONS = "reservations";
    private static final List<String> STAFF_ROLES = List.of("ADMIN", "LIBRARIAN");

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public PageResponse<NotificationResponse> getMyNotifications(
            String authenticatedEmail,
            String status,
            Pageable pageable) {
        User user = getUserByEmail(authenticatedEmail);
        String normalizedStatus = status == null ? "all" : status.trim().toLowerCase();

        Page<Notification> page = switch (normalizedStatus) {
            case "all" -> notificationRepository.findAllByUserIdOrderByCreatedAtDesc(user.getId(), pageable);
            case "unread" -> notificationRepository.findAllByUserIdAndReadAtIsNullOrderByCreatedAtDesc(
                    user.getId(), pageable);
            default -> throw new BusinessRuleException("El filtro de notificaciones debe ser all o unread.");
        };

        return PageResponse.from(page.map(this::toResponse));
    }

    @Override
    @Transactional(readOnly = true)
    public UnreadNotificationsResponse getUnreadCount(String authenticatedEmail) {
        User user = getUserByEmail(authenticatedEmail);
        return new UnreadNotificationsResponse(notificationRepository.countByUserIdAndReadAtIsNull(user.getId()));
    }

    @Override
    @Transactional
    public NotificationResponse markAsRead(Long notificationId, String authenticatedEmail) {
        User user = getUserByEmail(authenticatedEmail);
        Notification notification = notificationRepository.findByIdAndUserId(notificationId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Notificacion no encontrada con id: " + notificationId));

        if (notification.getReadAt() == null) {
            notification.setReadAt(LocalDateTime.now());
        }
        return toResponse(notificationRepository.save(notification));
    }

    @Override
    @Transactional
    public UnreadNotificationsResponse markAllAsRead(String authenticatedEmail) {
        User user = getUserByEmail(authenticatedEmail);
        notificationRepository.markAllUnreadAsRead(user.getId(), LocalDateTime.now());
        return new UnreadNotificationsResponse(0);
    }

    @Override
    @Transactional
    public void notifyLoanCreated(Loan loan) {
        createOnceForLoan(
                loan,
                NotificationType.LOAN_CREATED,
                "Prestamo creado",
                "Tu prestamo de \"" + loan.getBook().getTitle()
                        + "\" fue creado. Vence el " + formatDate(loan.getDueDate()) + ".",
                "LOAN_CREATED:" + loan.getId());
        createOnceForStaff(
                loan.getUser(),
                NotificationType.LOAN_CREATED,
                "Nuevo prestamo",
                loan.getUser().getName() + " tomo prestado \"" + loan.getBook().getTitle()
                        + "\" hasta el " + formatDate(loan.getDueDate()) + ".",
                TARGET_LOANS,
                loan.getId(),
                "STAFF:LOAN_CREATED:" + loan.getId());
    }

    @Override
    @Transactional
    public void notifyLoanRenewed(Loan loan) {
        createOnceForLoan(
                loan,
                NotificationType.LOAN_RENEWED,
                "Prestamo renovado",
                "Tu prestamo de \"" + loan.getBook().getTitle()
                        + "\" fue renovado hasta el " + formatDate(loan.getDueDate()) + ".",
                "LOAN_RENEWED:" + loan.getId() + ":" + loan.getDueDate());
        createOnceForStaff(
                loan.getUser(),
                NotificationType.LOAN_RENEWED,
                "Prestamo renovado",
                loan.getUser().getName() + " renovo \"" + loan.getBook().getTitle()
                        + "\" hasta el " + formatDate(loan.getDueDate()) + ".",
                TARGET_LOANS,
                loan.getId(),
                "STAFF:LOAN_RENEWED:" + loan.getId() + ":" + loan.getDueDate());
    }

    @Override
    @Transactional
    public void notifyLoanReturned(Loan loan) {
        createOnceForLoan(
                loan,
                NotificationType.LOAN_RETURNED,
                "Prestamo devuelto",
                "Confirmamos la devolucion de \"" + loan.getBook().getTitle() + "\".",
                "LOAN_RETURNED:" + loan.getId());
        createOnceForStaff(
                loan.getUser(),
                NotificationType.LOAN_RETURNED,
                "Prestamo devuelto",
                loan.getUser().getName() + " devolvio \"" + loan.getBook().getTitle() + "\".",
                TARGET_LOANS,
                loan.getId(),
                "STAFF:LOAN_RETURNED:" + loan.getId());
    }

    @Override
    @Transactional
    public void notifyLoanDueSoon(Loan loan) {
        createOnceForLoan(
                loan,
                NotificationType.LOAN_DUE_SOON,
                "Prestamo por vencer",
                "Tu prestamo de \"" + loan.getBook().getTitle()
                        + "\" vence pronto: " + formatDate(loan.getDueDate()) + ".",
                "LOAN_DUE_SOON:" + loan.getId() + ":" + loan.getDueDate());
        createOnceForStaff(
                loan.getUser(),
                NotificationType.LOAN_DUE_SOON,
                "Prestamo por vencer",
                "El prestamo de " + loan.getUser().getName() + " para \""
                        + loan.getBook().getTitle() + "\" vence el " + formatDate(loan.getDueDate()) + ".",
                TARGET_LOANS,
                loan.getId(),
                "STAFF:LOAN_DUE_SOON:" + loan.getId() + ":" + loan.getDueDate());
    }

    @Override
    @Transactional
    public void notifyLoanOverdue(Loan loan) {
        createOnceForLoan(
                loan,
                NotificationType.LOAN_OVERDUE,
                "Prestamo vencido",
                "Tu prestamo de \"" + loan.getBook().getTitle() + "\" ya vencio. Por favor devuelvelo cuanto antes.",
                "LOAN_OVERDUE:" + loan.getId());
        createOnceForStaff(
                loan.getUser(),
                NotificationType.LOAN_OVERDUE,
                "Prestamo vencido",
                "El prestamo de " + loan.getUser().getName() + " para \""
                        + loan.getBook().getTitle() + "\" esta vencido.",
                TARGET_LOANS,
                loan.getId(),
                "STAFF:LOAN_OVERDUE:" + loan.getId());
    }

    @Override
    @Transactional
    public void notifyReservationCreated(Reservation reservation) {
        createOnceForReservation(
                reservation,
                NotificationType.RESERVATION_CREATED,
                "Reserva creada",
                "Tu reserva de \"" + reservation.getBook().getTitle() + "\" quedo en cola.",
                TARGET_RESERVATIONS,
                reservation.getId(),
                "RESERVATION_CREATED:" + reservation.getId());
        createOnceForStaff(
                reservation.getUser(),
                NotificationType.RESERVATION_CREATED,
                "Nueva reserva",
                reservation.getUser().getName() + " reservo \"" + reservation.getBook().getTitle() + "\".",
                TARGET_RESERVATIONS,
                reservation.getId(),
                "STAFF:RESERVATION_CREATED:" + reservation.getId());
    }

    @Override
    @Transactional
    public void notifyReservationCancelled(Reservation reservation) {
        createOnceForReservation(
                reservation,
                NotificationType.RESERVATION_CANCELLED,
                "Reserva cancelada",
                "Cancelaste tu reserva de \"" + reservation.getBook().getTitle() + "\".",
                TARGET_RESERVATIONS,
                reservation.getId(),
                "RESERVATION_CANCELLED:" + reservation.getId());
        createOnceForStaff(
                reservation.getUser(),
                NotificationType.RESERVATION_CANCELLED,
                "Reserva cancelada",
                reservation.getUser().getName() + " cancelo la reserva de \""
                        + reservation.getBook().getTitle() + "\".",
                TARGET_RESERVATIONS,
                reservation.getId(),
                "STAFF:RESERVATION_CANCELLED:" + reservation.getId());
    }

    @Override
    @Transactional
    public void notifyReservationFulfilled(Reservation reservation) {
        Long fulfilledLoanId = reservation.getFulfilledLoan() == null ? null : reservation.getFulfilledLoan().getId();
        createOnceForReservation(
                reservation,
                NotificationType.RESERVATION_FULFILLED,
                "Reserva asignada",
                "Tu reserva de \"" + reservation.getBook().getTitle() + "\" ya se convirtio en prestamo.",
                TARGET_LOANS,
                fulfilledLoanId,
                "RESERVATION_FULFILLED:" + reservation.getId() + ":" + fulfilledLoanId);
        createOnceForStaff(
                reservation.getUser(),
                NotificationType.RESERVATION_FULFILLED,
                "Reserva asignada",
                "La reserva de " + reservation.getUser().getName() + " para \""
                        + reservation.getBook().getTitle() + "\" se convirtio en prestamo.",
                TARGET_LOANS,
                fulfilledLoanId,
                "STAFF:RESERVATION_FULFILLED:" + reservation.getId() + ":" + fulfilledLoanId);
    }

    private void createOnceForLoan(
            Loan loan,
            NotificationType type,
            String title,
            String message,
            String dedupeKey) {
        createOnce(loan.getUser(), type, title, message, TARGET_LOANS, loan.getId(), dedupeKey);
    }

    private void createOnceForReservation(
            Reservation reservation,
            NotificationType type,
            String title,
            String message,
            String targetView,
            Long targetId,
            String dedupeKey) {
        createOnce(reservation.getUser(), type, title, message, targetView, targetId, dedupeKey);
    }

    private void createOnce(
            User user,
            NotificationType type,
            String title,
            String message,
            String targetView,
            Long targetId,
            String dedupeKey) {
        if (dedupeKey != null && notificationRepository.existsByDedupeKey(dedupeKey)) {
            return;
        }

        Notification notification = Notification.builder()
                .user(user)
                .type(type)
                .title(title)
                .message(message)
                .targetView(targetView)
                .targetId(targetId)
                .dedupeKey(dedupeKey)
                .build();
        notificationRepository.save(notification);
    }

    private void createOnceForStaff(
            User subjectUser,
            NotificationType type,
            String title,
            String message,
            String targetView,
            Long targetId,
            String dedupePrefix) {
        for (User staffUser : userRepository.findAllByRoleUpperIn(STAFF_ROLES)) {
            if (staffUser.getId().equals(subjectUser.getId())) {
                continue;
            }
            createOnce(
                    staffUser,
                    type,
                    title,
                    message,
                    targetView,
                    targetId,
                    dedupePrefix + ":" + staffUser.getId());
        }
    }

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado para el token actual."));
    }

    private NotificationResponse toResponse(Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .type(notification.getType().name())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .targetView(notification.getTargetView())
                .targetId(notification.getTargetId())
                .readAt(notification.getReadAt())
                .createdAt(notification.getCreatedAt())
                .build();
    }

    private String formatDate(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "sin fecha";
        }
        String normalized = dateTime.toString().replace("T", " ");
        return normalized.length() > 16 ? normalized.substring(0, 16) : normalized;
    }
}
