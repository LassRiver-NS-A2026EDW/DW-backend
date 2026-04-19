package com.lassriver.bookworm.services;

import com.lassriver.bookworm.dtos.response.FavoriteResponse;
import com.lassriver.bookworm.dtos.response.FavoriteToggleResponse;

import java.util.List;

public interface FavoriteService {
    FavoriteToggleResponse toggleFavorite(Long bookId, String authenticatedEmail);

    List<FavoriteResponse> getMyFavorites(String authenticatedEmail);
}
