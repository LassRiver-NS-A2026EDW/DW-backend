package com.lassriver.bookworm.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lassriver.bookworm.BaseSecurityIntegrationTest;
import com.lassriver.bookworm.dtos.request.LoanCreateRequest;
import com.lassriver.bookworm.entities.Book;
import com.lassriver.bookworm.entities.Loan;
import com.lassriver.bookworm.entities.User;
import com.lassriver.bookworm.repositories.BookRepository;
import com.lassriver.bookworm.repositories.LoanRepository;
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
@DisplayName("LoanController - Tests de Integración")
class LoanControllerIT extends BaseSecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

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
    private String otherUserToken;

    private User user;
    private User otherUser;
    private Book activeBook;
    private Book inactiveBook;

    @BeforeEach
    void setUp() {
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

        otherUser = userRepository.save(User.builder()
                .name("Other")
                .email("other@bookworm.com")
                .password(passwordEncoder.encode("OtherPass123!"))
                .role("USER")
                .language("es")
                .build());

        userToken = jwtService.generateToken(user);
        otherUserToken = jwtService.generateToken(otherUser);

        activeBook = bookRepository.save(Book.builder()
                .title("Clean Architecture")
                .author("Robert Martin")
                .isbn("ISBN-ACTIVE")
                .status("ACTIVE")
                .build());

        inactiveBook = bookRepository.save(Book.builder()
                .title("Inactive Book")
                .author("Any")
                .isbn("ISBN-INACTIVE")
                .status("INACTIVE")
                .build());
    }

    @Test
    @DisplayName("POST /api/loans - crea préstamo válido")
    void createLoan_WithValidRequest_Returns201() throws Exception {
        LoanCreateRequest request = new LoanCreateRequest();
        request.setBookId(activeBook.getId());

        mockMvc.perform(post("/api/loans")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.status", is("ACTIVE")))
                .andExpect(jsonPath("$.bookId", is(activeBook.getId().intValue())))
                .andExpect(jsonPath("$.userEmail", is(user.getEmail())));
    }

    @Test
    @DisplayName("POST /api/loans - 401 cuando no hay token")
    void createLoan_WithoutToken_Returns401() throws Exception {
        LoanCreateRequest request = new LoanCreateRequest();
        request.setBookId(activeBook.getId());

        mockMvc.perform(post("/api/loans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("POST /api/loans - 400 si libro inactivo")
    void createLoan_WhenBookInactive_Returns400() throws Exception {
        LoanCreateRequest request = new LoanCreateRequest();
        request.setBookId(inactiveBook.getId());

        mockMvc.perform(post("/api/loans")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code", is("BUSINESS_RULE_VIOLATION")));
    }

    @Test
    @DisplayName("POST /api/loans - 400 cuando no hay disponibilidad")
    void createLoan_WhenBookAlreadyLoaned_Returns400() throws Exception {
        loanRepository.save(Loan.builder().user(user).book(activeBook).status("ACTIVE").build());

        LoanCreateRequest request = new LoanCreateRequest();
        request.setBookId(activeBook.getId());

        mockMvc.perform(post("/api/loans")
                        .header("Authorization", "Bearer " + otherUserToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code", is("BUSINESS_RULE_VIOLATION")));
    }

    @Test
    @DisplayName("POST /api/loans - 400 cuando usuario supera límite")
    void createLoan_WhenUserExceedsLoanLimit_Returns400() throws Exception {
        Book book2 = bookRepository.save(Book.builder().title("B2").author("A2").isbn("ISBN-2").status("ACTIVE").build());
        Book book3 = bookRepository.save(Book.builder().title("B3").author("A3").isbn("ISBN-3").status("ACTIVE").build());
        Book book4 = bookRepository.save(Book.builder().title("B4").author("A4").isbn("ISBN-4").status("ACTIVE").build());

        loanRepository.save(Loan.builder().user(user).book(activeBook).status("ACTIVE").build());
        loanRepository.save(Loan.builder().user(user).book(book2).status("ACTIVE").build());
        loanRepository.save(Loan.builder().user(user).book(book3).status("ACTIVE").build());

        LoanCreateRequest request = new LoanCreateRequest();
        request.setBookId(book4.getId());

        mockMvc.perform(post("/api/loans")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code", is("BUSINESS_RULE_VIOLATION")));
    }

    @Test
    @DisplayName("PUT /api/loans/{id}/return - devuelve préstamo propio")
    void returnLoan_WhenOwnedByUser_Returns200() throws Exception {
        Loan loan = loanRepository.save(Loan.builder().user(user).book(activeBook).status("ACTIVE").build());

        mockMvc.perform(put("/api/loans/{id}/return", loan.getId())
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("RETURNED")))
                .andExpect(jsonPath("$.returnedAt", notNullValue()));
    }

    @Test
    @DisplayName("PUT /api/loans/{id}/return - 403 si préstamo pertenece a otro usuario")
    void returnLoan_WhenLoanBelongsToOtherUser_Returns403() throws Exception {
        Loan loan = loanRepository.save(Loan.builder().user(otherUser).book(activeBook).status("ACTIVE").build());

        mockMvc.perform(put("/api/loans/{id}/return", loan.getId())
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code", is("FORBIDDEN")));
    }

    @Test
    @DisplayName("PUT /api/loans/{id}/return - 404 si préstamo no existe")
    void returnLoan_WhenLoanNotFound_Returns404() throws Exception {
        mockMvc.perform(put("/api/loans/{id}/return", 99999L)
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code", is("NOT_FOUND")));
    }

    @Test
    @DisplayName("GET /api/loans/my-loans - lista préstamos del usuario autenticado")
    void getMyLoans_ReturnsOnlyAuthenticatedUserLoans() throws Exception {
        loanRepository.save(Loan.builder().user(user).book(activeBook).status("ACTIVE").build());
        loanRepository.save(Loan.builder().user(otherUser).book(activeBook).status("RETURNED").build());

        mockMvc.perform(get("/api/loans/my-loans")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].userEmail", is(user.getEmail())));
    }

    @Test
    @DisplayName("POST /api/loans - 400 por body inválido")
    void createLoan_WithInvalidBody_Returns400() throws Exception {
        LoanCreateRequest request = new LoanCreateRequest();

        mockMvc.perform(post("/api/loans")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code", is("VALIDATION_ERROR")))
                .andExpect(jsonPath("$.fields.bookId", notNullValue()));
    }
}
