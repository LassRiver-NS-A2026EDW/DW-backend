package com.lassriver.bookworm.services;

import com.lassriver.bookworm.dtos.request.ChatRequest;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface ChatService {
    SseEmitter streamChat(ChatRequest request, String authenticatedEmail);
}
