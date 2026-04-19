# =========================================================================
# Dockerfile multi-stage optimizado para Bookworm Backend (Spring Boot)
# =========================================================================
# Stage 1: Compilación con Maven + JDK
# Stage 2: Runtime mínimo con JRE
# =========================================================================

# ======================== STAGE 1: BUILD ========================
FROM eclipse-temurin:21-jdk AS builder

# Metadatos de la imagen
LABEL maintainer="LassRiver Team"
LABEL description="Bookworm Backend API - Build Stage"

# Directorio de trabajo para la compilación
WORKDIR /app

# Copiar archivos de Maven primero (aprovecha capas de cache de Docker)
COPY pom.xml ./

# Descargar dependencias (esta capa se cachea si pom.xml no cambia)
RUN mvn dependency:go-offline -B

# Copiar el código fuente
COPY src ./src

# Compilar el JAR sin ejecutar tests (los tests se ejecutan en CI)
RUN mvn clean package -DskipTests -B \
    && mv target/*.jar target/app.jar

# ======================== STAGE 2: RUNTIME ========================
FROM eclipse-temurin:21-jre-alpine AS runtime

# Metadatos de la imagen final
LABEL maintainer="LassRiver Team"
LABEL description="Bookworm Backend API - Production"
LABEL version="0.0.1-SNAPSHOT"

# Crear un usuario no-root por seguridad
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

# Directorio de la aplicación
WORKDIR /app

# Copiar el JAR desde el stage de build
COPY --from=builder /app/target/app.jar ./app.jar

# Cambiar al usuario no-root
USER appuser

# Puerto expuesto (Spring Boot por defecto)
EXPOSE 8080

# Health check integrado
HEALTHCHECK --interval=30s --timeout=10s --retries=3 --start-period=40s \
    CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# Variables de entorno por defecto
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"
ENV SPRING_PROFILES_ACTIVE="prod"

# Punto de entrada optimizado para contenedores
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar app.jar"]
