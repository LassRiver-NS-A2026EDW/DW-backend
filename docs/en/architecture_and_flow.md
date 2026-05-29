# BookWorm Architecture And End-To-End Flow

## 1. Purpose

This document explains how BookWorm works from end to end. It is written for developers who are joining the project with no previous context about the codebase or the business domain.

BookWorm is a digital library application. Users can register, sign in, browse a book catalog, read PDF books inside the application, borrow books, join reservation queues, write reviews, receive transactional notifications, and use an AI assistant while reading. Administrators and librarians can manage books, copies, PDFs, loans, reviews, and operational metrics.

The project is split into two independent applications:

- `DW-frontend-ViteReact`: React, Vite, and TypeScript frontend.
- `DW-backend-SpringBoot`: Spring Boot REST API.

The database is PostgreSQL and the schema is controlled by Flyway migrations. This matters because the domain is relational: users own loans, books own copies, loans consume a concrete copy, reservations form a queue, reviews belong to a user and a book, and notifications belong to a user.

## 2. Textual Architecture Diagram

```text
User
  |
  | Clicks, types, filters, borrows, reserves, reads
  v
React + Vite frontend
  |
  | App.tsx
  | - QueryClientProvider
  | - AppProvider
  | - Sidebar / Topbar
  | - View selected by currentView
  |
  | State
  | - AppContext: session, navigation, selected book, actions
  | - TanStack Query: server state cache
  | - Local component state: forms, modals, temporary UI state
  v
API client layer
  |
  | src/api/http.ts
  | - Builds /api URLs
  | - Adds Authorization: Bearer <token>
  | - Serializes JSON
  | - Parses JSON/text
  | - Normalizes errors
  | - Clears session on 401
  v
Spring Boot backend
  |
  | SecurityConfig + JwtAuthenticationFilter
  | - CORS
  | - Stateless JWT authentication
  | - Route authorization by role
  |
  | Controllers
  | - Receive HTTP input
  | - Validate request DTOs
  | - Extract authenticated user
  | - Delegate to services
  |
  | Services
  | - Business rules
  | - Transaction boundaries
  | - Concurrency-safe operations
  | - Cross-entity orchestration
  |
  | Repositories
  | - Spring Data JPA
  | - Queries
  | - Specifications
  | - Pessimistic locks
  v
PostgreSQL
  |
  | Tables
  | - users
  | - books
  | - book_copies
  | - loans
  | - loan_renewals
  | - reservations
  | - reviews
  | - user_favorites
  | - notifications
```

## 3. Frontend Layers

### 3.1 Entry Point

`src/main.tsx` mounts `<App />` into the DOM element with id `root`. It intentionally contains no business logic. The entry point should remain stable and predictable.

### 3.2 Application Composition

`src/App.tsx` wraps the application with:

- `QueryClientProvider`: enables TanStack Query.
- `AppProvider`: exposes BookWorm application state and actions.
- `Toaster`: renders global toast messages.

`AppContent` reads `currentView` from `AppContext` and decides which page to render. The project currently uses context-driven navigation instead of URL-based routing. Although `react-router` exists as a dependency, the active navigation model is `setCurrentView(...)`.

### 3.3 Application Context

`AppContext` coordinates global UI state:

- Authenticated user.
- Current view.
- Selected book.
- Sidebar state.
- Theme.
- Catalog filters.
- High-level actions such as creating loans, creating reservations, returning loans, adding reviews, deleting books, and refreshing server state.

This exists because one business operation often affects several screens. Returning a loan can change:

- The loan list.
- Book availability.
- Reservation queue state.
- Notification count.
- The currently selected book.

Centralizing these workflows avoids duplicated invalidation and refresh logic.

### 3.4 Server State With TanStack Query

TanStack Query stores data that comes from the backend:

- Books.
- Book facets.
- Favorites.
- Loans.
- Reservations.
- Notifications.
- Admin reviews.

The configured query client uses:

- `staleTime: 30_000`
- `retry: 1`
- `refetchOnWindowFocus: false`

This keeps the UI responsive without constantly refetching data. Mutations and AppContext actions explicitly invalidate or refresh affected queries.

## 4. Backend Layers

### 4.1 Controllers

Controllers are the HTTP boundary. They define methods and routes, validate DTOs with `@Valid`, read path/query/body parameters, extract `UserDetails` when a route is authenticated, and delegate to services.

The main controllers are:

- `AuthController`
- `UserController`
- `BookController`
- `LoanController`
- `ReservationController`
- `ReviewController`
- `FavoriteController`
- `NotificationController`
- `ChatController`

### 4.2 DTOs

DTOs define the public API contract. They prevent the frontend from depending directly on JPA entities.

For example, the `Book` entity contains `pdfPath`, which is an internal server filesystem path. The frontend never receives that field. Instead, it receives:

- `hasPdf`
- `pdfUrl`

This keeps storage details private and makes future storage changes easier.

### 4.3 Services

Services contain the business rules. They decide whether an operation is allowed and orchestrate changes across repositories.

Important rules include:

- A user cannot have more than three open loans.
- Loan and reservation duration must be between 5 minutes and 10080 minutes.
- A book can only be borrowed if it is active and has an available copy.
- A reservation can only be created when no copies are available.
- A user cannot reserve or borrow the same book immediately after returning it; there is a 24-hour cooldown.
- A book cannot be deleted while it has active or overdue loans.
- A copy cannot be retired if it is not available, if it is the last operational copy, or if there is a waiting reservation queue.
- A review requires a previous loan for that book.
- Reading a PDF and using the book chat require an active loan unless the user is staff.

### 4.4 Repositories

Repositories are Spring Data JPA interfaces. Some operations use derived queries, some use JPQL, and some use pessimistic locks.

Critical locking methods include:

- `BookRepository.findLockedById`
- `LoanRepository.findLockedById`
- `BookCopyRepository.findByIdAndBookId`
- `ReservationRepository.findAllByBookIdAndStatusOrderByCreatedAtAsc`

Locks are important because copy assignment and queue fulfillment are concurrent workflows. Two users should never receive the same available copy.

### 4.5 Entities

The main entities are:

- `User`: identity, credentials, role, profile fields.
- `Book`: bibliographic metadata and PDF metadata.
- `BookCopy`: a concrete borrowable copy of a book.
- `Loan`: active, overdue, or returned borrowing transaction.
- `LoanRenewal`: audit trail for loan renewals.
- `Reservation`: waiting queue entry that may become a loan.
- `Review`: user review that can be visible or hidden.
- `UserFavorite`: user-book favorite relationship.
- `Notification`: persistent user notification.

## 5. Security Flow

### 5.1 Registration

The frontend sends:

```text
POST /api/auth/register
```

The backend validates the request, checks that the email is unique, validates the minimum age of 13 years, hashes the password with Argon2, assigns role `USER`, and persists the user.

### 5.2 Login

The frontend sends:

```text
POST /api/auth/login
```

The backend authenticates with Spring Security. If credentials are valid, it returns a JWT and a minimal profile payload. The frontend stores the token in localStorage and also keeps it in memory.

### 5.3 Authenticated Requests

`src/api/http.ts` adds:

```http
Authorization: Bearer <token>
```

`JwtAuthenticationFilter` extracts and validates the token, loads the user, and populates Spring Security's context. Controllers can then read the authenticated user with `@AuthenticationPrincipal`.

### 5.4 Role-Based Access

Routes for library management require `ADMIN` or `LIBRARIAN`. Regular users can browse books, manage their own loans, manage their own reservations, write reviews, use favorites, read borrowed PDFs, and use the book chat when they have access.

## 6. Catalog Flow

The user opens the catalog, searches, changes filters, or paginates.

Frontend flow:

```text
CatalogPage
  -> AppContext filter state
  -> bookQueryParams
  -> useBooksQuery
  -> booksApi.list
  -> GET /api/books
```

Backend flow:

```text
BookController.getBooks
  -> BookServiceImpl.getBooks
  -> JPA Specification
  -> BookRepository.findAll(spec, pageable)
  -> BookResponse with aggregates
```

Availability is not a simple field. A book is available only when:

- The book status is `ACTIVE`.
- At least one `BookCopy` has status `AVAILABLE`.

This prevents an active book with no free copies from being shown as borrowable.

## 7. Facets Flow

Catalog filters for categories and languages come from:

```text
GET /api/books/facets
```

The backend calculates distinct categories and languages from all books. This is intentional. If the frontend calculated facets from the currently filtered page, selecting one category would shrink the category list and make the filter UI misleading.

## 8. Book Detail Flow

When a user opens a book:

```text
BookCard.onOpen
  -> setSelectedBook(book)
  -> setCurrentView("book-detail")
  -> refetch selected book
  -> refresh book reviews
```

The detail page uses:

- `GET /api/books/{id}`
- `GET /api/reviews/book/{bookId}`

`BookResponse` contains metadata, rating, review count, PDF availability, copy counts, waiting queue size, active-loan information, and cooldown information.

## 9. Loan Flow

Creating a loan is one of the most important transactional workflows.

Frontend flow:

```text
BookActionPanel
  -> AppContext.addLoan
  -> loansApi.create
  -> POST /api/loans
  -> refresh loans, reservations, notifications, books, selected book
```

Backend flow:

```text
LoanServiceImpl.createLoan
  -> load authenticated user
  -> lock book
  -> validate duration
  -> validate active book
  -> validate no duplicate open loan
  -> validate cooldown
  -> validate max active loans
  -> find available copy
  -> mark copy as LOANED
  -> create Loan
  -> create notifications
```

The transaction must commit the copy update and the loan insert together. If one succeeds and the other fails, inventory consistency breaks.

## 10. Return And Queue Fulfillment Flow

Returning a loan:

```text
PUT /api/loans/{id}/return
```

The backend locks the loan, validates ownership or staff role, rejects double returns, marks the loan as `RETURNED`, sets `returnedAt`, marks the copy as `AVAILABLE`, and sends notifications.

If a waiting reservation exists, the same transaction can immediately convert the first waiting reservation into a new loan:

```text
Returned copy
  -> first WAITING reservation
  -> create new Loan for reservation owner
  -> mark reservation as FULFILLED
  -> link fulfilledLoan
  -> notify user and staff
```

This is a transactional workflow because a single user action changes loans, copies, reservations, and notifications.

## 11. Reservation Flow

A reservation represents a queue position, not a loan.

Frontend:

```text
BookActionPanel
  -> AppContext.createReservation
  -> reservationsApi.create
  -> POST /api/reservations
  -> refresh reservations, notifications, books, selected book
```

Backend rules:

- Book must be active.
- No copies can be available.
- User cannot already have an open loan for the same book.
- User cannot be in the 24-hour cooldown for the same book.
- User cannot already have a waiting reservation for the same book.
- Requested future loan duration must be between 5 minutes and 7 days.

Queue position is calculated by counting previous waiting reservations for the same book.

## 12. Renewal Flow

Renewal endpoint:

```text
POST /api/loans/{id}/renew
```

Rules:

- The loan cannot be returned.
- The loan cannot be overdue.
- Maximum two renewals.
- A loan cannot be renewed when users are waiting in the reservation queue for the same book.
- Renewal duration must be between 5 minutes and 7 days.

Each renewal creates a `LoanRenewal` record. This keeps an audit trail instead of silently changing `dueDate`.

## 13. Review Flow

Creating a review:

```text
POST /api/reviews
```

Rules:

- Rating must be from 1 to 5.
- Comment is required.
- Comment maximum length is 2000 characters.
- Only one review per user and book.
- The user must have requested the book in a loan at least once.

Public book reviews use:

```text
GET /api/reviews/book/{bookId}
```

Only `VISIBLE` reviews are returned.

Admin moderation uses:

```text
GET /api/reviews?status=ALL
PATCH /api/reviews/{id}/hide
PATCH /api/reviews/{id}/show
```

Hiding a review changes its status to `HIDDEN`; it does not delete the row.

## 14. Favorites Flow

Favorites are stored as `UserFavorite`.

Endpoints:

```text
GET /api/favorites
POST /api/favorites/{bookId}
```

The POST endpoint is a toggle:

- If the favorite does not exist, it is created.
- If it already exists, it is deleted.

The database unique constraint on `(user_id, book_id)` prevents duplicates.

## 15. Notification Flow

Notifications are persistent records. They are not the same as frontend toast messages.

Notification types:

- `LOAN_CREATED`
- `LOAN_RENEWED`
- `LOAN_RETURNED`
- `LOAN_DUE_SOON`
- `LOAN_OVERDUE`
- `RESERVATION_CREATED`
- `RESERVATION_CANCELLED`
- `RESERVATION_FULFILLED`

The backend creates notifications for the affected user and for staff users. `dedupeKey` prevents duplicate notification records.

The frontend `NotificationBell` reads:

- `GET /api/notifications`
- `GET /api/notifications/unread-count`

It can also mark one or all notifications as read.

## 16. Administration Flow

The admin page is protected in both frontend and backend.

It supports:

- Dashboard charts.
- Book creation and editing.
- Book status changes.
- PDF upload and remote PDF download.
- Copy creation.
- Copy retirement.
- Book deletion with business-rule validation.
- Loan return from staff view.
- Review moderation.

Deleting a book is protected by backend rules. The backend rejects deletion if there are active or overdue loans, or waiting reservations. If deletion is allowed, dependent records are removed in a safe order.

## 17. PDF Reader Flow

The frontend loads PDFs from:

```text
GET /api/books/{id}/pdf
```

The backend allows access if:

- The user is `ADMIN` or `LIBRARIAN`, or
- The user has an active loan for the book.

The reader page manages:

- PDF loading state.
- Current page.
- Total pages.
- Zoom.
- Selected text.
- Chat panel visibility.

## 18. AI Chat Flow

The chat endpoint is:

```text
POST /api/chat
Produces: text/event-stream
```

The frontend sends the book id, selected text, context, question, optional personal provider key, and local chat history.

The backend validates access to the book, builds provider messages, and streams chunks through `SseEmitter`. The AI client first tries the system API key. If it is unavailable or fails and a personal key was provided, it can use the personal key.

The personal key is stored only in the browser localStorage and is sent only when asking a question.

## 19. Error Flow

The backend centralizes errors in `GlobalExceptionHandler`.

Common responses:

- `400 VALIDATION_ERROR`
- `400 BUSINESS_RULE_VIOLATION`
- `400 BAD_REQUEST`
- `401 INVALID_CREDENTIALS`
- `403 FORBIDDEN`
- `404 NOT_FOUND`
- `409 EMAIL_ALREADY_EXISTS`
- `500 INTERNAL_SERVER_ERROR`

The frontend wraps those errors in `ApiError`. On `401`, it clears the token, removes stored user data, dispatches `bookworm:unauthorized`, and redirects the user to login.

## 20. Why The System Is Transactional

BookWorm is not a simple CRUD app. Many workflows update several tables and require strict consistency:

- Loan creation updates `book_copies`, inserts `loans`, and creates `notifications`.
- Loan return updates `loans`, updates `book_copies`, may fulfill a reservation, may create a new loan, updates `reservations`, and creates notifications.
- Renewal updates `loans` and inserts `loan_renewals`.
- Reservation creation inserts `reservations` and notifications.
- Book deletion removes dependent data in a safe order and rejects unsafe deletion.
- Notification deduplication prevents repeated operational events.

These flows justify transaction boundaries, validation rules, pessimistic locks, and relational constraints.

