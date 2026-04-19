package com.lassriver.bookworm.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lassriver.bookworm.BaseSecurityIntegrationTest;
import com.lassriver.bookworm.dtos.request.BookUpsertRequest;
import com.lassriver.bookworm.entities.Book;
import com.lassriver.bookworm.entities.User;
import com.lassriver.bookworm.repositories.BookRepository;
import com.lassriver.bookworm.repositories.UserRepository;
import com.lassriver.bookworm.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@DisplayName("BookController - Tests de Integración")
class BookControllerIT extends BaseSecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String adminToken;
    private String userToken;

    @BeforeEach
    void setUp() {
        bookRepository.deleteAll();
        userRepository.deleteAll();

        User admin = User.builder()
                .name("Admin")
                .email("admin@bookworm.com")
                .password(passwordEncoder.encode("AdminPass123!"))
                .role("ADMIN")
                .language("es")
                .build();

        User user = User.builder()
                .name("User")
                .email("user@bookworm.com")
                .password(passwordEncoder.encode("UserPass123!"))
                .role("USER")
                .language("es")
                .build();

        admin = userRepository.save(admin);
        user = userRepository.save(user);

        adminToken = jwtService.generateToken(admin);
        userToken = jwtService.generateToken(user);

        bookRepository.save(Book.builder().title("Clean Architecture").author("Robert Martin").isbn("ISBN-001").category("Software").status("ACTIVE").build());
        bookRepository.save(Book.builder().title("Java Concurrency in Practice").author("Brian Goetz").isbn("ISBN-002").category("Programming").status("ACTIVE").build());
        bookRepository.save(Book.builder().title("Architecture Patterns").author("Martin Fowler").isbn("ISBN-003").category("Software").status("ACTIVE").build());
    }

    @Test
    @DisplayName("GET /api/books - paginación y filtros combinables")
    void getBooks_WithPaginationAndFilters_ReturnsExpectedPage() throws Exception {
        mockMvc.perform(get("/api/books")
                        .header("Authorization", "Bearer " + adminToken)
                        .param("title", "arch")
                        .param("category", "soft")
                        .param("page", "0")
                        .param("size", "5")
                        .param("sort", "title,asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].title", containsString("Arch")))
                .andExpect(jsonPath("$.totalElements", is(2)));
    }

    @Test
    @DisplayName("POST /api/books - solo ADMIN puede crear")
    void createBook_OnlyAdminCanCreate() throws Exception {
        BookUpsertRequest request = new BookUpsertRequest();
        request.setTitle("Refactoring");
        request.setAuthor("Martin Fowler");
        request.setIsbn("ISBN-NEW");
        request.setCategory("Software");
        request.setLanguage("es");
        request.setCoverUrl("https://example.com/cover.jpg");

        mockMvc.perform(post("/api/books")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());

        mockMvc.perform(post("/api/books")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.title", is("Refactoring")));
    }

    @Test
    @DisplayName("PUT /api/books/{id} - solo ADMIN puede actualizar")
    void updateBook_OnlyAdminCanUpdate() throws Exception {
        Long id = bookRepository.findAll().getFirst().getId();

        BookUpsertRequest request = new BookUpsertRequest();
        request.setTitle("Clean Architecture 2nd Edition");
        request.setAuthor("Robert Martin");
        request.setIsbn("ISBN-001");
        request.setCategory("Software");
        request.setLanguage("en");

        mockMvc.perform(put("/api/books/{id}", id)
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());

        mockMvc.perform(put("/api/books/{id}", id)
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("Clean Architecture 2nd Edition")));
    }

    @Test
    @DisplayName("PATCH /api/books/{id}/status - desactivación lógica solo ADMIN")
    void deactivateBook_OnlyAdminCanDeactivate() throws Exception {
        Long id = bookRepository.findAll().getFirst().getId();

        mockMvc.perform(patch("/api/books/{id}/status", id)
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden());

        mockMvc.perform(patch("/api/books/{id}/status", id)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("INACTIVE")));
    }

    @Test
    @DisplayName("POST /api/books - validaciones de request")
    void createBook_WithInvalidBody_Returns400() throws Exception {
        BookUpsertRequest invalidRequest = new BookUpsertRequest();
        invalidRequest.setTitle(" ");
        invalidRequest.setAuthor(" ");
        invalidRequest.setIsbn(" ");

        mockMvc.perform(post("/api/books")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code", is("VALIDATION_ERROR")))
                .andExpect(jsonPath("$.fields.title", notNullValue()))
                .andExpect(jsonPath("$.fields.author", notNullValue()))
                .andExpect(jsonPath("$.fields.isbn", notNullValue()));
    }
}
