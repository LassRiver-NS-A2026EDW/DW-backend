package com.lassriver.bookworm.config;

import com.lassriver.bookworm.security.JwtAuthenticationFilter;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new Argon2PasswordEncoder(16, 32, 1, 32768, 2);
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/books", "/api/books/*").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/books/*/availability").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/reviews/book/*").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/books").hasAnyAuthority("ADMIN", "LIBRARIAN")
                        .requestMatchers(HttpMethod.PUT, "/api/books/*").hasAnyAuthority("ADMIN", "LIBRARIAN")
                        .requestMatchers(HttpMethod.PATCH, "/api/books/*/status").hasAnyAuthority("ADMIN", "LIBRARIAN")
                        .requestMatchers(HttpMethod.DELETE, "/api/books/*").hasAnyAuthority("ADMIN", "LIBRARIAN")
                        .requestMatchers(HttpMethod.GET, "/api/books/*/copies").hasAnyAuthority("ADMIN", "LIBRARIAN")
                        .requestMatchers(HttpMethod.POST, "/api/books/*/copies").hasAnyAuthority("ADMIN", "LIBRARIAN")
                        .requestMatchers(HttpMethod.DELETE, "/api/books/*/copies/*").hasAnyAuthority("ADMIN", "LIBRARIAN")
                        .requestMatchers(HttpMethod.POST, "/api/books/*/pdf/upload", "/api/books/*/pdf/download").hasAnyAuthority("ADMIN", "LIBRARIAN")
                        .requestMatchers(HttpMethod.GET, "/api/loans").hasAnyAuthority("ADMIN", "LIBRARIAN")
                        .requestMatchers(HttpMethod.GET, "/api/reviews").hasAnyAuthority("ADMIN", "LIBRARIAN")
                        .requestMatchers(HttpMethod.PATCH, "/api/reviews/*/hide", "/api/reviews/*/show").hasAnyAuthority("ADMIN", "LIBRARIAN")
                        .anyRequest().authenticated())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .logout(logout -> logout
                        .logoutUrl("/api/auth/logout")
                        .addLogoutHandler((request, response, authentication) -> SecurityContextHolder.clearContext())
                        .logoutSuccessHandler((request, response, authentication) ->
                                response.setStatus(HttpServletResponse.SC_OK)));

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of(
                "http://localhost:5173",
                "http://127.0.0.1:5173"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        configuration.setExposedHeaders(List.of("Authorization"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", configuration);
        return source;
    }
}
