package com.lassriver.bookworm.services.impl;

import com.lassriver.bookworm.dtos.request.LoanCreateRequest;
import com.lassriver.bookworm.dtos.response.LoanResponse;
import com.lassriver.bookworm.entities.Book;
import com.lassriver.bookworm.entities.Loan;
import com.lassriver.bookworm.entities.User;
import com.lassriver.bookworm.exceptions.BusinessRuleException;
import com.lassriver.bookworm.exceptions.ResourceNotFoundException;
import com.lassriver.bookworm.repositories.BookRepository;
import com.lassriver.bookworm.repositories.LoanRepository;
import com.lassriver.bookworm.repositories.UserRepository;
import com.lassriver.bookworm.services.LoanService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LoanServiceImpl implements LoanService {

    private static final String LOAN_STATUS_ACTIVE = "ACTIVE";
    private static final String LOAN_STATUS_RETURNED = "RETURNED";
    private static final int MAX_ACTIVE_LOANS_PER_USER = 3;

    private final LoanRepository loanRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public LoanResponse createLoan(LoanCreateRequest request, String authenticatedEmail) {
        User user = getUserByEmail(authenticatedEmail);
        Book book = bookRepository.findById(request.getBookId())
                .orElseThrow(() -> new ResourceNotFoundException("Libro no encontrado con id: " + request.getBookId()));

        if (!"ACTIVE".equalsIgnoreCase(book.getStatus())) {
            throw new BusinessRuleException("No se puede prestar un libro inactivo.");
        }

        if (loanRepository.existsByBookIdAndStatus(book.getId(), LOAN_STATUS_ACTIVE)) {
            throw new BusinessRuleException("El libro no está disponible actualmente.");
        }

        long activeLoans = loanRepository.countByUserIdAndStatus(user.getId(), LOAN_STATUS_ACTIVE);
        if (activeLoans >= MAX_ACTIVE_LOANS_PER_USER) {
            throw new BusinessRuleException("Has alcanzado el límite de préstamos activos.");
        }

        Loan loan = Loan.builder()
                .user(user)
                .book(book)
                .status(LOAN_STATUS_ACTIVE)
                .loanDate(LocalDateTime.now())
                .build();

        return toResponse(loanRepository.save(loan));
    }

    @Override
    @Transactional
    public LoanResponse returnLoan(Long loanId, String authenticatedEmail) {
        User user = getUserByEmail(authenticatedEmail);

        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new ResourceNotFoundException("Préstamo no encontrado con id: " + loanId));

        if (!loan.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("No puedes devolver un préstamo de otro usuario.");
        }

        if (LOAN_STATUS_RETURNED.equalsIgnoreCase(loan.getStatus())) {
            throw new BusinessRuleException("El préstamo ya fue devuelto.");
        }

        loan.setStatus(LOAN_STATUS_RETURNED);
        loan.setReturnedAt(LocalDateTime.now());

        return toResponse(loanRepository.save(loan));
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

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado para el token actual."));
    }

    private LoanResponse toResponse(Loan loan) {
        return LoanResponse.builder()
                .id(loan.getId())
                .userId(loan.getUser().getId())
                .userEmail(loan.getUser().getEmail())
                .bookId(loan.getBook().getId())
                .bookTitle(loan.getBook().getTitle())
                .bookIsbn(loan.getBook().getIsbn())
                .loanDate(loan.getLoanDate())
                .returnedAt(loan.getReturnedAt())
                .status(loan.getStatus())
                .createdAt(loan.getCreatedAt())
                .build();
    }
}
