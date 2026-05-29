package com.lassriver.bookworm.dtos.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class NotificationResponse {
    private Long id;
    private String type;
    private String title;
    private String message;
    private String targetView;
    private Long targetId;
    private LocalDateTime readAt;
    private LocalDateTime createdAt;
}
