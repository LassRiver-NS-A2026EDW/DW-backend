package com.lassriver.bookworm.services.impl;

import com.lassriver.bookworm.dtos.response.FavoriteResponse;
import com.lassriver.bookworm.dtos.response.FavoriteToggleResponse;
import com.lassriver.bookworm.entities.Book;
import com.lassriver.bookworm.entities.User;
import com.lassriver.bookworm.entities.UserFavorite;
import com.lassriver.bookworm.exceptions.ResourceNotFoundException;
import com.lassriver.bookworm.repositories.BookRepository;
import com.lassriver.bookworm.repositories.UserFavoriteRepository;
import com.lassriver.bookworm.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FavoriteServiceImplTest {

    @Mock
    private UserFavoriteRepository userFavoriteRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private FavoriteServiceImpl favoriteService;

    @Test
    void toggleFavorite_WhenNotFavorite_AddsFavorite() {
        User user = User.builder().id(1L).email("user@bookworm.com").build();
        Book book = Book.builder().id(10L).build();

        when(userRepository.findByEmail("user@bookworm.com")).thenReturn(Optional.of(user));
        when(bookRepository.findById(10L)).thenReturn(Optional.of(book));
        when(userFavoriteRepository.findByUserIdAndBookId(1L, 10L)).thenReturn(Optional.empty());

        FavoriteToggleResponse response = favoriteService.toggleFavorite(10L, "user@bookworm.com");

        assertTrue(response.isFavorite());
        verify(userFavoriteRepository, times(1)).save(any(UserFavorite.class));
    }

    @Test
    void toggleFavorite_WhenAlreadyFavorite_RemovesFavorite() {
        User user = User.builder().id(1L).email("user@bookworm.com").build();
        Book book = Book.builder().id(10L).build();
        UserFavorite existing = UserFavorite.builder().id(100L).user(user).book(book).build();

        when(userRepository.findByEmail("user@bookworm.com")).thenReturn(Optional.of(user));
        when(bookRepository.findById(10L)).thenReturn(Optional.of(book));
        when(userFavoriteRepository.findByUserIdAndBookId(1L, 10L)).thenReturn(Optional.of(existing));

        FavoriteToggleResponse response = favoriteService.toggleFavorite(10L, "user@bookworm.com");

        assertFalse(response.isFavorite());
        verify(userFavoriteRepository, times(1)).delete(existing);
        verify(userFavoriteRepository, never()).save(any(UserFavorite.class));
    }

    @Test
    void toggleFavorite_WhenBookNotFound_ThrowsResourceNotFoundException() {
        User user = User.builder().id(1L).email("user@bookworm.com").build();

        when(userRepository.findByEmail("user@bookworm.com")).thenReturn(Optional.of(user));
        when(bookRepository.findById(10L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> favoriteService.toggleFavorite(10L, "user@bookworm.com"));
    }

    @Test
    void getMyFavorites_ReturnsFavoriteList() {
        User user = User.builder().id(1L).email("user@bookworm.com").build();
        Book book = Book.builder().id(10L).title("Book").author("Author").isbn("ISBN").status("ACTIVE").build();
        UserFavorite favorite = UserFavorite.builder().id(99L).user(user).book(book).build();

        when(userRepository.findByEmail("user@bookworm.com")).thenReturn(Optional.of(user));
        when(userFavoriteRepository.findAllByUserIdOrderByCreatedAtDesc(1L)).thenReturn(List.of(favorite));

        List<FavoriteResponse> response = favoriteService.getMyFavorites("user@bookworm.com");

        assertEquals(1, response.size());
        assertEquals(10L, response.getFirst().getBookId());
    }
}
