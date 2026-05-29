# Backend API Documentation

## 1. General Conventions

The backend API uses the base prefix:

```text
/api
```

Request and response bodies are JSON unless stated otherwise. Property names use `camelCase`.

Date conventions:

- `LocalDate`: `YYYY-MM-DD`
- `LocalDateTime`: ISO date-time, for example `2026-05-29T10:00:00`

Protected endpoints require:

```http
Authorization: Bearer <jwt>
```

Main roles:

- `USER`
- `LIBRARIAN`
- `ADMIN`

## 2. Error Format

Most controlled errors use:

```json
{
  "code": "BUSINESS_RULE_VIOLATION",
  "message": "The business rule message.",
  "severity": "warning",
  "timestamp": "2026-05-29T10:00:00Z",
  "path": "/api/loans"
}
```

Validation errors use:

```json
{
  "code": "VALIDATION_ERROR",
  "message": "Request validation failed",
  "severity": "error",
  "timestamp": "2026-05-29T10:00:00Z",
  "path": "/api/auth/register",
  "fields": {
    "email": "Invalid email format"
  }
}
```

Common status codes:

| HTTP | Code | Meaning |
|---|---|---|
| 400 | `VALIDATION_ERROR` | DTO validation failed |
| 400 | `BUSINESS_RULE_VIOLATION` | The request violates a domain rule |
| 400 | `BAD_REQUEST` | Malformed JSON or invalid enum value |
| 401 | `INVALID_CREDENTIALS` | Invalid login credentials |
| 401 | Security response | Missing, invalid, or expired token |
| 403 | `FORBIDDEN` | Authenticated user lacks permission |
| 404 | `NOT_FOUND` | Resource does not exist |
| 409 | `EMAIL_ALREADY_EXISTS` | Email is already registered |
| 500 | `INTERNAL_SERVER_ERROR` | Unexpected server error |

## 3. Database Entities

### 3.1 `User`

Table: `users`.

Fields:

- `id`
- `name`
- `email`
- `password`
- `role`
- `language`
- `gender`
- `birthDate`
- `createdAt`

Relationships:

- Has many loans.
- Has many reservations.
- Has many reviews.
- Has many favorites.
- Has many notifications.

### 3.2 `Book`

Table: `books`.

Fields:

- `id`
- `title`
- `author`
- `isbn`
- `category`
- `language`
- `status`
- `coverUrl`
- `publisher`
- `publishDate`
- `pages`
- `description`
- `pdfPath`
- `pdfSourceUrl`
- `pdfOriginalFilename`
- `createdAt`

Relationships:

- Has many copies.
- Has many loans.
- Has many reservations.
- Has many reviews.
- Has many favorites through `UserFavorite`.

### 3.3 `BookCopy`

Table: `book_copies`.

Statuses:

- `AVAILABLE`
- `LOANED`
- `RESERVED`
- `INACTIVE`

Purpose:

A copy is the concrete resource that can be borrowed. Multiple users can borrow the same book only if multiple copies exist.

### 3.4 `Loan`

Table: `loans`.

Statuses:

- `ACTIVE`
- `OVERDUE`
- `RETURNED`

Important fields:

- `user`
- `book`
- `copy`
- `loanDate`
- `dueDate`
- `returnedAt`
- `renewalCount`

### 3.5 `LoanRenewal`

Table: `loan_renewals`.

Stores every renewal with previous due date, new due date, duration, and creation date.

### 3.6 `Reservation`

Table: `reservations`.

Statuses:

- `WAITING`
- `FULFILLED`
- `CANCELLED`

Reservations create a queue when no copies are available.

### 3.7 `Review`

Table: `reviews`.

Statuses:

- `VISIBLE`
- `HIDDEN`

There is a unique constraint for one review per user and book.

### 3.8 `UserFavorite`

Table: `user_favorites`.

Represents a user-book favorite relation. The `(user_id, book_id)` pair is unique.

### 3.9 `Notification`

Table: `notifications`.

Stores persistent notifications. `readAt = null` means unread. `dedupeKey` prevents duplicates.

## 4. Auth Endpoints

### 4.1 `POST /api/auth/register`

Purpose:

Creates a new regular user.

Authentication:

Public.

Request:

```json
{
  "name": "Kevin Rivera",
  "email": "kevin@example.com",
  "password": "Password123!",
  "gender": "M",
  "birthDate": "2000-05-10"
}
```

Response `201 Created`:

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

Rules:

- Name is required and must have 3 to 50 characters.
- Email is required and must be unique.
- Password requires at least 8 characters.
- Gender must be `M`, `F`, `NR`, or `OTHER`.
- Birth date must be in the past and represent at least 13 years of age.
- Password is stored with Argon2.
- Role is initialized as `USER`.

Errors:

- `400 VALIDATION_ERROR`
- `400 BUSINESS_RULE_VIOLATION`
- `409 EMAIL_ALREADY_EXISTS`
- `500 INTERNAL_SERVER_ERROR`

### 4.2 `POST /api/auth/login`

Purpose:

Authenticates the user and returns a JWT.

Authentication:

Public.

Request:

```json
{
  "email": "kevin@example.com",
  "password": "Password123!"
}
```

Response `200 OK`:

```json
{
  "token": "jwt.token.value",
  "id": 1,
  "name": "Kevin Rivera",
  "role": "USER"
}
```

Errors:

- `400 VALIDATION_ERROR`
- `401 INVALID_CREDENTIALS`
- `500 INTERNAL_SERVER_ERROR`

## 5. User Endpoints

### 5.1 `GET /api/users/me`

Purpose:

Returns the authenticated user's profile.

Authentication:

Required.

Response:

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

Errors:

- `401`
- `404 NOT_FOUND`
- `500 INTERNAL_SERVER_ERROR`

### 5.2 `PUT /api/users/me`

Purpose:

Updates the authenticated user's profile name. Email changes are intentionally blocked in this phase.

Request:

```json
{
  "name": "Kevin Rivera",
  "email": "kevin@example.com"
}
```

Response:

Returns `UserProfileResponse`.

Errors:

- `400 VALIDATION_ERROR`
- `400 BUSINESS_RULE_VIOLATION` when attempting to change email
- `401`
- `404`
- `500`

### 5.3 `PATCH /api/users/me/password`

Purpose:

Changes the authenticated user's password.

Request:

```json
{
  "currentPassword": "Password123!",
  "newPassword": "NewPassword123!"
}
```

Response:

`204 No Content`.

Rules:

- Current password must match.
- New password must have at least 8 characters.
- New password must be different from the current one.
- New password is stored as a hash.

## 6. Book Endpoints

### 6.1 `GET /api/books`

Purpose:

Lists books with pagination and filters.

Authentication:

Optional.

Query parameters:

- `search`
- `title`
- `category`
- `language`
- `status`
- `availability`: `available` or `unavailable`
- `page`
- `size`
- `sort`

Response:

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
      "description": "Book description.",
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

Rules:

- `available` means active book with at least one available copy.
- `unavailable` means inactive book or no available copies.
- Rating and review count include visible reviews only.

### 6.2 `GET /api/books/{id}`

Purpose:

Returns a single book with aggregate availability, rating, PDF, and user-context fields.

Response:

Returns `BookResponse`.

Errors:

- `404 NOT_FOUND`

### 6.3 `GET /api/books/facets`

Purpose:

Returns all category and language facets.

Response:

```json
{
  "categories": ["Clásicos", "Ficción", "Romance"],
  "languages": ["Español"]
}
```

### 6.4 `GET /api/books/{id}/availability`

Purpose:

Returns copy and queue availability.

Response:

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

### 6.5 `GET /api/books/{id}/copies`

Purpose:

Lists copies for a book.

Authentication:

Requires `ADMIN` or `LIBRARIAN`.

Response:

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

### 6.6 `POST /api/books/{id}/copies`

Purpose:

Creates a new available copy.

Authentication:

Requires `ADMIN` or `LIBRARIAN`.

Rules:

- The book must be active.
- The copy code is generated by the backend.

### 6.7 `DELETE /api/books/{id}/copies/{copyId}`

Purpose:

Retires a copy by changing its status to `INACTIVE`.

Authentication:

Requires `ADMIN` or `LIBRARIAN`.

Rules:

- There must be no waiting reservation queue for the book.
- The copy must be `AVAILABLE`.
- It cannot be the last operational copy.

### 6.8 `POST /api/books`

Purpose:

Creates a book.

Authentication:

Requires `ADMIN` or `LIBRARIAN`.

Request:

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
  "description": "Book description."
}
```

Response:

`201 Created` with `BookResponse`.

Rules:

- ISBN must be unique.
- Active books receive an initial copy when needed.

### 6.9 `PUT /api/books/{id}`

Purpose:

Updates book metadata.

Authentication:

Requires `ADMIN` or `LIBRARIAN`.

Request:

Uses the same body as book creation.

### 6.10 `PATCH /api/books/{id}/status`

Purpose:

Changes book status.

Authentication:

Requires `ADMIN` or `LIBRARIAN`.

Query:

- `status`: `ACTIVE` or `INACTIVE`.

Rules:

- Invalid statuses are rejected.
- Activating a book can create an initial copy when none exists.

### 6.11 `DELETE /api/books/{id}`

Purpose:

Deletes a book and safe dependent records.

Authentication:

Requires `ADMIN` or `LIBRARIAN`.

Response:

`204 No Content`.

Rules:

- Reject if the book has active or overdue loans.
- Reject if the book has waiting reservations.
- Deletes reservations, renewals, loans, reviews, favorites, copies, and then the book.

### 6.12 `POST /api/books/{id}/pdf/upload`

Purpose:

Uploads a PDF file for a book.

Authentication:

Requires `ADMIN` or `LIBRARIAN`.

Content type:

`multipart/form-data`

Field:

- `file`

Response:

```json
{
  "bookId": 1,
  "message": "PDF subido y guardado exitosamente.",
  "filename": "Book_ab12cd34.pdf",
  "pdfUrl": "/api/books/1/pdf"
}
```

Rules:

- File is required.
- File must look like a PDF.
- File must not exceed configured maximum size.

### 6.13 `POST /api/books/{id}/pdf/download`

Purpose:

Downloads a remote PDF from an allowed HTTPS host and attaches it to the book.

Request:

```json
{
  "url": "https://allowed-host.example/book.pdf"
}
```

Rules:

- HTTPS only.
- Default HTTPS port only.
- Host must be configured as allowed.
- Host cannot resolve to private or local addresses.
- Maximum three redirects.
- Resource must be a PDF.

### 6.14 `GET /api/books/{id}/pdf`

Purpose:

Serves the PDF for in-app reading.

Authentication:

Required.

Response:

PDF binary with `Content-Type: application/pdf`.

Rules:

- Staff can read.
- Regular users need an active loan for the book.
- The PDF must exist and be readable.

## 7. Loan Endpoints

### 7.1 `GET /api/loans`

Purpose:

Lists all loans for staff.

Authentication:

Requires `ADMIN` or `LIBRARIAN`.

### 7.2 `GET /api/loans/my-loans`

Purpose:

Lists loans for the authenticated user.

Authentication:

Required.

### 7.3 `POST /api/loans`

Purpose:

Creates a loan for an available copy.

Request:

```json
{
  "bookId": 1,
  "durationMinutes": 1440
}
```

Response:

`201 Created` with `LoanResponse`.

Rules:

- Duration from 5 to 10080 minutes.
- Book must be active.
- There must be an available copy.
- User cannot already have an open loan for the same book.
- User cannot be in the 24-hour cooldown period for the same book.
- User cannot exceed three open loans.
- The selected copy becomes `LOANED`.
- Notifications are created.

### 7.4 `PUT /api/loans/{id}/return`

Purpose:

Returns a loan.

Rules:

- Owner or staff only.
- Cannot return twice.
- Copy becomes available.
- Next waiting reservation may be fulfilled.
- Notifications are created.

### 7.5 `POST /api/loans/{id}/renew`

Purpose:

Renews a loan.

Request:

```json
{
  "durationMinutes": 1440
}
```

Rules:

- Owner or staff only.
- Duration from 5 to 10080 minutes.
- Loan cannot be returned.
- Loan cannot be overdue.
- Maximum two renewals.
- Cannot renew if there is a waiting reservation queue for the book.
- A `LoanRenewal` record is inserted.

### 7.6 `GET /api/loans/{id}/history`

Purpose:

Returns renewal history.

Response:

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

## 8. Reservation Endpoints

### 8.1 `GET /api/reservations/my`

Purpose:

Lists reservations for the authenticated user.

Response:

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

### 8.2 `POST /api/reservations`

Purpose:

Adds the user to a waiting queue for a book with no available copies.

Request:

```json
{
  "bookId": 1,
  "requestedLoanDurationMinutes": 1440
}
```

Rules:

- Duration from 5 to 10080 minutes.
- Book must be active.
- No copies can be available.
- User cannot have an open loan for the book.
- User cannot be in cooldown.
- User cannot already have a waiting reservation for the book.

### 8.3 `DELETE /api/reservations/{id}`

Purpose:

Cancels a waiting reservation.

Rules:

- Only waiting reservations can be cancelled.
- Owner or staff only.
- `cancelledAt` is assigned.
- Notifications are created.

## 9. Review Endpoints

### 9.1 `GET /api/reviews/book/{bookId}`

Purpose:

Lists visible reviews for a book.

Authentication:

Public.

### 9.2 `GET /api/reviews`

Purpose:

Lists reviews for staff moderation.

Authentication:

Requires `ADMIN` or `LIBRARIAN`.

Query:

- `status`: `VISIBLE`, `HIDDEN`, or `ALL`.

### 9.3 `POST /api/reviews`

Purpose:

Creates a review.

Request:

```json
{
  "bookId": 1,
  "rating": 5,
  "comment": "Excellent book."
}
```

Rules:

- Rating from 1 to 5.
- Comment required, maximum 2000 characters.
- One review per user and book.
- User must have had a loan for the book.

### 9.4 `PATCH /api/reviews/{id}/hide`

Purpose:

Changes review status to `HIDDEN`.

Authentication:

Requires `ADMIN` or `LIBRARIAN`.

### 9.5 `PATCH /api/reviews/{id}/show`

Purpose:

Changes review status to `VISIBLE`.

Authentication:

Requires `ADMIN` or `LIBRARIAN`.

## 10. Favorite Endpoints

### 10.1 `GET /api/favorites`

Purpose:

Lists the authenticated user's favorite books.

### 10.2 `POST /api/favorites/{bookId}`

Purpose:

Toggles a favorite.

Response when created:

```json
{
  "bookId": 1,
  "favorite": true
}
```

Response when removed:

```json
{
  "bookId": 1,
  "favorite": false
}
```

## 11. Notification Endpoints

### 11.1 `GET /api/notifications`

Purpose:

Lists notifications for the authenticated user.

Query:

- `status`: `all` or `unread`.
- `page`: defaults to `0`.
- `size`: defaults to `20`, maximum `50`.

Response:

```json
{
  "content": [
    {
      "id": 1,
      "type": "LOAN_CREATED",
      "title": "Prestamo creado",
      "message": "Your loan was created.",
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

### 11.2 `GET /api/notifications/unread-count`

Purpose:

Returns unread notification count.

Response:

```json
{
  "unreadCount": 3
}
```

### 11.3 `PATCH /api/notifications/{id}/read`

Purpose:

Marks one notification as read.

Rules:

- Only the owner can mark the notification.
- Already-read notifications keep their original read state.

### 11.4 `PATCH /api/notifications/read-all`

Purpose:

Marks all unread notifications for the user as read.

Response:

```json
{
  "unreadCount": 0
}
```

## 12. Chat Endpoint

### 12.1 `POST /api/chat`

Purpose:

Streams an AI answer for a book-related question.

Authentication:

Required.

Produces:

```http
text/event-stream
```

Request:

```json
{
  "bookId": 1,
  "selectedText": "Selected text from the book.",
  "context": "Book context.",
  "question": "What does this fragment mean?",
  "providerApiKey": "sk-personal",
  "history": [
    {
      "role": "user",
      "content": "Previous question"
    },
    {
      "role": "assistant",
      "content": "Previous answer"
    }
  ]
}
```

Stream response:

```text
data: "First chunk"

data: "Second chunk"

data: [DONE]
```

Stream error:

```text
data: [ERROR] Provider error message.
```

Rules:

- If `bookId` is provided, regular users need an active loan for the book.
- Staff can use the chat without a loan.
- The backend tries the system provider key first.
- If the system key is missing or fails, a personal key can be used.

