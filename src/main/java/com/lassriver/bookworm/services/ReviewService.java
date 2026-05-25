package com.lassriver.bookworm.services;

import com.lassriver.bookworm.dtos.request.ReviewCreateRequest;
import com.lassriver.bookworm.dtos.response.ReviewResponse;

import java.util.List;

public interface ReviewService {
    ReviewResponse createReview(ReviewCreateRequest request, String authenticatedEmail);

    List<ReviewResponse> getVisibleReviewsByBook(Long bookId);

    List<ReviewResponse> getReviews(String status);

    ReviewResponse hideReview(Long reviewId);

    ReviewResponse showReview(Long reviewId);
}
