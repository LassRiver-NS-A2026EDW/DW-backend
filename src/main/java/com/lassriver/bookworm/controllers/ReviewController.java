package com.lassriver.bookworm.controllers;

import com.lassriver.bookworm.dtos.request.ReviewCreateRequest;
import com.lassriver.bookworm.dtos.response.ReviewResponse;
import com.lassriver.bookworm.services.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping("/book/{bookId}")
    public ResponseEntity<List<ReviewResponse>> getVisibleReviewsByBook(@PathVariable Long bookId) {
        return ResponseEntity.ok(reviewService.getVisibleReviewsByBook(bookId));
    }

    @GetMapping
    public ResponseEntity<List<ReviewResponse>> getReviews(@RequestParam(required = false) String status) {
        return ResponseEntity.ok(reviewService.getReviews(status));
    }

    @PostMapping
    public ResponseEntity<ReviewResponse> createReview(
            @Valid @RequestBody ReviewCreateRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        ReviewResponse response = reviewService.createReview(request, userDetails.getUsername());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PatchMapping("/{id}/hide")
    public ResponseEntity<ReviewResponse> hideReview(@PathVariable Long id) {
        return ResponseEntity.ok(reviewService.hideReview(id));
    }

    @PatchMapping("/{id}/show")
    public ResponseEntity<ReviewResponse> showReview(@PathVariable Long id) {
        return ResponseEntity.ok(reviewService.showReview(id));
    }
}
