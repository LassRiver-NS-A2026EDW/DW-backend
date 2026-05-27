package com.lassriver.bookworm.services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lassriver.bookworm.dtos.request.ChatMessageRequest;
import com.lassriver.bookworm.dtos.request.ChatRequest;
import com.lassriver.bookworm.entities.Book;
import com.lassriver.bookworm.entities.User;
import com.lassriver.bookworm.entities.enums.LoanStatus;
import com.lassriver.bookworm.exceptions.ResourceNotFoundException;
import com.lassriver.bookworm.repositories.BookRepository;
import com.lassriver.bookworm.repositories.LoanRepository;
import com.lassriver.bookworm.repositories.UserRepository;
import com.lassriver.bookworm.services.ChatService;
import com.lassriver.bookworm.services.ai.AiChatClient;
import com.lassriver.bookworm.services.ai.AiMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private static final long SSE_TIMEOUT_MS = 90_000L;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final AiChatClient aiChatClient;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final LoanRepository loanRepository;

    @Override
    public SseEmitter streamChat(ChatRequest request, String authenticatedEmail) {
        User user = getUser(authenticatedEmail);
        Book book = resolveBookAndValidateAccess(request.getBookId(), user);
        List<AiMessage> messages = buildMessages(request, book);
        SseEmitter emitter = new SseEmitter(SSE_TIMEOUT_MS);

        Thread.ofVirtual().start(() -> {
            try {
                aiChatClient.stream(messages, chunk -> sendChunk(emitter, chunk));
                emitter.send(SseEmitter.event().data("[DONE]"));
                emitter.complete();
            } catch (Exception ex) {
                sendError(emitter, ex.getMessage());
                emitter.complete();
            }
        });

        return emitter;
    }

    private User getUser(String authenticatedEmail) {
        return userRepository.findByEmail(authenticatedEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado para el token actual."));
    }

    private Book resolveBookAndValidateAccess(Long bookId, User user) {
        if (bookId == null) {
            return null;
        }
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Libro no encontrado con id: " + bookId));
        boolean canUseChat = isPrivileged(user)
                || loanRepository.existsByUserIdAndBookIdAndStatus(user.getId(), book.getId(), LoanStatus.ACTIVE);
        if (!canUseChat) {
            throw new AccessDeniedException("Debes tener un prestamo activo para usar el chat de este libro.");
        }
        return book;
    }

    private boolean isPrivileged(User user) {
        return "ADMIN".equalsIgnoreCase(user.getRole()) || "LIBRARIAN".equalsIgnoreCase(user.getRole());
    }

    private List<AiMessage> buildMessages(ChatRequest request, Book book) {
        String title = book == null ? "libro" : book.getTitle();
        String author = book == null ? "autor desconocido" : book.getAuthor();
        String context = firstNonBlank(request.getContext(), book == null ? "" : book.getDescription());

        List<AiMessage> messages = new ArrayList<>();
        messages.add(new AiMessage("system",
                "Eres un asistente literario experto y claro. Ayudas al usuario a comprender el libro \""
                        + title + "\" de " + author + ". Contexto del libro: "
                        + (context == null || context.isBlank() ? "No disponible." : context)
                        + " Responde siempre en el idioma del usuario y ancla tu explicacion al fragmento seleccionado cuando exista."));

        if (request.getHistory() != null) {
            for (ChatMessageRequest historyMessage : request.getHistory()) {
                if (isAllowedRole(historyMessage.getRole())) {
                    messages.add(new AiMessage(historyMessage.getRole().toLowerCase(), historyMessage.getContent()));
                }
            }
        }

        String question = request.getQuestion();
        String selectedText = request.getSelectedText();
        if (selectedText != null && !selectedText.isBlank()) {
            question = "Fragmento seleccionado del libro:\n\"" + selectedText.trim() + "\"\n\nMi pregunta:\n" + question;
        }
        messages.add(new AiMessage("user", question));
        return messages;
    }

    private boolean isAllowedRole(String role) {
        return "user".equalsIgnoreCase(role) || "assistant".equalsIgnoreCase(role);
    }

    private String firstNonBlank(String first, String second) {
        return first != null && !first.isBlank() ? first : second;
    }

    private void sendChunk(SseEmitter emitter, String chunk) {
        try {
            emitter.send(SseEmitter.event().data(objectMapper.writeValueAsString(chunk)));
        } catch (IOException ex) {
            throw new IllegalStateException("No se pudo enviar el chunk SSE.", ex);
        }
    }

    private void sendError(SseEmitter emitter, String message) {
        try {
            emitter.send(SseEmitter.event().data("[ERROR] " + (message == null ? "Error de IA." : message)));
        } catch (IOException ex) {
            throw new IllegalStateException("No se pudo enviar el error SSE.", ex);
        }
    }
}
