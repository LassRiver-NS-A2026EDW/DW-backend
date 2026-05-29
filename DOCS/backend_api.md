# Documentación Técnica De La API Backend

## 1. Convenciones Generales

La API vive bajo el prefijo:

```text
/api
```

El frontend usa `VITE_API_BASE_URL` si existe. Si no existe, usa `/api`.

Las respuestas JSON usan nombres de propiedades en `camelCase`.

Las fechas se serializan con formato ISO:

- `LocalDate`: `YYYY-MM-DD`.
- `LocalDateTime`: `YYYY-MM-DDTHH:mm:ss` o formato equivalente emitido por Jackson.

## 2. Autenticación

Las rutas protegidas requieren:

```http
Authorization: Bearer <jwt>
```

El JWT se emite en `POST /api/auth/login`. El token contiene:

- `sub`: email del usuario.
- `role`: rol del usuario.
- `iat`: fecha de emisión.
- `exp`: expiración.

Roles válidos:

```text
USER
LIBRARIAN
ADMIN
```

## 3. Formato General De Errores

### 3.1 Error Normal

```json
{
  "code": "BUSINESS_RULE_VIOLATION",
  "message": "La duracion del prestamo debe estar entre 5 minutos y 7 dias.",
  "severity": "warning",
  "timestamp": "2026-05-29T10:15:30.000Z",
  "path": "/api/loans"
}
```

Propiedades:

- `code`: identificador técnico del error.
- `message`: texto legible para mostrar o registrar.
- `severity`: severidad visual o semántica.
- `timestamp`: instante en que ocurrió.
- `path`: ruta que falló.

### 3.2 Error De Validación

```json
{
  "code": "VALIDATION_ERROR",
  "message": "Request validation failed",
  "severity": "error",
  "timestamp": "2026-05-29T10:15:30.000Z",
  "path": "/api/auth/register",
  "fields": {
    "email": "El formato del correo es inválido",
    "password": "La contraseña debe tener al menos 8 caracteres"
  }
}
```

Propiedades adicionales:

- `fields`: mapa donde la clave es el campo inválido y el valor es el mensaje exacto de validación.

### 3.3 Códigos Comunes

| HTTP | `code` | Cuándo ocurre |
|---|---|---|
| 400 | `VALIDATION_ERROR` | Fallan anotaciones como `@NotBlank`, `@Size`, `@Min`, `@Max`, `@Email` |
| 400 | `BUSINESS_RULE_VIOLATION` | La petición es sintácticamente correcta pero viola reglas del negocio |
| 400 | `BAD_REQUEST` | JSON mal formado o enum inválido |
| 401 | `INVALID_CREDENTIALS` | Login con email o contraseña incorrectos |
| 401 | respuesta de Spring Security | Falta token, token inválido o token expirado en rutas protegidas |
| 403 | `FORBIDDEN` | Usuario autenticado sin permisos |
| 404 | `NOT_FOUND` | Recurso inexistente |
| 409 | `EMAIL_ALREADY_EXISTS` | Registro con correo ya existente |
| 500 | `INTERNAL_SERVER_ERROR` | Error no controlado |

## 4. Entidades Principales

### 4.1 `User`

Tabla: `users`.

Campos principales:

- `id`: identificador.
- `name`: nombre visible.
- `email`: correo único.
- `password`: hash Argon2.
- `role`: `USER`, `LIBRARIAN` o `ADMIN`.
- `language`: idioma preferido.
- `gender`: enum `M`, `F`, `NR`, `OTHER`.
- `birthDate`: fecha de nacimiento.
- `createdAt`: fecha de creación.

Relaciones:

- Un usuario puede tener muchos préstamos.
- Un usuario puede tener muchas reservas.
- Un usuario puede tener muchas reseñas.
- Un usuario puede tener muchos favoritos.
- Un usuario puede tener muchas notificaciones.

### 4.2 `Book`

Tabla: `books`.

Campos:

- `id`
- `title`
- `author`
- `isbn`
- `category`
- `language`
- `status`: `ACTIVE` o `INACTIVE`.
- `coverUrl`
- `publisher`
- `publishDate`
- `pages`
- `description`
- `pdfPath`
- `pdfSourceUrl`
- `pdfOriginalFilename`
- `createdAt`

Relaciones:

- Un libro tiene muchos ejemplares (`BookCopy`).
- Un libro tiene muchos préstamos (`Loan`).
- Un libro tiene muchas reservas (`Reservation`).
- Un libro tiene muchas reseñas (`Review`).
- Un libro puede estar en favoritos de muchos usuarios.

### 4.3 `BookCopy`

Tabla: `book_copies`.

Campos:

- `id`
- `book`
- `copyCode`
- `status`: `AVAILABLE`, `LOANED`, `RESERVED`, `INACTIVE`.
- `createdAt`

Regla crítica:

Un préstamo toma una copia concreta. Esto evita decir solamente “el libro está prestado” y permite manejar múltiples copias del mismo libro.

### 4.4 `Loan`

Tabla: `loans`.

Campos:

- `id`
- `user`
- `book`
- `copy`
- `loanDate`
- `dueDate`
- `returnedAt`
- `status`: `ACTIVE`, `OVERDUE`, `RETURNED`.
- `renewalCount`
- `createdAt`

Reglas:

- Máximo 3 préstamos abiertos por usuario.
- Duración entre 5 minutos y 7 días.
- No se puede crear préstamo si no hay copia disponible.
- No se puede prestar libro inactivo.
- No se puede prestar dos veces el mismo libro al mismo usuario si ya tiene préstamo abierto.
- Hay cooldown de 24 horas después de devolver el mismo libro.

### 4.5 `LoanRenewal`

Tabla: `loan_renewals`.

Campos:

- `id`
- `loan`
- `previousDueDate`
- `newDueDate`
- `durationMinutes`
- `createdAt`

Regla:

Cada renovación queda auditada. No se modifica la fecha sin registrar cómo se llegó a la nueva fecha.

### 4.6 `Reservation`

Tabla: `reservations`.

Campos:

- `id`
- `user`
- `book`
- `fulfilledLoan`
- `status`: `WAITING`, `FULFILLED`, `CANCELLED`.
- `requestedLoanDurationMinutes`
- `createdAt`
- `fulfilledAt`
- `cancelledAt`

Reglas:

- Solo se reserva si no hay copias disponibles.
- Una reserva `WAITING` por usuario y libro.
- La duración solicitada también respeta mínimo 5 minutos y máximo 7 días.
- Cuando se devuelve una copia, la primera reserva en cola puede convertirse en préstamo.

### 4.7 `Review`

Tabla: `reviews`.

Campos:

- `id`
- `user`
- `book`
- `rating`
- `comment`
- `status`: `VISIBLE` o `HIDDEN`.
- `createdAt`
- `updatedAt`

Reglas:

- Una reseña por usuario y libro.
- Rating entre 1 y 5.
- Solo puede reseñar quien haya solicitado el libro en préstamo.
- Ocultar una reseña no la borra.

### 4.8 `UserFavorite`

Tabla: `user_favorites`.

Campos:

- `id`
- `user`
- `book`
- `createdAt`

Regla:

Restricción única `(user_id, book_id)`.

### 4.9 `Notification`

Tabla: `notifications`.

Campos:

- `id`
- `user`
- `type`
- `title`
- `message`
- `targetView`
- `targetId`
- `dedupeKey`
- `readAt`
- `createdAt`

Reglas:

- Pertenece a un usuario.
- `dedupeKey` evita duplicados.
- `readAt = null` significa no leída.

## 5. Auth API

### 5.1 `POST /api/auth/register`

Propósito:

Registra un usuario nuevo, cifra su contraseña y lo deja con rol `USER`.

Autenticación:

No requiere token.

Request body:

```json
{
  "name": "Kevin Rivera",
  "email": "kevin@example.com",
  "password": "Password123!",
  "gender": "M",
  "birthDate": "2000-05-10"
}
```

Tipos:

- `name`: string, obligatorio, 3 a 50 caracteres.
- `email`: string, obligatorio, formato email.
- `password`: string, obligatorio, mínimo 8 caracteres.
- `gender`: enum `M`, `F`, `NR`, `OTHER`.
- `birthDate`: date, obligatorio, pasado y con edad mínima de 13 años.

Respuesta exitosa `201 Created`:

```json
{
  "id": 1,
  "name": "Kevin Rivera",
  "email": "kevin@example.com",
  "message": "Usuario registrado exitosamente.",
  "gender": "M",
  "birthDate": "2000-05-10"
}
```

Entidades:

- `User`: se crea.

Reglas:

- Email único.
- Password cifrada con Argon2.
- Rol inicial `USER`.
- Idioma inicial `es`.
- Edad mínima 13 años.

Errores:

- `400 VALIDATION_ERROR`: campos faltantes o inválidos.
- `400 BUSINESS_RULE_VIOLATION`: edad menor a 13 años.
- `409 EMAIL_ALREADY_EXISTS`: email ya registrado.
- `500 INTERNAL_SERVER_ERROR`: fallo inesperado.

### 5.2 `POST /api/auth/login`

Propósito:

Autentica credenciales y entrega JWT.

Autenticación:

No requiere token.

Request body:

```json
{
  "email": "kevin@example.com",
  "password": "Password123!"
}
```

Respuesta exitosa `200 OK`:

```json
{
  "token": "jwt.token.value",
  "id": 1,
  "name": "Kevin Rivera",
  "role": "USER"
}
```

Entidades:

- `User`: se consulta por email.

Reglas:

- Spring Security compara contraseña plana contra hash Argon2.
- El token expira después de 1 día.

Errores:

- `400 VALIDATION_ERROR`: email o password vacíos.
- `401 INVALID_CREDENTIALS`: credenciales incorrectas.
- `500 INTERNAL_SERVER_ERROR`: fallo inesperado.

## 6. Users API

### 6.1 `GET /api/users/me`

Propósito:

Devuelve el perfil del usuario autenticado.

Headers:

```http
Authorization: Bearer <jwt>
```

Respuesta `200 OK`:

```json
{
  "id": 1,
  "name": "Kevin Rivera",
  "email": "kevin@example.com",
  "role": "USER",
  "gender": "M",
  "birthDate": "2000-05-10"
}
```

Entidades:

- `User`: se consulta por el email del token.

Errores:

- `401`: sin token válido.
- `404 NOT_FOUND`: usuario del token no existe.
- `500 INTERNAL_SERVER_ERROR`.

### 6.2 `PUT /api/users/me`

Propósito:

Actualiza el nombre del perfil. El email se recibe pero no puede cambiar en esta fase.

Headers:

```http
Authorization: Bearer <jwt>
```

Request body:

```json
{
  "name": "Kevin Rivera",
  "email": "kevin@example.com"
}
```

Respuesta `200 OK`:

```json
{
  "id": 1,
  "name": "Kevin Rivera",
  "email": "kevin@example.com",
  "role": "USER",
  "gender": "M",
  "birthDate": "2000-05-10"
}
```

Entidades:

- `User`: se actualiza `name`.

Reglas:

- Si el email enviado es diferente al email actual, se rechaza.

Errores:

- `400 VALIDATION_ERROR`: nombre o email inválidos.
- `400 BUSINESS_RULE_VIOLATION`: intento de cambio de correo.
- `401`: sin token válido.
- `404 NOT_FOUND`: usuario no encontrado.
- `500 INTERNAL_SERVER_ERROR`.

### 6.3 `PATCH /api/users/me/password`

Propósito:

Cambia la contraseña del usuario autenticado.

Request body:

```json
{
  "currentPassword": "Password123!",
  "newPassword": "NewPassword123!"
}
```

Respuesta `204 No Content`.

Entidades:

- `User`: se actualiza `password`.

Reglas:

- La contraseña actual debe coincidir.
- La nueva contraseña debe tener mínimo 8 caracteres.
- La nueva contraseña debe ser diferente a la actual.
- La nueva contraseña se guarda cifrada.

Errores:

- `400 VALIDATION_ERROR`: campos faltantes o nueva contraseña corta.
- `400 BUSINESS_RULE_VIOLATION`: contraseña actual incorrecta o nueva igual a la actual.
- `401`.
- `404 NOT_FOUND`.
- `500 INTERNAL_SERVER_ERROR`.

## 7. Books API

### 7.1 `GET /api/books`

Propósito:

Lista libros paginados con filtros combinables.

Autenticación:

Opcional. Si hay token, la respuesta puede incluir datos del usuario como cooldown o préstamo activo.

Query params:

- `search`: busca en título, autor, ISBN y descripción.
- `title`: filtro específico por título.
- `category`: filtro parcial por categoría.
- `language`: filtro exacto por idioma.
- `status`: `ACTIVE` o `INACTIVE`.
- `availability`: `available`, `unavailable` o ausente.
- `page`: número de página.
- `size`: tamaño de página.
- `sort`: orden Spring, por ejemplo `title,asc`.

Respuesta `200 OK`:

```json
{
  "content": [
    {
      "id": 1,
      "title": "Cien Años de Soledad",
      "author": "Gabriel García Márquez",
      "isbn": "978-0307474728",
      "category": "Ficción",
      "language": "Español",
      "status": "ACTIVE",
      "coverUrl": "https://example.com/cover.jpg",
      "publisher": "Editorial Sudamericana",
      "publishDate": "1967-05-30",
      "pages": 417,
      "description": "Descripción del libro.",
      "rating": 4.5,
      "reviewCount": 2,
      "hasPdf": true,
      "pdfUrl": "/api/books/1/pdf",
      "reservedByMe": false,
      "loanCooldownUntil": null,
      "totalCopies": 3,
      "availableCopies": 1,
      "waitingReservations": 0,
      "createdAt": "2026-05-29T10:00:00"
    }
  ],
  "totalElements": 1,
  "totalPages": 1,
  "number": 0,
  "size": 8
}
```

Entidades:

- `Book`
- `BookCopy`
- `Review`
- `Reservation`
- `Loan`
- `User`, si hay token.

Reglas:

- `availability=available`: libro activo y al menos una copia `AVAILABLE`.
- `availability=unavailable`: libro inactivo o sin copias disponibles.
- Rating y conteo solo consideran reseñas `VISIBLE`.

Errores:

- `400 BUSINESS_RULE_VIOLATION`: disponibilidad inválida.
- `500 INTERNAL_SERVER_ERROR`.

### 7.2 `GET /api/books/{id}`

Propósito:

Obtiene detalle completo de un libro.

Path params:

- `id`: long.

Respuesta:

```json
{
  "id": 1,
  "title": "Cien Años de Soledad",
  "author": "Gabriel García Márquez",
  "isbn": "978-0307474728",
  "category": "Ficción",
  "language": "Español",
  "status": "ACTIVE",
  "coverUrl": "https://example.com/cover.jpg",
  "publisher": "Editorial Sudamericana",
  "publishDate": "1967-05-30",
  "pages": 417,
  "description": "Descripción del libro.",
  "rating": 4.5,
  "reviewCount": 2,
  "hasPdf": true,
  "pdfUrl": "/api/books/1/pdf",
  "reservedByMe": false,
  "loanCooldownUntil": null,
  "totalCopies": 3,
  "availableCopies": 1,
  "waitingReservations": 0,
  "createdAt": "2026-05-29T10:00:00"
}
```

Entidades:

- `Book`, `Review`, `BookCopy`, `Reservation`, `Loan`.

Errores:

- `404 NOT_FOUND`: libro inexistente.
- `500 INTERNAL_SERVER_ERROR`.

### 7.3 `GET /api/books/facets`

Propósito:

Devuelve categorías e idiomas disponibles para filtros.

Autenticación:

No requiere token.

Respuesta:

```json
{
  "categories": ["Clásicos", "Ficción", "Romance"],
  "languages": ["Español"]
}
```

Entidades:

- `Book`: consulta valores distintos de `category` y `language`.

Errores:

- `500 INTERNAL_SERVER_ERROR`.

### 7.4 `GET /api/books/{id}/availability`

Propósito:

Obtiene inventario operativo de un libro.

Respuesta:

```json
{
  "bookId": 1,
  "totalCopies": 3,
  "availableCopies": 1,
  "loanedCopies": 2,
  "reservedCopies": 0,
  "waitingReservations": 4
}
```

Entidades:

- `Book`
- `BookCopy`
- `Reservation`

Errores:

- `404 NOT_FOUND`: libro inexistente.
- `500 INTERNAL_SERVER_ERROR`.

### 7.5 `GET /api/books/{id}/copies`

Propósito:

Lista ejemplares de un libro para administración.

Autenticación:

Requiere `ADMIN` o `LIBRARIAN`.

Respuesta:

```json
[
  {
    "id": 10,
    "bookId": 1,
    "bookTitle": "Cien Años de Soledad",
    "copyCode": "BOOK-1-COPY-1",
    "status": "AVAILABLE",
    "createdAt": "2026-05-29T10:00:00"
  }
]
```

Entidades:

- `Book`
- `BookCopy`

Errores:

- `401`.
- `403 FORBIDDEN`.
- `404 NOT_FOUND`.
- `500 INTERNAL_SERVER_ERROR`.

### 7.6 `POST /api/books/{id}/copies`

Propósito:

Crea un ejemplar disponible para un libro activo.

Autenticación:

Requiere `ADMIN` o `LIBRARIAN`.

Request body:

No tiene body.

Respuesta `201 Created`:

```json
{
  "id": 11,
  "bookId": 1,
  "bookTitle": "Cien Años de Soledad",
  "copyCode": "BOOK-1-COPY-2",
  "status": "AVAILABLE",
  "createdAt": "2026-05-29T10:00:00"
}
```

Entidades:

- `Book`: se bloquea.
- `BookCopy`: se crea.

Reglas:

- No se agregan copias a libros inactivos.
- El código se genera como `BOOK-{bookId}-COPY-{n}`.

Errores:

- `400 BUSINESS_RULE_VIOLATION`: libro inactivo.
- `401`.
- `403`.
- `404`.
- `500`.

### 7.7 `DELETE /api/books/{id}/copies/{copyId}`

Propósito:

Retira un ejemplar. No lo borra físicamente; cambia estado a `INACTIVE`.

Autenticación:

Requiere `ADMIN` o `LIBRARIAN`.

Respuesta:

```json
{
  "id": 11,
  "bookId": 1,
  "bookTitle": "Cien Años de Soledad",
  "copyCode": "BOOK-1-COPY-2",
  "status": "INACTIVE",
  "createdAt": "2026-05-29T10:00:00"
}
```

Entidades:

- `Book`
- `BookCopy`
- `Reservation`

Reglas:

- No se retira si hay reservas `WAITING`.
- Solo se retiran copias `AVAILABLE`.
- No se retira el último ejemplar operativo.

Errores:

- `400 BUSINESS_RULE_VIOLATION`: copia no disponible, cola activa o último ejemplar.
- `401`.
- `403`.
- `404`.
- `500`.

### 7.8 `POST /api/books`

Propósito:

Crea un libro.

Autenticación:

Requiere `ADMIN` o `LIBRARIAN`.

Request body:

```json
{
  "title": "Cien Años de Soledad",
  "author": "Gabriel García Márquez",
  "isbn": "978-0307474728",
  "category": "Ficción",
  "language": "Español",
  "coverUrl": "https://example.com/cover.jpg",
  "publisher": "Editorial Sudamericana",
  "publishDate": "1967-05-30",
  "pages": 417,
  "description": "Descripción del libro."
}
```

Respuesta `201 Created`:

Devuelve `BookResponse`.

Entidades:

- `Book`: se crea.
- `BookCopy`: se crea copia inicial si el libro queda activo.

Reglas:

- ISBN único por restricción de tabla.
- `status` por defecto `ACTIVE`.
- Si el libro está activo y no tiene copias, se crea una copia inicial.

Errores:

- `400 VALIDATION_ERROR`.
- `401`.
- `403`.
- `500`.

### 7.9 `PUT /api/books/{id}`

Propósito:

Actualiza metadatos del libro.

Autenticación:

Requiere `ADMIN` o `LIBRARIAN`.

Request body:

Mismo formato de `BookUpsertRequest`.

Respuesta:

Devuelve `BookResponse`.

Entidades:

- `Book`.

Errores:

- `400 VALIDATION_ERROR`.
- `401`.
- `403`.
- `404`.
- `500`.

### 7.10 `PATCH /api/books/{id}/status?status=ACTIVE`

Propósito:

Cambia estado del libro.

Autenticación:

Requiere `ADMIN` o `LIBRARIAN`.

Query params:

- `status`: `ACTIVE` o `INACTIVE`. Si se omite, el backend usa `INACTIVE`.

Respuesta:

Devuelve `BookResponse`.

Reglas:

- Solo se aceptan `ACTIVE` e `INACTIVE`.
- Si se activa un libro sin copias operativas, se asegura una copia inicial.

Errores:

- `400 BUSINESS_RULE_VIOLATION`: estado inválido.
- `401`.
- `403`.
- `404`.
- `500`.

### 7.11 `DELETE /api/books/{id}`

Propósito:

Elimina un libro y sus datos dependientes cuando no hay operaciones vivas.

Autenticación:

Requiere `ADMIN` o `LIBRARIAN`.

Respuesta:

`204 No Content`.

Entidades:

- `Book`
- `BookCopy`
- `Loan`
- `LoanRenewal`
- `Reservation`
- `Review`
- `UserFavorite`

Reglas:

- No elimina si hay préstamos `ACTIVE` u `OVERDUE`.
- No elimina si hay reservas `WAITING`.
- Borra relaciones en orden para respetar claves foráneas.

Errores:

- `400 BUSINESS_RULE_VIOLATION`: préstamos activos/vencidos o cola activa.
- `401`.
- `403`.
- `404`.
- `500`.

### 7.12 `POST /api/books/{id}/pdf/upload`

Propósito:

Sube archivo PDF local para un libro.

Autenticación:

Requiere `ADMIN` o `LIBRARIAN`.

Content-Type:

```http
multipart/form-data
```

Campos:

- `file`: archivo PDF.

Respuesta:

```json
{
  "bookId": 1,
  "message": "PDF subido y guardado exitosamente.",
  "filename": "Cien_Anos_de_Soledad_ab12cd34.pdf",
  "pdfUrl": "/api/books/1/pdf"
}
```

Entidades:

- `Book`: actualiza ruta y nombre de PDF.

Reglas:

- Archivo obligatorio.
- Debe parecer PDF por extensión o content-type.
- Respeta tamaño máximo configurado.
- El archivo anterior se intenta borrar.

Errores:

- `400 BUSINESS_RULE_VIOLATION`: archivo ausente, no PDF, supera tamaño o falla almacenamiento.
- `401`.
- `403`.
- `404`.
- `500`.

### 7.13 `POST /api/books/{id}/pdf/download`

Propósito:

Descarga un PDF desde URL HTTPS permitida y lo asocia al libro.

Autenticación:

Requiere `ADMIN` o `LIBRARIAN`.

Request body:

```json
{
  "url": "https://allowed-host.example/book.pdf"
}
```

Respuesta:

```json
{
  "bookId": 1,
  "message": "PDF descargado y guardado exitosamente.",
  "filename": "Cien_Anos_de_Soledad_ab12cd34.pdf",
  "pdfUrl": "/api/books/1/pdf"
}
```

Reglas:

- Solo HTTPS.
- Sin user info.
- Sin fragment.
- Solo puerto 443 o puerto por defecto.
- Host debe estar en `bookworm.storage.allowed-pdf-hosts`.
- Host no puede resolver a IP privada o local.
- Máximo 3 redirecciones.
- Debe ser PDF.
- Respeta tamaño máximo.

Errores:

- `400 VALIDATION_ERROR`: URL vacía.
- `400 BUSINESS_RULE_VIOLATION`: URL insegura, host no permitido, no PDF, redirecciones excesivas.
- `401`.
- `403`.
- `404`.
- `500`.

### 7.14 `GET /api/books/{id}/pdf`

Propósito:

Sirve el PDF del libro para lectura dentro de la app.

Autenticación:

Requiere token.

Respuesta:

```http
200 OK
Content-Type: application/pdf
Content-Disposition: inline; filename="book.pdf"
```

Body:

Archivo PDF binario.

Reglas:

- Admin/librarian puede leer.
- Usuario normal necesita préstamo `ACTIVE` del libro.
- El libro debe tener PDF.
- El archivo debe existir.

Errores:

- `401`.
- `403 FORBIDDEN`: usuario sin préstamo activo.
- `404 NOT_FOUND`: libro sin PDF o archivo ausente.
- `500`.

## 8. Loans API

### 8.1 `GET /api/loans`

Propósito:

Lista todos los préstamos para administración.

Autenticación:

Requiere `ADMIN` o `LIBRARIAN`.

Respuesta:

```json
[
  {
    "id": 1,
    "userId": 1,
    "userEmail": "kevin@example.com",
    "bookId": 1,
    "bookTitle": "Cien Años de Soledad",
    "bookIsbn": "978-0307474728",
    "copyId": 10,
    "copyCode": "BOOK-1-COPY-1",
    "loanDate": "2026-05-29T10:00:00",
    "dueDate": "2026-05-30T10:00:00",
    "returnedAt": null,
    "status": "ACTIVE",
    "renewalCount": 0,
    "canRenew": true,
    "blockedReason": null,
    "createdAt": "2026-05-29T10:00:00"
  }
]
```

Errores:

- `401`.
- `403`.
- `500`.

### 8.2 `GET /api/loans/my-loans`

Propósito:

Lista préstamos del usuario autenticado.

Autenticación:

Requiere token.

Respuesta:

Lista de `LoanResponse`.

Entidades:

- `User`
- `Loan`
- `Book`
- `BookCopy`
- `Reservation`, para calcular `canRenew`.

Errores:

- `401`.
- `404`.
- `500`.

### 8.3 `POST /api/loans`

Propósito:

Crea un préstamo sobre una copia disponible.

Request body:

```json
{
  "bookId": 1,
  "durationMinutes": 1440
}
```

Respuesta `201 Created`:

Devuelve `LoanResponse`.

Entidades:

- `User`
- `Book`
- `BookCopy`
- `Loan`
- `Notification`

Reglas:

- Duración entre 5 y 10080 minutos.
- Libro activo.
- Copia disponible.
- Usuario sin préstamo abierto para el mismo libro.
- Usuario sin cooldown activo para ese libro.
- Máximo 3 préstamos abiertos.
- La copia pasa a `LOANED`.
- Se notifica al usuario y al staff.

Errores:

- `400 VALIDATION_ERROR`.
- `400 BUSINESS_RULE_VIOLATION`: sin copias, libro inactivo, duplicado, cooldown o límite de préstamos.
- `401`.
- `404`.
- `500`.

### 8.4 `PUT /api/loans/{id}/return`

Propósito:

Devuelve un préstamo.

Respuesta:

Devuelve `LoanResponse` del préstamo devuelto.

Entidades:

- `Loan`
- `BookCopy`
- `Reservation`
- `Notification`

Reglas:

- Dueño del préstamo o staff.
- No se puede devolver dos veces.
- La copia vuelve a `AVAILABLE`.
- Si hay reserva en cola, se asigna automáticamente.
- Se crean notificaciones.

Errores:

- `400 BUSINESS_RULE_VIOLATION`: préstamo ya devuelto.
- `401`.
- `403`: devolver préstamo ajeno sin rol.
- `404`.
- `500`.

### 8.5 `POST /api/loans/{id}/renew`

Propósito:

Renueva un préstamo.

Request body:

```json
{
  "durationMinutes": 1440
}
```

Respuesta:

Devuelve `LoanResponse`.

Entidades:

- `Loan`
- `LoanRenewal`
- `Reservation`
- `Notification`

Reglas:

- Dueño o staff.
- Duración entre 5 y 10080.
- No préstamo devuelto.
- No préstamo vencido.
- Máximo 2 renovaciones.
- No renovar si hay reservas esperando el libro.

Errores:

- `400 VALIDATION_ERROR`.
- `400 BUSINESS_RULE_VIOLATION`.
- `401`.
- `403`.
- `404`.
- `500`.

### 8.6 `GET /api/loans/{id}/history`

Propósito:

Consulta historial de renovaciones.

Respuesta:

```json
[
  {
    "id": 1,
    "loanId": 1,
    "previousDueDate": "2026-05-30T10:00:00",
    "newDueDate": "2026-05-31T10:00:00",
    "durationMinutes": 1440,
    "createdAt": "2026-05-29T11:00:00"
  }
]
```

Reglas:

- Dueño del préstamo o staff.

Errores:

- `401`.
- `403`.
- `404`.
- `500`.

## 9. Reservations API

### 9.1 `GET /api/reservations/my`

Propósito:

Lista reservas del usuario autenticado.

Respuesta:

```json
[
  {
    "id": 1,
    "userId": 1,
    "userEmail": "kevin@example.com",
    "bookId": 1,
    "bookTitle": "Cien Años de Soledad",
    "status": "WAITING",
    "requestedLoanDurationMinutes": 1440,
    "queuePosition": 1,
    "fulfilledLoanId": null,
    "createdAt": "2026-05-29T10:00:00",
    "fulfilledAt": null,
    "cancelledAt": null
  }
]
```

Entidades:

- `User`
- `Reservation`
- `Book`
- `Loan`

Errores:

- `401`.
- `404`.
- `500`.

### 9.2 `POST /api/reservations`

Propósito:

Une al usuario a la cola de reserva de un libro sin copias disponibles.

Request body:

```json
{
  "bookId": 1,
  "requestedLoanDurationMinutes": 1440
}
```

Respuesta `201 Created`:

Devuelve `ReservationResponse`.

Reglas:

- Duración entre 5 y 10080.
- Libro activo.
- No debe haber copias disponibles.
- Usuario sin préstamo abierto del libro.
- Usuario sin cooldown de 24 horas.
- Usuario sin reserva `WAITING` para ese libro.
- Crea notificación.

Errores:

- `400 VALIDATION_ERROR`.
- `400 BUSINESS_RULE_VIOLATION`.
- `401`.
- `404`.
- `500`.

### 9.3 `DELETE /api/reservations/{id}`

Propósito:

Cancela una reserva pendiente.

Respuesta:

Devuelve `ReservationResponse` con estado `CANCELLED`.

Reglas:

- Solo reservas `WAITING`.
- Dueño o staff.
- Asigna `cancelledAt`.
- Crea notificación.

Errores:

- `401`.
- `403`.
- `404`.
- `500`.

## 10. Reviews API

### 10.1 `GET /api/reviews/book/{bookId}`

Propósito:

Lista reseñas visibles de un libro.

Autenticación:

No requiere token.

Respuesta:

```json
[
  {
    "id": 1,
    "userId": 1,
    "userName": "Kevin Rivera",
    "userEmail": "kevin@example.com",
    "bookId": 1,
    "bookTitle": "Cien Años de Soledad",
    "rating": 5,
    "comment": "Excelente libro.",
    "status": "VISIBLE",
    "createdAt": "2026-05-29T10:00:00",
    "updatedAt": "2026-05-29T10:00:00"
  }
]
```

Errores:

- `404 NOT_FOUND`: libro inexistente.
- `500`.

### 10.2 `GET /api/reviews?status=VISIBLE`

Propósito:

Lista reseñas para administración.

Autenticación:

Requiere `ADMIN` o `LIBRARIAN`.

Query params:

- `status`: `VISIBLE`, `HIDDEN` o `ALL`. Si se omite, usa `VISIBLE`.

Respuesta:

Lista de `ReviewResponse`.

Errores:

- `401`.
- `403`.
- `500`.

### 10.3 `POST /api/reviews`

Propósito:

Crea reseña para un libro.

Request body:

```json
{
  "bookId": 1,
  "rating": 5,
  "comment": "Excelente libro."
}
```

Respuesta `201 Created`:

Devuelve `ReviewResponse`.

Reglas:

- Rating de 1 a 5.
- Comentario obligatorio, máximo 2000.
- Una reseña por usuario y libro.
- Debe existir algún préstamo del usuario para ese libro.
- Estado inicial `VISIBLE`.

Errores:

- `400 VALIDATION_ERROR`.
- `400 BUSINESS_RULE_VIOLATION`: reseña duplicada o usuario sin préstamo.
- `401`.
- `404`.
- `500`.

### 10.4 `PATCH /api/reviews/{id}/hide`

Propósito:

Oculta reseña.

Autenticación:

Requiere `ADMIN` o `LIBRARIAN`.

Respuesta:

Devuelve `ReviewResponse` con `status = "HIDDEN"`.

Errores:

- `401`.
- `403`.
- `404`.
- `500`.

### 10.5 `PATCH /api/reviews/{id}/show`

Propósito:

Vuelve visible una reseña.

Autenticación:

Requiere `ADMIN` o `LIBRARIAN`.

Respuesta:

Devuelve `ReviewResponse` con `status = "VISIBLE"`.

Errores:

- `401`.
- `403`.
- `404`.
- `500`.

## 11. Favorites API

### 11.1 `GET /api/favorites`

Propósito:

Lista favoritos del usuario autenticado.

Respuesta:

```json
[
  {
    "id": 1,
    "bookId": 1,
    "title": "Cien Años de Soledad",
    "author": "Gabriel García Márquez",
    "isbn": "978-0307474728",
    "status": "ACTIVE",
    "favoritedAt": "2026-05-29T10:00:00"
  }
]
```

Errores:

- `401`.
- `404`.
- `500`.

### 11.2 `POST /api/favorites/{bookId}`

Propósito:

Agrega o quita un libro de favoritos.

Respuesta:

```json
{
  "bookId": 1,
  "favorite": true
}
```

Si ya existía favorito:

```json
{
  "bookId": 1,
  "favorite": false
}
```

Entidades:

- `User`
- `Book`
- `UserFavorite`

Reglas:

- Toggle transaccional.
- Restricción única impide duplicados.

Errores:

- `401`.
- `404`.
- `500`.

## 12. Notifications API

### 12.1 `GET /api/notifications`

Propósito:

Lista notificaciones del usuario autenticado.

Query params:

- `status`: `all` o `unread`. Default `all`.
- `page`: default `0`.
- `size`: default `20`, máximo real `50`.

Respuesta:

```json
{
  "content": [
    {
      "id": 1,
      "type": "LOAN_CREATED",
      "title": "Prestamo creado",
      "message": "Tu prestamo de \"Cien Años de Soledad\" fue creado.",
      "targetView": "loans",
      "targetId": 1,
      "readAt": null,
      "createdAt": "2026-05-29T10:00:00"
    }
  ],
  "totalElements": 1,
  "totalPages": 1,
  "number": 0,
  "size": 20
}
```

Reglas:

- Solo devuelve notificaciones del usuario autenticado.
- `status=unread` filtra `readAt is null`.

Errores:

- `400 BUSINESS_RULE_VIOLATION`: status distinto de `all` o `unread`.
- `401`.
- `404`.
- `500`.

### 12.2 `GET /api/notifications/unread-count`

Propósito:

Devuelve número de notificaciones no leídas.

Respuesta:

```json
{
  "unreadCount": 3
}
```

Errores:

- `401`.
- `404`.
- `500`.

### 12.3 `PATCH /api/notifications/{id}/read`

Propósito:

Marca una notificación como leída.

Respuesta:

Devuelve `NotificationResponse` con `readAt` asignado.

Reglas:

- Solo puede marcar notificaciones propias.
- Si ya estaba leída, conserva `readAt`.

Errores:

- `401`.
- `404 NOT_FOUND`: notificación inexistente o de otro usuario.
- `500`.

### 12.4 `PATCH /api/notifications/read-all`

Propósito:

Marca todas las notificaciones no leídas del usuario como leídas.

Respuesta:

```json
{
  "unreadCount": 0
}
```

Errores:

- `401`.
- `404`.
- `500`.

## 13. Chat API

### 13.1 `POST /api/chat`

Propósito:

Envía pregunta al asistente IA y devuelve respuesta por streaming SSE.

Autenticación:

Requiere token.

Produces:

```http
text/event-stream
```

Request body:

```json
{
  "bookId": 1,
  "selectedText": "Fragmento seleccionado por el usuario.",
  "context": "Descripción o contexto del libro.",
  "question": "¿Qué significa este fragmento?",
  "providerApiKey": "sk-personal",
  "history": [
    {
      "role": "user",
      "content": "Pregunta previa"
    },
    {
      "role": "assistant",
      "content": "Respuesta previa"
    }
  ]
}
```

Tipos:

- `bookId`: long opcional.
- `selectedText`: string opcional, máximo 6000.
- `context`: string opcional, máximo 6000.
- `question`: string obligatorio, máximo 2000.
- `providerApiKey`: string opcional, máximo 300.
- `history`: lista opcional de mensajes.
- `history[].role`: `user` o `assistant`.
- `history[].content`: string.

Respuesta exitosa:

```text
data: "Primer fragmento"

data: "Segundo fragmento"

data: [DONE]
```

Error en stream:

```text
data: [ERROR] No se pudo contactar el proveedor IA.
```

Entidades:

- `User`
- `Book`, si se envía `bookId`.
- `Loan`, para validar préstamo activo.

Reglas:

- Si hay `bookId`, usuario normal necesita préstamo `ACTIVE`.
- Admin/librarian puede usar chat sin préstamo.
- El backend intenta clave IA del sistema.
- Si falla o no existe, puede usar `providerApiKey`.
- Solo roles de historial `user` y `assistant` pasan al proveedor.

Errores HTTP:

- `400 VALIDATION_ERROR`: pregunta faltante o campos demasiado largos.
- `401`.
- `403`: sin préstamo activo para ese libro.
- `404`: libro o usuario no encontrado.
- `500`.

