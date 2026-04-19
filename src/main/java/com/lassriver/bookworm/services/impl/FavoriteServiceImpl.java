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
import com.lassriver.bookworm.services.FavoriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FavoriteServiceImpl implements FavoriteService {

    private final UserFavoriteRepository userFavoriteRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;

    @Override
    @Transactional
    public FavoriteToggleResponse toggleFavorite(Long bookId, String authenticatedEmail) {
        User user = getUserByEmail(authenticatedEmail);
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Libro no encontrado con id: " + bookId));

        return userFavoriteRepository.findByUserIdAndBookId(user.getId(), book.getId())
                .map(existingFavorite -> {
                    userFavoriteRepository.delete(existingFavorite);
                    return FavoriteToggleResponse.builder().bookId(book.getId()).favorite(false).build();
                })
                .orElseGet(() -> {
                    UserFavorite favorite = UserFavorite.builder().user(user).book(book).build();
                    userFavoriteRepository.save(favorite);
                    return FavoriteToggleResponse.builder().bookId(book.getId()).favorite(true).build();
                });
    }

    @Override
    @Transactional(readOnly = true)
    public List<FavoriteResponse> getMyFavorites(String authenticatedEmail) {
        User user = getUserByEmail(authenticatedEmail);

        return userFavoriteRepository.findAllByUserIdOrderByCreatedAtDesc(user.getId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado para el token actual."));
    }

    private FavoriteResponse toResponse(UserFavorite favorite) {
        return FavoriteResponse.builder()
                .id(favorite.getId())
                .bookId(favorite.getBook().getId())
                .title(favorite.getBook().getTitle())
                .author(favorite.getBook().getAuthor())
                .isbn(favorite.getBook().getIsbn())
                .status(favorite.getBook().getStatus())
                .favoritedAt(favorite.getCreatedAt())
                .build();
    }
}
