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
import com.lassriver.bookworm.services.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private static final String REVIEW_VISIBLE = "VISIBLE";
    private static final String REVIEW_HIDDEN = "HIDDEN";

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final LoanRepository loanRepository;

    @Override
    @Transactional
    public ReviewResponse createReview(ReviewCreateRequest request, String authenticatedEmail) {
        User user = getUserByEmail(authenticatedEmail);
        Book book = bookRepository.findById(request.getBookId())
                .orElseThrow(() -> new ResourceNotFoundException("Libro no encontrado con id: " + request.getBookId()));

        if (reviewRepository.existsByUserIdAndBookId(user.getId(), book.getId())) {
            throw new BusinessRuleException("Ya existe una reseña para este libro por este usuario.");
        }

        if (!loanRepository.existsByUserIdAndBookId(user.getId(), book.getId())) {
            throw new BusinessRuleException("Solo puedes reseñar libros que hayas solicitado en préstamo.");
        }

        Review review = Review.builder()
                .user(user)
                .book(book)
                .rating(request.getRating())
                .comment(request.getComment())
                .status(REVIEW_VISIBLE)
                .build();

        return toResponse(reviewRepository.save(review));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReviewResponse> getVisibleReviewsByBook(Long bookId) {
        if (!bookRepository.existsById(bookId)) {
            throw new ResourceNotFoundException("Libro no encontrado con id: " + bookId);
        }

        return reviewRepository.findAllByBookIdAndStatusOrderByCreatedAtDesc(bookId, REVIEW_VISIBLE)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReviewResponse> getReviews(String status) {
        String effectiveStatus = status == null || status.isBlank() ? REVIEW_VISIBLE : status.toUpperCase();
        return reviewRepository.findAllByStatusOrderByCreatedAtDesc(effectiveStatus)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public ReviewResponse hideReview(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Reseña no encontrada con id: " + reviewId));

        review.setStatus(REVIEW_HIDDEN);
        return toResponse(reviewRepository.save(review));
    }

    @Override
    @Transactional
    public ReviewResponse showReview(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Reseña no encontrada con id: " + reviewId));

        review.setStatus(REVIEW_VISIBLE);
        return toResponse(reviewRepository.save(review));
    }

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado para el token actual."));
    }

    private ReviewResponse toResponse(Review review) {
        return ReviewResponse.builder()
                .id(review.getId())
                .userId(review.getUser().getId())
                .userName(review.getUser().getName())
                .userEmail(review.getUser().getEmail())
                .bookId(review.getBook().getId())
                .bookTitle(review.getBook().getTitle())
                .rating(review.getRating())
                .comment(review.getComment())
                .status(review.getStatus())
                .createdAt(review.getCreatedAt())
                .updatedAt(review.getUpdatedAt())
                .build();
    }
}
