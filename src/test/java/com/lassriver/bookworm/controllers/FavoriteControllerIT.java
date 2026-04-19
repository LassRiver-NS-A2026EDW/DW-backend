package com.lassriver.bookworm.controllers;

import com.lassriver.bookworm.BaseSecurityIntegrationTest;
import com.lassriver.bookworm.entities.Book;
import com.lassriver.bookworm.entities.User;
import com.lassriver.bookworm.entities.UserFavorite;
import com.lassriver.bookworm.repositories.BookRepository;
import com.lassriver.bookworm.repositories.UserFavoriteRepository;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@DisplayName("FavoriteController - Tests de Integración")
class FavoriteControllerIT extends BaseSecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserFavoriteRepository userFavoriteRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String userToken;

    private User user;
    private User otherUser;
    private Book book;

    @BeforeEach
    void setUp() {
        userFavoriteRepository.deleteAll();
        bookRepository.deleteAll();
        userRepository.deleteAll();

        user = userRepository.save(User.builder()
                .name("User")
                .email("user@bookworm.com")
                .password(passwordEncoder.encode("UserPass123!"))
                .role("USER")
                .language("es")
                .build());

        otherUser = userRepository.save(User.builder()
                .name("Other")
                .email("other@bookworm.com")
                .password(passwordEncoder.encode("OtherPass123!"))
                .role("USER")
                .language("es")
                .build());

        userToken = jwtService.generateToken(user);

        book = bookRepository.save(Book.builder()
                .title("Clean Code")
                .author("Robert Martin")
                .isbn("ISBN-FAVORITE")
                .status("ACTIVE")
                .build());
    }

    @Test
    @DisplayName("POST /api/favorites/{bookId} - agrega favorito y luego lo quita (sin duplicados)")
    void toggleFavorite_AddAndRemove_ReturnsExpectedState() throws Exception {
        mockMvc.perform(post("/api/favorites/{bookId}", book.getId())
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookId", is(book.getId().intValue())))
                .andExpect(jsonPath("$.favorite", is(true)));

        mockMvc.perform(post("/api/favorites/{bookId}", book.getId())
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.favorite", is(false)));
    }

    @Test
    @DisplayName("GET /api/favorites - retorna solo favoritos del usuario autenticado")
    void getFavorites_ReturnsOnlyAuthenticatedUserFavorites() throws Exception {
        userFavoriteRepository.save(UserFavorite.builder().user(user).book(book).build());
        userFavoriteRepository.save(UserFavorite.builder().user(otherUser).book(book).build());

        mockMvc.perform(get("/api/favorites")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].bookId", is(book.getId().intValue())));
    }

    @Test
    @DisplayName("POST /api/favorites/{bookId} - 401 sin token")
    void toggleFavorite_WithoutToken_Returns401() throws Exception {
        mockMvc.perform(post("/api/favorites/{bookId}", book.getId()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("POST /api/favorites/{bookId} - 404 cuando libro no existe")
    void toggleFavorite_WithUnknownBook_Returns404() throws Exception {
        mockMvc.perform(post("/api/favorites/{bookId}", 99999L)
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code", is("NOT_FOUND")));
    }
}
