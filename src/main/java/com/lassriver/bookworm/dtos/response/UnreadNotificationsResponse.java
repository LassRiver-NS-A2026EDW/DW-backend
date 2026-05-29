package com.lassriver.bookworm.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UnreadNotificationsResponse {
    private long unreadCount;
}
