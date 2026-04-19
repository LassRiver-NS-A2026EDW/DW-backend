package com.lassriver.bookworm.dtos.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class FavoriteResponse {
    private Long id;
    private Long bookId;
    private String title;
    private String author;
    private String isbn;
    private String status;
    private LocalDateTime favoritedAt;
}
