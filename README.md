# 📖 Bookworm Backend API

![Java](https://img.shields.io/badge/Java-25-orange.svg)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-4.0.5-brightgreen.svg)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-14%2B-blue.svg)
![JWT](https://img.shields.io/badge/Security-JWT-black.svg)
![Swagger](https://img.shields.io/badge/Swagger-OpenAPI_3-green.svg)

## 📝 Descripción

**Bookworm Backend** es una API RESTful robusta desarrollada en Java con Spring Boot para la gestión de una biblioteca o catálogo de libros en línea. Permite a los usuarios registrarse, buscar libros, gestionar sus favoritos, y calificar/reseñar obras. Además, incluye funcionalidades administrativas completas para gestionar el catálogo de libros, préstamos de la plataforma y moderar reseñas de la comunidad.

---

## 🛠 Tecnologías Utilizadas

* **Lenguaje:** Java 25
* **Framework Principal:** Spring Boot 4.0.5
  * Spring WebMVC
  * Spring Data JPA
  * Spring Security
  * Spring Validation
* **Base de Datos:** PostgreSQL
* **Migraciones de BDD:** Flyway
* **Seguridad:** JSON Web Tokens (JJWT) y BouncyCastle
* **Documentación API:** SpringDoc OpenAPI (Swagger 3)
* **Utilidades:** Lombok (reducción de boilerplate)

---

## 🏗 Arquitectura del Proyecto

El proyecto sigue una arquitectura en capas limpia, promoviendo la separación de responsabilidades, alta cohesión y bajo acoplamiento:

```text
src/main/java/com/lassriver/bookworm/
├── config/        # Configuraciones globales (Beans, CORS, Swagger)
├── controllers/   # Controladores REST, gestión de requests y responses
├── dtos/          # Data Transfer Objects (Request/Response)
├── entities/      # Entidades de dominio mapeadas a la BDD (JPA)
├── exceptions/    # Manejo global de excepciones (@ControllerAdvice)
├── repositories/  # Interfaces de Spring Data para acceso a datos
├── security/      # Lógica de seguridad, filtros JWT, autenticación
└── services/      # Lógica de negocio y reglas de la aplicación
```

---

## ⚙️ Requisitos Previos

Antes de ejecutar el proyecto, asegúrate de tener instalado:
- **Java JDK 25** (o superior compatible, según lo definido en `pom.xml`)
- **Maven 3.8+** (o puedes usar el wrapper `./mvnw` incluido)
- **PostgreSQL 14+**

---

## 🚀 Configuración y Ejecución Local

1. **Configurar la base de datos PostgreSQL:**
   Crea una base de datos local llamada `bookworm_db` en PostgreSQL.

2. **Revisar / Modificar las credenciales de BDD (`application.yml`):**
   El sistema está configurado para utilizar el perfil `dev` por defecto. Verifica las credenciales en `src/main/resources/application.yml`:
   ```yaml
   datasource:
     url: jdbc:postgresql://localhost:5432/bookworm_db
     username: postgres
     password: <tu_contraseña>
   ```

3. **Compilar el proyecto:**
   Abre una terminal en la raíz del proyecto y ejecuta:
   ```bash
   ./mvnw clean install -DskipTests
   ```

4. **Ejecutar la aplicación:**
   ```bash
   ./mvnw spring-boot:run
   ```
   *Al iniciar, **Flyway** se encargará de ejecutar todas las migraciones necesarias para estructurar y poblar la base de datos automáticamente si tienes scripts SQL definidos.*

---

## 🔐 Seguridad y Autenticación

Toda la aplicación se encuentra securizada siguiendo los lineamientos técnicos del proyecto:
- **JWT (JSON Web Tokens):** Las sesiones se manejan en modo stateless. Debes incluir el token obtenido al iniciar sesión en los Headers de peticiones privadas:
  `Authorization: Bearer <tu_jwt_token>`
- **Hasheo Seguro:** Todas las contraseñas se almacenan mediante algoritmos de cifrado unidireccionales seguros.
- **Role-Based Access Control (RBAC):** Separación estricta de responsabilidades entre el rol `USER` (navegación, favoritos, reseñas) y el rol `ADMIN` (gestión de libros, moderación, préstamos).

---

## 📚 Documentación de API (Swagger)

Una vez que la aplicación se encuentra en ejecución (usualmente en `http://localhost:8080`), puedes acceder de forma interactiva a toda la documentación de los endpoints expuestos mediante Swagger Endpoint:

👉 **[Swagger UI: http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)**

Desde esta interfaz podrás ver y probar cada uno de los requests soportados de forma visual.

---

## 🎯 Resumen de Historias de Usuario Principales

### Funcionalidades de Usuario y Visitante
- **HU-F01:** Registro, Login, Logout y Edición de Perfil.
- **HU-F02:** Listado del catálogo, búsqueda (por título/autor) y filtrado de libros con paginación integrada.
- **HU-F04:** Gestión de "Mis Favoritos" y Creación/Edición de sistema de reseñas (Ratings de 1 a 5).

### Funcionalidades Administrativas
- **HU-F05:** Creación, edición y baja de elementos en el catálogo literario. Gestión visual del ciclo de préstamos de libros y moderación de comentarios de la comunidad.

### Estándares Técnicos Implementados
- Manejo de respuestas y **excepciones globalizadas**.
- Uso estricto de **DTOs** para la comunicación Cliente<->Servidor.
- Gestión de versiones de base de datos a través de **Flyway**.
- Paginación y Filtrado estándar aplicable a las listas grandes (ej: catálogo).
