# =========================================================================
# Dockerfile multi-stage optimizado para Bookworm Backend (Spring Boot)
# =========================================================================
# Stage 1: Compilación con Maven + JDK
# Stage 2: Runtime mínimo con JRE
# =========================================================================

# ======================== STAGE 1: BUILD ========================
FROM maven:3.9.9-eclipse-temurin-25 AS builder

LABEL maintainer="LassRiver Team"
LABEL description="Bookworm Backend API - Build Stage"

WORKDIR /app

# Copiar primero el descriptor de Maven para aprovechar cache
COPY pom.xml ./

# Descargar dependencias
RUN mvn dependency:go-offline -B

# Copiar código fuente
COPY src ./src

# Compilar JAR sin tests
RUN mvn clean package -DskipTests -B \
    && mv target/*.jar target/app.jar

# ======================== STAGE 2: RUNTIME ========================
FROM eclipse-temurin:25-jre-alpine AS runtime

LABEL maintainer="LassRiver Team"
LABEL description="Bookworm Backend API - Production"
LABEL version="0.0.1-SNAPSHOT"

RUN addgroup -S appgroup && adduser -S appuser -G appgroup

WORKDIR /app

COPY --from=builder /app/target/app.jar ./app.jar

USER appuser

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=10s --retries=3 --start-period=40s \
    CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"
ENV SPRING_PROFILES_ACTIVE="prod"

ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar app.jar"]