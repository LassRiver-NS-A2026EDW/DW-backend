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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoanServiceImplTest {

    @Mock
    private LoanRepository loanRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private LoanServiceImpl loanService;

    @Test
    void createLoan_HappyPath_ReturnsCreatedLoan() {
        User user = User.builder().id(1L).email("user@bookworm.com").build();
        Book book = Book.builder().id(10L).title("Clean Code").isbn("ISBN-001").status("ACTIVE").build();
        LoanCreateRequest request = new LoanCreateRequest();
        request.setBookId(10L);

        Loan savedLoan = Loan.builder().id(100L).user(user).book(book).status("ACTIVE").build();

        when(userRepository.findByEmail("user@bookworm.com")).thenReturn(Optional.of(user));
        when(bookRepository.findById(10L)).thenReturn(Optional.of(book));
        when(loanRepository.existsByBookIdAndStatus(10L, "ACTIVE")).thenReturn(false);
        when(loanRepository.countByUserIdAndStatus(1L, "ACTIVE")).thenReturn(1L);
        when(loanRepository.save(any(Loan.class))).thenReturn(savedLoan);

        LoanResponse response = loanService.createLoan(request, "user@bookworm.com");

        assertEquals(100L, response.getId());
        assertEquals("ACTIVE", response.getStatus());
        assertEquals(10L, response.getBookId());
    }

    @Test
    void createLoan_WhenBookInactive_ThrowsBusinessRuleException() {
        User user = User.builder().id(1L).email("user@bookworm.com").build();
        Book book = Book.builder().id(10L).status("INACTIVE").build();
        LoanCreateRequest request = new LoanCreateRequest();
        request.setBookId(10L);

        when(userRepository.findByEmail("user@bookworm.com")).thenReturn(Optional.of(user));
        when(bookRepository.findById(10L)).thenReturn(Optional.of(book));

        assertThrows(BusinessRuleException.class, () -> loanService.createLoan(request, "user@bookworm.com"));
        verify(loanRepository, never()).save(any(Loan.class));
    }

    @Test
    void createLoan_WhenActiveLoanLimitReached_ThrowsBusinessRuleException() {
        User user = User.builder().id(1L).email("user@bookworm.com").build();
        Book book = Book.builder().id(10L).status("ACTIVE").build();
        LoanCreateRequest request = new LoanCreateRequest();
        request.setBookId(10L);

        when(userRepository.findByEmail("user@bookworm.com")).thenReturn(Optional.of(user));
        when(bookRepository.findById(10L)).thenReturn(Optional.of(book));
        when(loanRepository.existsByBookIdAndStatus(10L, "ACTIVE")).thenReturn(false);
        when(loanRepository.countByUserIdAndStatus(1L, "ACTIVE")).thenReturn(3L);

        assertThrows(BusinessRuleException.class, () -> loanService.createLoan(request, "user@bookworm.com"));
        verify(loanRepository, never()).save(any(Loan.class));
    }

    @Test
    void returnLoan_WhenLoanIsFromAnotherUser_ThrowsAccessDeniedException() {
        User requester = User.builder().id(1L).email("user@bookworm.com").build();
        User owner = User.builder().id(2L).email("other@bookworm.com").build();
        Loan loan = Loan.builder().id(99L).user(owner).status("ACTIVE").build();

        when(userRepository.findByEmail("user@bookworm.com")).thenReturn(Optional.of(requester));
        when(loanRepository.findById(99L)).thenReturn(Optional.of(loan));

        assertThrows(AccessDeniedException.class, () -> loanService.returnLoan(99L, "user@bookworm.com"));
    }

    @Test
    void returnLoan_WhenAlreadyReturned_ThrowsBusinessRuleException() {
        User user = User.builder().id(1L).email("user@bookworm.com").build();
        Loan loan = Loan.builder().id(99L).user(user).status("RETURNED").build();

        when(userRepository.findByEmail("user@bookworm.com")).thenReturn(Optional.of(user));
        when(loanRepository.findById(99L)).thenReturn(Optional.of(loan));

        assertThrows(BusinessRuleException.class, () -> loanService.returnLoan(99L, "user@bookworm.com"));
    }

    @Test
    void returnLoan_WhenNotFound_ThrowsResourceNotFoundException() {
        User user = User.builder().id(1L).email("user@bookworm.com").build();
        when(userRepository.findByEmail("user@bookworm.com")).thenReturn(Optional.of(user));
        when(loanRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> loanService.returnLoan(99L, "user@bookworm.com"));
    }

    @Test
    void getMyLoans_ReturnsLoanList() {
        User user = User.builder().id(1L).email("user@bookworm.com").build();
        Book book = Book.builder().id(10L).title("Clean Code").isbn("ISBN-001").build();
        Loan loan = Loan.builder().id(100L).user(user).book(book).status("ACTIVE").build();

        when(userRepository.findByEmail("user@bookworm.com")).thenReturn(Optional.of(user));
        when(loanRepository.findAllByUserIdOrderByLoanDateDesc(1L)).thenReturn(List.of(loan));

        List<LoanResponse> response = loanService.getMyLoans("user@bookworm.com");

        assertEquals(1, response.size());
        assertEquals(100L, response.getFirst().getId());
    }
}
