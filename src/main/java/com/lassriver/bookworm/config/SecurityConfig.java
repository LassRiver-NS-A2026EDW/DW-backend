package com.lassriver.bookworm.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        // Parámetros: saltLength, hashLength, parallelism, memory (en KB), iterations
        // 32MB = 32768 KB para cumplir con AC-19-1
        return new Argon2PasswordEncoder(16, 32, 1, 32768, 2);
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 1. Apagamos CSRF porque usaremos JWT, no cookies de sesión (HU-T01-01)
                .csrf(csrf -> csrf.disable())

                // 2. Configuramos la gestión de sesiones como Stateless (sin estado)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 3. Autorización de rutas
                .authorizeHttpRequests(auth -> auth
                        // Dejamos públicos los endpoints de autenticación (Login y Registro)
                        .requestMatchers("/api/auth/**").permitAll()

                        // Dejamos pública la documentación OpenAPI / Swagger (HU-T02-04)
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()

                        // Cualquier otra ruta va a requerir que el usuario esté autenticado
                        .anyRequest().authenticated());

        return http.build();
    }
}