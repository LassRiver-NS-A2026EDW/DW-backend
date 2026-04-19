package com.lassriver.bookworm.services;

import com.lassriver.bookworm.dtos.request.ReviewCreateRequest;
import com.lassriver.bookworm.dtos.response.ReviewResponse;

public interface ReviewService {
    ReviewResponse createReview(ReviewCreateRequest request, String authenticatedEmail);

    ReviewResponse hideReview(Long reviewId);
}
