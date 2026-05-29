package com.lassriver.bookworm.services.impl;

import com.lassriver.bookworm.dtos.response.NotificationResponse;
import com.lassriver.bookworm.dtos.response.PageResponse;
import com.lassriver.bookworm.entities.Book;
import com.lassriver.bookworm.entities.Loan;
import com.lassriver.bookworm.entities.Notification;
import com.lassriver.bookworm.entities.User;
import com.lassriver.bookworm.entities.enums.LoanStatus;
import com.lassriver.bookworm.entities.enums.NotificationType;
import com.lassriver.bookworm.exceptions.BusinessRuleException;
import com.lassriver.bookworm.exceptions.ResourceNotFoundException;
import com.lassriver.bookworm.repositories.NotificationRepository;
import com.lassriver.bookworm.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceImplTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    @Test
    void getMyNotifications_WhenUnreadFilter_ReturnsOnlyUnreadPage() {
        User user = User.builder().id(1L).email("user@bookworm.com").build();
        Notification notification = Notification.builder()
                .id(10L)
                .user(user)
                .type(NotificationType.LOAN_DUE_SOON)
                .title("Prestamo por vencer")
                .message("Mensaje")
                .createdAt(LocalDateTime.now())
                .build();
        PageRequest pageable = PageRequest.of(0, 10);

        when(userRepository.findByEmail("user@bookworm.com")).thenReturn(Optional.of(user));
        when(notificationRepository.findAllByUserIdAndReadAtIsNullOrderByCreatedAtDesc(1L, pageable))
                .thenReturn(new PageImpl<>(List.of(notification), pageable, 1));

        PageResponse<NotificationResponse> response =
                notificationService.getMyNotifications("user@bookworm.com", "unread", pageable);

        assertEquals(1, response.getTotalElements());
        assertEquals(NotificationType.LOAN_DUE_SOON.name(), response.getContent().getFirst().getType());
    }

    @Test
    void getMyNotifications_WhenInvalidStatus_ThrowsBusinessRuleException() {
        User user = User.builder().id(1L).email("user@bookworm.com").build();
        PageRequest pageable = PageRequest.of(0, 10);

        when(userRepository.findByEmail("user@bookworm.com")).thenReturn(Optional.of(user));

        assertThrows(
                BusinessRuleException.class,
                () -> notificationService.getMyNotifications("user@bookworm.com", "archived", pageable));
    }

    @Test
    void markAsRead_WhenNotificationBelongsToUser_SetsReadAt() {
        User user = User.builder().id(1L).email("user@bookworm.com").build();
        Notification notification = Notification.builder()
                .id(10L)
                .user(user)
                .type(NotificationType.LOAN_CREATED)
                .title("Prestamo creado")
                .message("Mensaje")
                .createdAt(LocalDateTime.now())
                .build();

        when(userRepository.findByEmail("user@bookworm.com")).thenReturn(Optional.of(user));
        when(notificationRepository.findByIdAndUserId(10L, 1L)).thenReturn(Optional.of(notification));
        when(notificationRepository.save(any(Notification.class))).thenAnswer(invocation -> invocation.getArgument(0));

        NotificationResponse response = notificationService.markAsRead(10L, "user@bookworm.com");

        assertNotNull(response.getReadAt());
        verify(notificationRepository).save(notification);
    }

    @Test
    void markAsRead_WhenNotificationDoesNotBelongToUser_ThrowsResourceNotFoundException() {
        User user = User.builder().id(1L).email("user@bookworm.com").build();

        when(userRepository.findByEmail("user@bookworm.com")).thenReturn(Optional.of(user));
        when(notificationRepository.findByIdAndUserId(10L, 1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> notificationService.markAsRead(10L, "user@bookworm.com"));
    }

    @Test
    void notifyLoanCreated_WhenDedupeKeyDoesNotExist_SavesNotification() {
        User user = User.builder().id(1L).email("user@bookworm.com").build();
        Book book = Book.builder().id(20L).title("Clean Code").build();
        Loan loan = Loan.builder()
                .id(30L)
                .user(user)
                .book(book)
                .status(LoanStatus.ACTIVE)
                .dueDate(LocalDateTime.now().plusHours(1))
                .build();
        ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);

        when(notificationRepository.existsByDedupeKey("LOAN_CREATED:30")).thenReturn(false);
        when(userRepository.findAllByRoleUpperIn(any())).thenReturn(List.of());

        notificationService.notifyLoanCreated(loan);

        verify(notificationRepository).save(captor.capture());
        assertEquals(user, captor.getValue().getUser());
        assertEquals(NotificationType.LOAN_CREATED, captor.getValue().getType());
        assertEquals("loans", captor.getValue().getTargetView());
        assertEquals(30L, captor.getValue().getTargetId());
    }

    @Test
    void notifyLoanReturned_WhenStaffUsersExist_SavesStaffNotification() {
        User borrower = User.builder().id(1L).name("Kevin").email("user@bookworm.com").role("USER").build();
        User admin = User.builder().id(2L).name("Admin").email("admin@bookworm.com").role("ADMIN").build();
        Book book = Book.builder().id(20L).title("Clean Code").build();
        Loan loan = Loan.builder()
                .id(30L)
                .user(borrower)
                .book(book)
                .status(LoanStatus.RETURNED)
                .dueDate(LocalDateTime.now().plusHours(1))
                .build();
        ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);

        when(notificationRepository.existsByDedupeKey(any())).thenReturn(false);
        when(userRepository.findAllByRoleUpperIn(any())).thenReturn(List.of(admin));

        notificationService.notifyLoanReturned(loan);

        verify(notificationRepository, times(2)).save(captor.capture());
        Notification staffNotification = captor.getAllValues().get(1);
        assertEquals(admin, staffNotification.getUser());
        assertEquals(NotificationType.LOAN_RETURNED, staffNotification.getType());
        assertEquals("STAFF:LOAN_RETURNED:30:2", staffNotification.getDedupeKey());
    }
}
