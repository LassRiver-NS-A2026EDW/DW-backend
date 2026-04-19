package com.lassriver.bookworm.dtos.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FavoriteToggleResponse {
    private Long bookId;
    private boolean favorite;
}
