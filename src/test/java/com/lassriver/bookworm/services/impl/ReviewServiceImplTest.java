package com.lassriver.bookworm.services.impl;

import com.lassriver.bookworm.dtos.request.ReviewCreateRequest;
import com.lassriver.bookworm.dtos.response.ReviewResponse;
import com.lassriver.bookworm.entities.Book;
import com.lassriver.bookworm.entities.Review;
import com.lassriver.bookworm.entities.User;
import com.lassriver.bookworm.exceptions.BusinessRuleException;
import com.lassriver.bookworm.exceptions.ResourceNotFoundException;
import com.lassriver.bookworm.repositories.BookRepository;
import com.lassriver.bookworm.repositories.LoanRepository;
import com.lassriver.bookworm.repositories.ReviewRepository;
import com.lassriver.bookworm.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceImplTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private LoanRepository loanRepository;

    @InjectMocks
    private ReviewServiceImpl reviewService;

    @Test
    void createReview_HappyPath_ReturnsCreatedReview() {
        User user = User.builder().id(1L).email("user@bookworm.com").build();
        Book book = Book.builder().id(10L).title("Book").build();
        ReviewCreateRequest request = new ReviewCreateRequest();
        request.setBookId(10L);
        request.setRating(5);
        request.setComment("Excelente libro");

        Review saved = Review.builder().id(100L).user(user).book(book).rating(5).comment("Excelente libro").status("VISIBLE").build();

        when(userRepository.findByEmail("user@bookworm.com")).thenReturn(Optional.of(user));
        when(bookRepository.findById(10L)).thenReturn(Optional.of(book));
        when(reviewRepository.existsByUserIdAndBookId(1L, 10L)).thenReturn(false);
        when(loanRepository.existsByUserIdAndBookId(1L, 10L)).thenReturn(true);
        when(reviewRepository.save(any(Review.class))).thenReturn(saved);

        ReviewResponse response = reviewService.createReview(request, "user@bookworm.com");

        assertEquals(100L, response.getId());
        assertEquals("VISIBLE", response.getStatus());
    }

    @Test
    void createReview_WhenDuplicated_ThrowsBusinessRuleException() {
        User user = User.builder().id(1L).email("user@bookworm.com").build();
        Book book = Book.builder().id(10L).build();
        ReviewCreateRequest request = new ReviewCreateRequest();
        request.setBookId(10L);
        request.setRating(4);
        request.setComment("Muy bueno");

        when(userRepository.findByEmail("user@bookworm.com")).thenReturn(Optional.of(user));
        when(bookRepository.findById(10L)).thenReturn(Optional.of(book));
        when(reviewRepository.existsByUserIdAndBookId(1L, 10L)).thenReturn(true);

        assertThrows(BusinessRuleException.class, () -> reviewService.createReview(request, "user@bookworm.com"));
        verify(reviewRepository, never()).save(any(Review.class));
    }

    @Test
    void createReview_WhenNeverLoaned_ThrowsBusinessRuleException() {
        User user = User.builder().id(1L).email("user@bookworm.com").build();
        Book book = Book.builder().id(10L).build();
        ReviewCreateRequest request = new ReviewCreateRequest();
        request.setBookId(10L);
        request.setRating(4);
        request.setComment("Muy bueno");

        when(userRepository.findByEmail("user@bookworm.com")).thenReturn(Optional.of(user));
        when(bookRepository.findById(10L)).thenReturn(Optional.of(book));
        when(reviewRepository.existsByUserIdAndBookId(1L, 10L)).thenReturn(false);
        when(loanRepository.existsByUserIdAndBookId(1L, 10L)).thenReturn(false);

        assertThrows(BusinessRuleException.class, () -> reviewService.createReview(request, "user@bookworm.com"));
        verify(reviewRepository, never()).save(any(Review.class));
    }

    @Test
    void hideReview_WhenNotFound_ThrowsResourceNotFoundException() {
        when(reviewRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> reviewService.hideReview(99L));
    }

    @Test
    void hideReview_HappyPath_ChangesStatusToHidden() {
        User user = User.builder().id(1L).email("user@bookworm.com").build();
        Book book = Book.builder().id(10L).title("Book").build();
        Review review = Review.builder().id(99L).user(user).book(book).rating(5).comment("x").status("VISIBLE").build();
        Review hidden = Review.builder().id(99L).user(user).book(book).rating(5).comment("x").status("HIDDEN").build();

        when(reviewRepository.findById(99L)).thenReturn(Optional.of(review));
        when(reviewRepository.save(review)).thenReturn(hidden);

        ReviewResponse response = reviewService.hideReview(99L);

        assertEquals("HIDDEN", response.getStatus());
    }

    @Test
    void getReviews_WhenStatusAll_ReturnsVisibleAndHiddenReviews() {
        User user = User.builder().id(1L).email("user@bookworm.com").build();
        Book book = Book.builder().id(10L).title("Book").build();
        Review visible = Review.builder().id(1L).user(user).book(book).rating(5).comment("x").status("VISIBLE").build();
        Review hidden = Review.builder().id(2L).user(user).book(book).rating(3).comment("y").status("HIDDEN").build();

        when(reviewRepository.findAllByOrderByCreatedAtDesc()).thenReturn(List.of(hidden, visible));

        List<ReviewResponse> response = reviewService.getReviews("ALL");

        assertEquals(2, response.size());
        assertEquals("HIDDEN", response.getFirst().getStatus());
        verify(reviewRepository, never()).findAllByStatusOrderByCreatedAtDesc(any());
    }
}
