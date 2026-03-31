package com.lassriver.bookworm.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lassriver.bookworm.AbstractIntegrationTest;
import com.lassriver.bookworm.dtos.request.LoginRequest;
import com.lassriver.bookworm.dtos.request.UserRegistrationRequest;
import com.lassriver.bookworm.entities.enums.Gender;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests de integración para AuthController.
 *
 * <p>Estos tests validan el flujo completo de autenticación contra
 * una base de datos PostgreSQL real levantada con Testcontainers.
 * Flyway ejecuta las migraciones reales del proyecto.</p>
 *
 * <p><strong>Convención:</strong> El sufijo {@code IT} indica que este
 * es un test de integración, ejecutado por Maven Failsafe.</p>
 */
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("AuthController - Tests de Integración")
class AuthControllerIT extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String BASE_URL = "/api/auth";
    private static final String TEST_EMAIL = "integration-test@bookworm.com";
    private static final String TEST_PASSWORD = "SecurePass123!";

    @Test
    @Order(1)
    @DisplayName("POST /api/auth/register - Registro exitoso devuelve 201 CREATED")
    void register_ConDatosValidos_DebeRetornar201() throws Exception {
        // GIVEN: Un request de registro con datos válidos
        UserRegistrationRequest request = new UserRegistrationRequest();
        request.setName("Test User");
        request.setEmail(TEST_EMAIL);
        request.setPassword(TEST_PASSWORD);
        request.setGender(Gender.F);
        request.setBirthDate(LocalDate.of(1995, 6, 15));

        // WHEN & THEN: El registro es exitoso y retorna 201
        mockMvc.perform(post(BASE_URL + "/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.name", is("Test User")))
                .andExpect(jsonPath("$.email", is(TEST_EMAIL)));
    }

    @Test
    @Order(2)
    @DisplayName("POST /api/auth/register - Email duplicado devuelve 409 CONFLICT")
    void register_ConEmailDuplicado_DebeRetornar409() throws Exception {
        // GIVEN: Un request con el mismo email ya registrado
        UserRegistrationRequest request = new UserRegistrationRequest();
        request.setName("Otro Usuario");
        request.setEmail(TEST_EMAIL);
        request.setPassword(TEST_PASSWORD);
        request.setGender(Gender.M);
        request.setBirthDate(LocalDate.of(2000, 1, 1));

        // WHEN & THEN: El registro falla por email duplicado
        mockMvc.perform(post(BASE_URL + "/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    @Order(3)
    @DisplayName("POST /api/auth/register - Datos inválidos devuelve 400 BAD REQUEST")
    void register_ConDatosInvalidos_DebeRetornar400() throws Exception {
        // GIVEN: Un request con campos vacíos/inválidos
        UserRegistrationRequest request = new UserRegistrationRequest();
        request.setName(""); // Nombre vacío (violación @NotBlank)
        request.setEmail("email-invalido"); // Email inválido
        request.setPassword("123"); // Muy corta (violación @Size(min=8))

        // WHEN & THEN: Validación falla y retorna 400
        mockMvc.perform(post(BASE_URL + "/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(4)
    @DisplayName("POST /api/auth/login - Login exitoso devuelve token JWT")
    void login_ConCredencialesValidas_DebeRetornarToken() throws Exception {
        // GIVEN: Credenciales del usuario registrado en el test anterior
        LoginRequest request = new LoginRequest();
        request.setEmail(TEST_EMAIL);
        request.setPassword(TEST_PASSWORD);

        // WHEN & THEN: El login es exitoso y retorna un token JWT
        mockMvc.perform(post(BASE_URL + "/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", notNullValue()))
                .andExpect(jsonPath("$.token", not(emptyString())));
    }

    @Test
    @Order(5)
    @DisplayName("POST /api/auth/login - Credenciales inválidas devuelve 401")
    void login_ConCredencialesInvalidas_DebeRetornar401() throws Exception {
        // GIVEN: Credenciales incorrectas
        LoginRequest request = new LoginRequest();
        request.setEmail(TEST_EMAIL);
        request.setPassword("ContraseñaIncorrecta123!");

        // WHEN & THEN: El login falla
        mockMvc.perform(post(BASE_URL + "/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }
}
