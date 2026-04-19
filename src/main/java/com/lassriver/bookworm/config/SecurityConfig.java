package com.lassriver.bookworm.config;

import com.lassriver.bookworm.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import jakarta.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        // Parámetros Argon2id para cumplir con AC-19-1
        // saltLength, hashLength, parallelism, memory, iterations
        return new Argon2PasswordEncoder(16, 32, 1, 32768, 2);
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 1. Desactivar CSRF (Requisito para APIs Stateless con JWT)
                .csrf(csrf -> csrf.disable())

                // 2. Definir rutas públicas y protegidas
                .authorizeHttpRequests(auth -> auth
                        // Endpoints públicos de Auth y Documentación (HU-T02-04)
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/books").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/books/*").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/books/*/status").hasAuthority("ADMIN")

                        // Cualquier otra petición requiere el token JWT
                        .anyRequest().authenticated())

                // 3. Configurar sesión como Stateless (HU-T01-01)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 4. Registrar nuestro filtro JWT antes del filtro de usuario/contraseña
                // estándar
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)

                // 5. Configuración de Cierre de Sesión (HU-F01-03)
                .logout(logout -> logout
                        .logoutUrl("/api/auth/logout")
                        .addLogoutHandler((request, response, authentication) -> {
                            // Limpiamos el contexto de seguridad de Spring
                            SecurityContextHolder.clearContext();
                        })
                        .logoutSuccessHandler((request, response, authentication) -> {
                            // Respondemos con un 200 OK al cerrar sesión exitosamente
                            response.setStatus(HttpServletResponse.SC_OK);
                        }));

        return http.build();
    }
}
