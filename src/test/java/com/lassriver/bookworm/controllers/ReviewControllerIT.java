package com.lassriver.bookworm.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lassriver.bookworm.BaseSecurityIntegrationTest;
import com.lassriver.bookworm.dtos.request.ReviewCreateRequest;
import com.lassriver.bookworm.entities.Book;
import com.lassriver.bookworm.entities.Loan;
import com.lassriver.bookworm.entities.Review;
import com.lassriver.bookworm.entities.User;
import com.lassriver.bookworm.repositories.BookRepository;
import com.lassriver.bookworm.repositories.LoanRepository;
import com.lassriver.bookworm.repositories.ReviewRepository;
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

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@DisplayName("ReviewController - Tests de Integración")
class ReviewControllerIT extends BaseSecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String userToken;
    private String adminToken;

    private User user;
    private User admin;
    private Book book;

    @BeforeEach
    void setUp() {
        reviewRepository.deleteAll();
        loanRepository.deleteAll();
        bookRepository.deleteAll();
        userRepository.deleteAll();

        user = userRepository.save(User.builder()
                .name("User")
                .email("user@bookworm.com")
                .password(passwordEncoder.encode("UserPass123!"))
                .role("USER")
                .language("es")
                .build());

        admin = userRepository.save(User.builder()
                .name("Admin")
                .email("admin@bookworm.com")
                .password(passwordEncoder.encode("AdminPass123!"))
                .role("ADMIN")
                .language("es")
                .build());

        userToken = jwtService.generateToken(user);
        adminToken = jwtService.generateToken(admin);

        book = bookRepository.save(Book.builder()
                .title("The Pragmatic Programmer")
                .author("Hunt and Thomas")
                .isbn("ISBN-REVIEW")
                .status("ACTIVE")
                .build());
    }

    @Test
    @DisplayName("POST /api/reviews - crea reseña válida")
    void createReview_WithValidData_Returns201() throws Exception {
        loanRepository.save(Loan.builder().user(user).book(book).status("RETURNED").build());

        ReviewCreateRequest request = new ReviewCreateRequest();
        request.setBookId(book.getId());
        request.setRating(5);
        request.setComment("Muy recomendado");

        mockMvc.perform(post("/api/reviews")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.status", is("VISIBLE")));
    }

    @Test
    @DisplayName("POST /api/reviews - 400 si intenta duplicar reseña")
    void createReview_WhenDuplicated_Returns400() throws Exception {
        loanRepository.save(Loan.builder().user(user).book(book).status("RETURNED").build());
        reviewRepository.save(Review.builder().user(user).book(book).rating(5).comment("x").status("VISIBLE").build());

        ReviewCreateRequest request = new ReviewCreateRequest();
        request.setBookId(book.getId());
        request.setRating(4);
        request.setComment("Otra reseña");

        mockMvc.perform(post("/api/reviews")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code", is("BUSINESS_RULE_VIOLATION")));
    }

    @Test
    @DisplayName("POST /api/reviews - 400 si nunca pidió prestado el libro")
    void createReview_WithoutLoanHistory_Returns400() throws Exception {
        ReviewCreateRequest request = new ReviewCreateRequest();
        request.setBookId(book.getId());
        request.setRating(4);
        request.setComment("Buena lectura");

        mockMvc.perform(post("/api/reviews")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code", is("BUSINESS_RULE_VIOLATION")));
    }

    @Test
    @DisplayName("POST /api/reviews - 401 sin token")
    void createReview_WithoutToken_Returns401() throws Exception {
        ReviewCreateRequest request = new ReviewCreateRequest();
        request.setBookId(book.getId());
        request.setRating(4);
        request.setComment("Buena lectura");

        mockMvc.perform(post("/api/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("POST /api/reviews - 400 por body inválido")
    void createReview_WithInvalidBody_Returns400() throws Exception {
        loanRepository.save(Loan.builder().user(user).book(book).status("RETURNED").build());

        ReviewCreateRequest request = new ReviewCreateRequest();
        request.setBookId(book.getId());
        request.setRating(6);
        request.setComment(" ");

        mockMvc.perform(post("/api/reviews")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code", is("VALIDATION_ERROR")));
    }

    @Test
    @DisplayName("PATCH /api/reviews/{id}/hide - ADMIN oculta reseña")
    void hideReview_AsAdmin_Returns200() throws Exception {
        Review review = reviewRepository.save(Review.builder().user(user).book(book).rating(5).comment("x").status("VISIBLE").build());

        mockMvc.perform(patch("/api/reviews/{id}/hide", review.getId())
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("HIDDEN")));
    }

    @Test
    @DisplayName("PATCH /api/reviews/{id}/hide - USER recibe 403")
    void hideReview_AsUser_Returns403() throws Exception {
        Review review = reviewRepository.save(Review.builder().user(user).book(book).rating(5).comment("x").status("VISIBLE").build());

        mockMvc.perform(patch("/api/reviews/{id}/hide", review.getId())
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("PATCH /api/reviews/{id}/hide - 404 si no existe")
    void hideReview_WhenNotFound_Returns404() throws Exception {
        mockMvc.perform(patch("/api/reviews/{id}/hide", 99999L)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code", is("NOT_FOUND")));
    }
}
