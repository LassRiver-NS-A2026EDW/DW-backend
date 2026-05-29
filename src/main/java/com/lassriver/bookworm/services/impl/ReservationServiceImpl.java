package com.lassriver.bookworm.services.impl;

import com.lassriver.bookworm.dtos.request.ReservationCreateRequest;
import com.lassriver.bookworm.dtos.response.ReservationResponse;
import com.lassriver.bookworm.entities.Book;
import com.lassriver.bookworm.entities.Reservation;
import com.lassriver.bookworm.entities.User;
import com.lassriver.bookworm.entities.enums.BookCopyStatus;
import com.lassriver.bookworm.entities.enums.LoanStatus;
import com.lassriver.bookworm.entities.enums.ReservationStatus;
import com.lassriver.bookworm.exceptions.BusinessRuleException;
import com.lassriver.bookworm.exceptions.ResourceNotFoundException;
import com.lassriver.bookworm.repositories.BookCopyRepository;
import com.lassriver.bookworm.repositories.BookRepository;
import com.lassriver.bookworm.repositories.LoanRepository;
import com.lassriver.bookworm.repositories.ReservationRepository;
import com.lassriver.bookworm.repositories.UserRepository;
import com.lassriver.bookworm.services.NotificationService;
import com.lassriver.bookworm.services.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {

    private static final int MIN_LOAN_DURATION_MINUTES = 5;
    private static final int MAX_LOAN_DURATION_MINUTES = 10_080;
    private static final int LOAN_COOLDOWN_HOURS = 24;
    private static final List<LoanStatus> OPEN_LOAN_STATUSES = List.of(LoanStatus.ACTIVE, LoanStatus.OVERDUE);

    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final BookCopyRepository bookCopyRepository;
    private final LoanRepository loanRepository;
    private final NotificationService notificationService;

    @Override
    @Transactional
    public ReservationResponse createReservation(ReservationCreateRequest request, String authenticatedEmail) {
        User user = getUserByEmail(authenticatedEmail);
        Book book = bookRepository.findLockedById(request.getBookId())
                .orElseThrow(() -> new ResourceNotFoundException("Libro no encontrado con id: " + request.getBookId()));
        validateLoanDuration(request.getRequestedLoanDurationMinutes());

        if (!"ACTIVE".equalsIgnoreCase(book.getStatus())) {
            throw new BusinessRuleException("No se puede reservar un libro inactivo.");
        }
        if (bookCopyRepository.countByBookIdAndStatus(book.getId(), BookCopyStatus.AVAILABLE) > 0) {
            throw new BusinessRuleException("Hay ejemplares disponibles. Crea un prestamo directamente.");
        }
        if (loanRepository.existsByUserIdAndBookIdAndStatusIn(user.getId(), book.getId(), OPEN_LOAN_STATUSES)) {
            throw new BusinessRuleException("Ya tienes un prestamo activo para este libro.");
        }
        if (isLoanCooldownActive(user, book)) {
            throw new BusinessRuleException("Debes esperar 24 horas despues de devolver este libro para volver a reservarlo.");
        }
        if (reservationRepository.existsByUserIdAndBookIdAndStatus(user.getId(), book.getId(), ReservationStatus.WAITING)) {
            throw new BusinessRuleException("Ya tienes una reserva en cola para este libro.");
        }

        Reservation reservation = Reservation.builder()
                .user(user)
                .book(book)
                .status(ReservationStatus.WAITING)
                .requestedLoanDurationMinutes(request.getRequestedLoanDurationMinutes())
                .build();

        Reservation savedReservation = reservationRepository.save(reservation);
        notificationService.notifyReservationCreated(savedReservation);
        return toResponse(savedReservation);
    }

    @Override
    @Transactional
    public ReservationResponse cancelReservation(Long reservationId, String authenticatedEmail) {
        User user = getUserByEmail(authenticatedEmail);
        Reservation reservation = reservationRepository.findByIdAndStatus(reservationId, ReservationStatus.WAITING)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva pendiente no encontrada con id: " + reservationId));

        if (!isPrivileged(user) && !reservation.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("No puedes cancelar una reserva de otro usuario.");
        }

        reservation.setStatus(ReservationStatus.CANCELLED);
        reservation.setCancelledAt(LocalDateTime.now());
        Reservation savedReservation = reservationRepository.save(reservation);
        notificationService.notifyReservationCancelled(savedReservation);
        return toResponse(savedReservation);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReservationResponse> getMyReservations(String authenticatedEmail) {
        User user = getUserByEmail(authenticatedEmail);
        return reservationRepository.findAllByUserIdOrderByCreatedAtDesc(user.getId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado para el token actual."));
    }

    private void validateLoanDuration(Integer durationMinutes) {
        if (durationMinutes == null
                || durationMinutes < MIN_LOAN_DURATION_MINUTES
                || durationMinutes > MAX_LOAN_DURATION_MINUTES) {
            throw new BusinessRuleException("La duracion del prestamo debe estar entre 5 minutos y 7 dias.");
        }
    }

    private boolean isPrivileged(User user) {
        return "ADMIN".equalsIgnoreCase(user.getRole()) || "LIBRARIAN".equalsIgnoreCase(user.getRole());
    }

    private boolean isLoanCooldownActive(User user, Book book) {
        LocalDateTime returnedAfter = LocalDateTime.now().minusHours(LOAN_COOLDOWN_HOURS);
        return loanRepository.findFirstByUserIdAndBookIdAndStatusAndReturnedAtAfterOrderByReturnedAtDesc(
                user.getId(),
                book.getId(),
                LoanStatus.RETURNED,
                returnedAfter).isPresent();
    }

    private ReservationResponse toResponse(Reservation reservation) {
        Integer queuePosition = null;
        if (ReservationStatus.WAITING.equals(reservation.getStatus())) {
            queuePosition = Math.toIntExact(
                    reservationRepository.countByBookIdAndStatusAndIdLessThan(
                            reservation.getBook().getId(), ReservationStatus.WAITING, reservation.getId()) + 1);
        }

        return ReservationResponse.builder()
                .id(reservation.getId())
                .userId(reservation.getUser().getId())
                .userEmail(reservation.getUser().getEmail())
                .bookId(reservation.getBook().getId())
                .bookTitle(reservation.getBook().getTitle())
                .status(reservation.getStatus().name())
                .requestedLoanDurationMinutes(reservation.getRequestedLoanDurationMinutes())
                .queuePosition(queuePosition)
                .fulfilledLoanId(reservation.getFulfilledLoan() == null ? null : reservation.getFulfilledLoan().getId())
                .createdAt(reservation.getCreatedAt())
                .fulfilledAt(reservation.getFulfilledAt())
                .cancelledAt(reservation.getCancelledAt())
                .build();
    }
}
