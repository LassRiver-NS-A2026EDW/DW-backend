package com.lassriver.bookworm.controllers;

import com.lassriver.bookworm.BaseSecurityIntegrationTest;
import com.lassriver.bookworm.entities.Notification;
import com.lassriver.bookworm.entities.User;
import com.lassriver.bookworm.entities.enums.NotificationType;
import com.lassriver.bookworm.repositories.NotificationRepository;
import com.lassriver.bookworm.repositories.UserRepository;
import com.lassriver.bookworm.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@DisplayName("NotificationController - Tests de Integracion")
class NotificationControllerIT extends BaseSecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User user;
    private User otherUser;
    private String userToken;

    @BeforeEach
    void setUp() {
        notificationRepository.deleteAll();
        String suffix = String.valueOf(System.nanoTime());

        user = userRepository.save(User.builder()
                .name("User")
                .email("notification-user-" + suffix + "@bookworm.com")
                .password(passwordEncoder.encode("UserPass123!"))
                .role("USER")
                .language("es")
                .build());

        otherUser = userRepository.save(User.builder()
                .name("Other")
                .email("notification-other-" + suffix + "@bookworm.com")
                .password(passwordEncoder.encode("OtherPass123!"))
                .role("USER")
                .language("es")
                .build());

        userToken = jwtService.generateToken(user);
    }

    @Test
    @DisplayName("GET /api/notifications - lista solo notificaciones del usuario autenticado")
    void getNotifications_ReturnsOnlyAuthenticatedUserNotifications() throws Exception {
        notificationRepository.save(Notification.builder()
                .user(user)
                .type(NotificationType.LOAN_CREATED)
                .title("Prestamo creado")
                .message("Mensaje")
                .targetView("loans")
                .targetId(1L)
                .build());
        notificationRepository.save(Notification.builder()
                .user(otherUser)
                .type(NotificationType.LOAN_CREATED)
                .title("Otra notificacion")
                .message("Mensaje")
                .targetView("loans")
                .targetId(2L)
                .build());

        mockMvc.perform(get("/api/notifications")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].title", is("Prestamo creado")))
                .andExpect(jsonPath("$.totalElements", is(1)));
    }

    @Test
    @DisplayName("GET /api/notifications/unread-count - devuelve contador de no leidas")
    void getUnreadCount_ReturnsUnreadCount() throws Exception {
        Notification readNotification = Notification.builder()
                .user(user)
                .type(NotificationType.LOAN_CREATED)
                .title("Leida")
                .message("Mensaje")
                .readAt(java.time.LocalDateTime.now())
                .build();
        Notification unreadNotification = Notification.builder()
                .user(user)
                .type(NotificationType.LOAN_OVERDUE)
                .title("No leida")
                .message("Mensaje")
                .build();
        notificationRepository.save(readNotification);
        notificationRepository.save(unreadNotification);

        mockMvc.perform(get("/api/notifications/unread-count")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.unreadCount", is(1)));
    }

    @Test
    @DisplayName("PATCH /api/notifications/{id}/read - marca como leida una notificacion propia")
    void markAsRead_WhenOwnNotification_ReturnsUpdatedNotification() throws Exception {
        Notification notification = notificationRepository.save(Notification.builder()
                .user(user)
                .type(NotificationType.RESERVATION_CREATED)
                .title("Reserva creada")
                .message("Mensaje")
                .targetView("reservations")
                .build());

        mockMvc.perform(patch("/api/notifications/{id}/read", notification.getId())
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.readAt", notNullValue()));
    }

    @Test
    @DisplayName("PATCH /api/notifications/{id}/read - 404 si pertenece a otro usuario")
    void markAsRead_WhenOtherUserNotification_Returns404() throws Exception {
        Notification notification = notificationRepository.save(Notification.builder()
                .user(otherUser)
                .type(NotificationType.RESERVATION_CREATED)
                .title("Reserva creada")
                .message("Mensaje")
                .targetView("reservations")
                .build());

        mockMvc.perform(patch("/api/notifications/{id}/read", notification.getId())
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code", is("NOT_FOUND")));
    }

    @Test
    @DisplayName("GET /api/notifications - 401 cuando no hay token")
    void getNotifications_WithoutToken_Returns401() throws Exception {
        mockMvc.perform(get("/api/notifications"))
                .andExpect(status().isUnauthorized());
    }
}
