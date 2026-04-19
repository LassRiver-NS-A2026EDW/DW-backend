package com.lassriver.bookworm.controllers;

import com.lassriver.bookworm.dtos.response.FavoriteResponse;
import com.lassriver.bookworm.dtos.response.FavoriteToggleResponse;
import com.lassriver.bookworm.services.FavoriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/favorites")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;

    @PostMapping("/{bookId}")
    public ResponseEntity<FavoriteToggleResponse> toggleFavorite(
            @PathVariable Long bookId,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(favoriteService.toggleFavorite(bookId, userDetails.getUsername()));
    }

    @GetMapping
    public ResponseEntity<List<FavoriteResponse>> getMyFavorites(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(favoriteService.getMyFavorites(userDetails.getUsername()));
    }
}
