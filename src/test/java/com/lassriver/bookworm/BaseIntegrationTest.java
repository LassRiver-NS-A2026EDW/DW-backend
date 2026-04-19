package com.lassriver.bookworm;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * Clase base abstracta para todos los tests de integración.
 * 
 * <p>Configura un contenedor PostgreSQL compartido (singleton pattern)
 * usando Testcontainers. Todos los tests de integración que extiendan
 * esta clase compartirán el mismo contenedor, mejorando el rendimiento.</p>
 * 
 * <p><strong>Convención de nombres:</strong> Los tests de integración deben
 * terminar en {@code IT} o {@code IntegrationTest} para que Maven Failsafe
 * los detecte automáticamente.</p>
 * 
 * <p><strong>Ejemplo de uso:</strong></p>
 * <pre>{@code
 * class MiServicioIT extends BaseIntegrationTest {
 *     @Test
 *     void debeGuardarEnBaseDeDatos() {
 *         // Este test usa PostgreSQL real vía Testcontainers
 *     }
 * }
 * }</pre>
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("integration")
@Testcontainers
public abstract class BaseIntegrationTest {

    /**
     * Contenedor PostgreSQL compartido (singleton).
     * Se levanta una sola vez para todos los tests de integración.
     * Usa la misma versión de PostgreSQL que producción (14+).
     */
    @Container
    static final PostgreSQLContainer<?> POSTGRES_CONTAINER =
            new PostgreSQLContainer<>("postgres:16-alpine")
                    .withDatabaseName("bookworm_test")
                    .withUsername("test")
                    .withPassword("test");

    /**
     * Inyecta dinámicamente las propiedades de conexión del contenedor
     * PostgreSQL en el contexto de Spring. Esto permite que Spring Data JPA
     * y Flyway se conecten al contenedor correcto en cada ejecución.
     *
     * @param registry Registro de propiedades dinámicas de Spring
     */
    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES_CONTAINER::getUsername);
        registry.add("spring.datasource.password", POSTGRES_CONTAINER::getPassword);
    }
}
