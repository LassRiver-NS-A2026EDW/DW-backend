package com.lassriver.bookworm.services.impl;

import com.lassriver.bookworm.dtos.request.LoanCreateRequest;
import com.lassriver.bookworm.dtos.request.LoanRenewRequest;
import com.lassriver.bookworm.dtos.response.LoanRenewalResponse;
import com.lassriver.bookworm.dtos.response.LoanResponse;
import com.lassriver.bookworm.entities.Book;
import com.lassriver.bookworm.entities.BookCopy;
import com.lassriver.bookworm.entities.Loan;
import com.lassriver.bookworm.entities.LoanRenewal;
import com.lassriver.bookworm.entities.Reservation;
import com.lassriver.bookworm.entities.User;
import com.lassriver.bookworm.entities.enums.BookCopyStatus;
import com.lassriver.bookworm.entities.enums.LoanStatus;
import com.lassriver.bookworm.entities.enums.ReservationStatus;
import com.lassriver.bookworm.exceptions.BusinessRuleException;
import com.lassriver.bookworm.exceptions.ResourceNotFoundException;
import com.lassriver.bookworm.repositories.BookCopyRepository;
import com.lassriver.bookworm.repositories.BookRepository;
import com.lassriver.bookworm.repositories.LoanRenewalRepository;
import com.lassriver.bookworm.repositories.LoanRepository;
import com.lassriver.bookworm.repositories.ReservationRepository;
import com.lassriver.bookworm.repositories.UserRepository;
import com.lassriver.bookworm.services.LoanService;
import com.lassriver.bookworm.services.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LoanServiceImpl implements LoanService {

    private static final int MIN_LOAN_DURATION_MINUTES = 5;
    private static final int MAX_LOAN_DURATION_MINUTES = 10_080;
    private static final int MAX_ACTIVE_LOANS_PER_USER = 3;
    private static final int MAX_RENEWALS_PER_LOAN = 2;
    private static final List<LoanStatus> OPEN_LOAN_STATUSES = List.of(LoanStatus.ACTIVE, LoanStatus.OVERDUE);

    private final LoanRepository loanRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final BookCopyRepository bookCopyRepository;
    private final ReservationRepository reservationRepository;
    private final LoanRenewalRepository loanRenewalRepository;
    private final NotificationService notificationService;

    @Override
    @Transactional
    public LoanResponse createLoan(LoanCreateRequest request, String authenticatedEmail) {
        User user = getUserByEmail(authenticatedEmail);
        Book book = getLockedBook(request.getBookId());
        validateLoanDuration(request.getDurationMinutes());
        validateBookCanBeLoaned(book);
        validateUserCanOpenLoan(user, book);

        BookCopy copy = getAvailableCopy(book);
        copy.setStatus(BookCopyStatus.LOANED);

        LocalDateTime now = LocalDateTime.now();
        Loan loan = Loan.builder()
                .user(user)
                .book(book)
                .copy(copy)
                .status(LoanStatus.ACTIVE)
                .loanDate(now)
                .dueDate(now.plusMinutes(request.getDurationMinutes()))
                .renewalCount(0)
                .build();

        Loan savedLoan = loanRepository.save(loan);
        notificationService.notifyLoanCreated(savedLoan);
        return toResponse(savedLoan);
    }

    @Override
    @Transactional
    public LoanResponse returnLoan(Long loanId, String authenticatedEmail) {
        User user = getUserByEmail(authenticatedEmail);
        Loan loan = loanRepository.findLockedById(loanId)
                .orElseThrow(() -> new ResourceNotFoundException("Prestamo no encontrado con id: " + loanId));

        boolean isAdmin = isPrivileged(user);
        if (!isAdmin && !loan.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("No puedes devolver un prestamo de otro usuario.");
        }

        if (LoanStatus.RETURNED.equals(loan.getStatus())) {
            throw new BusinessRuleException("El prestamo ya fue devuelto.");
        }

        BookCopy returnedCopy = loan.getCopy();
        loan.setStatus(LoanStatus.RETURNED);
        loan.setReturnedAt(LocalDateTime.now());
        if (returnedCopy != null) {
            returnedCopy.setStatus(BookCopyStatus.AVAILABLE);
        }
        Loan savedLoan = loanRepository.save(loan);
        notificationService.notifyLoanReturned(savedLoan);
        LoanResponse response = toResponse(savedLoan);

        if (returnedCopy != null && "ACTIVE".equalsIgnoreCase(loan.getBook().getStatus())) {
            fulfillNextReservation(loan.getBook(), returnedCopy);
        }

        return response;
    }

    @Override
    @Transactional
    public LoanResponse renewLoan(Long loanId, LoanRenewRequest request, String authenticatedEmail) {
        User user = getUserByEmail(authenticatedEmail);
        Loan loan = loanRepository.findLockedById(loanId)
                .orElseThrow(() -> new ResourceNotFoundException("Prestamo no encontrado con id: " + loanId));

        boolean isAdmin = isPrivileged(user);
        if (!isAdmin && !loan.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("No puedes renovar un prestamo de otro usuario.");
        }

        validateLoanDuration(request.getDurationMinutes());
        String blockedReason = getRenewalBlockedReason(loan);
        if (blockedReason != null) {
            throw new BusinessRuleException(blockedReason);
        }

        LocalDateTime previousDueDate = loan.getDueDate();
        LocalDateTime newDueDate = previousDueDate.plusMinutes(request.getDurationMinutes());
        loan.setDueDate(newDueDate);
        loan.setRenewalCount(loan.getRenewalCount() + 1);

        LoanRenewal renewal = LoanRenewal.builder()
                .loan(loan)
                .previousDueDate(previousDueDate)
                .newDueDate(newDueDate)
                .durationMinutes(request.getDurationMinutes())
                .build();
        loanRenewalRepository.save(renewal);

        Loan savedLoan = loanRepository.save(loan);
        notificationService.notifyLoanRenewed(savedLoan);
        return toResponse(savedLoan);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LoanRenewalResponse> getLoanHistory(Long loanId, String authenticatedEmail) {
        User user = getUserByEmail(authenticatedEmail);
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new ResourceNotFoundException("Prestamo no encontrado con id: " + loanId));

        if (!isPrivileged(user) && !loan.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("No puedes consultar el historial de otro usuario.");
        }

        return loanRenewalRepository.findAllByLoanIdOrderByCreatedAtDesc(loanId)
                .stream()
                .map(this::toRenewalResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<LoanResponse> getMyLoans(String authenticatedEmail) {
        User user = getUserByEmail(authenticatedEmail);

        return loanRepository.findAllByUserIdOrderByLoanDateDesc(user.getId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<LoanResponse> getAllLoans() {
        return loanRepository.findAll().stream()
                .sorted(Comparator.comparing(Loan::getLoanDate, Comparator.nullsLast(Comparator.reverseOrder())))
                .map(this::toResponse)
                .toList();
    }

    private Book getLockedBook(Long bookId) {
        return bookRepository.findLockedById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Libro no encontrado con id: " + bookId));
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

    private void validateBookCanBeLoaned(Book book) {
        if (!"ACTIVE".equalsIgnoreCase(book.getStatus())) {
            throw new BusinessRuleException("No se puede prestar un libro inactivo.");
        }
    }

    private void validateUserCanOpenLoan(User user, Book book) {
        if (loanRepository.existsByUserIdAndBookIdAndStatusIn(user.getId(), book.getId(), OPEN_LOAN_STATUSES)) {
            throw new BusinessRuleException("Ya tienes un prestamo activo para este libro.");
        }

        long openLoans = loanRepository.countByUserIdAndStatusIn(user.getId(), OPEN_LOAN_STATUSES);
        if (openLoans >= MAX_ACTIVE_LOANS_PER_USER) {
            throw new BusinessRuleException("Has alcanzado el limite de prestamos activos.");
        }
    }

    private BookCopy getAvailableCopy(Book book) {
        List<BookCopy> availableCopies = bookCopyRepository.findAllByBookIdAndStatusOrderByIdAsc(
                book.getId(), BookCopyStatus.AVAILABLE);
        if (availableCopies.isEmpty()) {
            throw new BusinessRuleException("No hay ejemplares disponibles. Puedes unirte a la cola de reservas.");
        }
        return availableCopies.getFirst();
    }

    private void fulfillNextReservation(Book book, BookCopy copy) {
        List<Reservation> waitingReservations = reservationRepository.findAllByBookIdAndStatusOrderByCreatedAtAsc(
                book.getId(), ReservationStatus.WAITING);
        if (waitingReservations.isEmpty()) {
            return;
        }

        Reservation reservation = waitingReservations.getFirst();
        if (loanRepository.countByUserIdAndStatusIn(reservation.getUser().getId(), OPEN_LOAN_STATUSES)
                >= MAX_ACTIVE_LOANS_PER_USER) {
            return;
        }
        if (loanRepository.existsByUserIdAndBookIdAndStatusIn(
                reservation.getUser().getId(), book.getId(), OPEN_LOAN_STATUSES)) {
            return;
        }

        copy.setStatus(BookCopyStatus.LOANED);
        LocalDateTime now = LocalDateTime.now();
        Loan assignedLoan = Loan.builder()
                .user(reservation.getUser())
                .book(book)
                .copy(copy)
                .status(LoanStatus.ACTIVE)
                .loanDate(now)
                .dueDate(now.plusMinutes(reservation.getRequestedLoanDurationMinutes()))
                .renewalCount(0)
                .build();
        Loan savedLoan = loanRepository.save(assignedLoan);

        reservation.setStatus(ReservationStatus.FULFILLED);
        reservation.setFulfilledAt(now);
        reservation.setFulfilledLoan(savedLoan);
        Reservation fulfilledReservation = reservationRepository.save(reservation);
        notificationService.notifyReservationFulfilled(fulfilledReservation);
    }

    private String getRenewalBlockedReason(Loan loan) {
        if (LoanStatus.RETURNED.equals(loan.getStatus())) {
            return "No se puede renovar un prestamo devuelto.";
        }
        if (loan.getDueDate() != null && loan.getDueDate().isBefore(LocalDateTime.now())) {
            return "No se puede renovar un prestamo vencido.";
        }
        if (loan.getRenewalCount() != null && loan.getRenewalCount() >= MAX_RENEWALS_PER_LOAN) {
            return "El prestamo ya alcanzo el limite de renovaciones.";
        }
        if (reservationRepository.existsByBookIdAndStatus(loan.getBook().getId(), ReservationStatus.WAITING)) {
            return "No se puede renovar porque hay usuarios esperando este libro.";
        }
        return null;
    }

    private LoanStatus getEffectiveStatus(Loan loan) {
        if (LoanStatus.RETURNED.equals(loan.getStatus())) {
            return LoanStatus.RETURNED;
        }
        if (loan.getDueDate() != null && loan.getDueDate().isBefore(LocalDateTime.now())) {
            return LoanStatus.OVERDUE;
        }
        return loan.getStatus() == null ? LoanStatus.ACTIVE : loan.getStatus();
    }

    private boolean isPrivileged(User user) {
        return "ADMIN".equalsIgnoreCase(user.getRole()) || "LIBRARIAN".equalsIgnoreCase(user.getRole());
    }

    private LoanResponse toResponse(Loan loan) {
        String blockedReason = getRenewalBlockedReason(loan);
        BookCopy copy = loan.getCopy();
        return LoanResponse.builder()
                .id(loan.getId())
                .userId(loan.getUser().getId())
                .userEmail(loan.getUser().getEmail())
                .bookId(loan.getBook().getId())
                .bookTitle(loan.getBook().getTitle())
                .bookIsbn(loan.getBook().getIsbn())
                .copyId(copy == null ? null : copy.getId())
                .copyCode(copy == null ? null : copy.getCopyCode())
                .loanDate(loan.getLoanDate())
                .dueDate(loan.getDueDate())
                .returnedAt(loan.getReturnedAt())
                .status(getEffectiveStatus(loan).name())
                .renewalCount(loan.getRenewalCount() == null ? 0 : loan.getRenewalCount())
                .canRenew(blockedReason == null)
                .blockedReason(blockedReason)
                .createdAt(loan.getCreatedAt())
                .build();
    }

    private LoanRenewalResponse toRenewalResponse(LoanRenewal renewal) {
        return LoanRenewalResponse.builder()
                .id(renewal.getId())
                .loanId(renewal.getLoan().getId())
                .previousDueDate(renewal.getPreviousDueDate())
                .newDueDate(renewal.getNewDueDate())
                .durationMinutes(renewal.getDurationMinutes())
                .createdAt(renewal.getCreatedAt())
                .build();
    }
}
