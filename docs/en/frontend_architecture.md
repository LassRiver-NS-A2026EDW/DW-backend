# Frontend Architecture

## 1. Purpose

This document explains the BookWorm frontend architecture for developers who are new to the project. It covers application startup, API consumption, global state, server-state caching, pages, shared components, UI primitives, and the reasoning behind important technical decisions.

The frontend is not only a group of visual pages. It is the client-side orchestration layer for:

- Authentication.
- Catalog browsing.
- Favorites.
- Loans.
- Reservations.
- Reviews.
- Notifications.
- Administration.
- PDF reading.
- AI chat.

## 2. Technology Stack

Main frontend technologies:

- React 18.
- Vite.
- TypeScript.
- TanStack Query.
- Radix UI primitives.
- Tailwind CSS.
- Sonner.
- Lucide React icons.
- Chart.js and `react-chartjs-2`.
- React Markdown and `remark-gfm`.

## 3. Application Startup

### 3.1 `src/main.tsx`

`main.tsx` mounts the React application:

```text
createRoot(...)
  -> render(<App />)
```

It imports `index.css`, which makes the global styles available.

### 3.2 `src/App.tsx`

`App.tsx` wraps the app with:

- `QueryClientProvider`
- `AppProvider`
- `Toaster`

`AppContent` reads `currentView` from `AppContext` and renders the correct page.

The current view can be:

- `home`
- `catalog`
- `book-detail`
- `book-reader`
- `favorites`
- `loans`
- `reservations`
- `reviews`
- `profile`
- `admin`
- `login`
- `register`

The app currently uses context-based navigation. This means pages switch by calling `setCurrentView(...)`.

## 4. State Model

### 4.1 AppContext State

File:

```text
src/context/AppContext.tsx
```

`AppContext` stores state that many components need:

- `currentUser`
- `currentView`
- `selectedBook`
- `sidebarCollapsed`
- `theme`
- Catalog filters
- Current book page
- Auth loading and auth errors

It also exposes actions:

- `login`
- `register`
- `logout`
- `updateProfile`
- `changePassword`
- `toggleFavorite`
- `addReview`
- `addBook`
- `updateBook`
- `deleteBook`
- `uploadBookPdf`
- `downloadBookPdf`
- `addLoan`
- `updateLoan`
- `renewLoan`
- `createReservation`
- `cancelReservation`
- `createBookCopy`
- `deleteBookCopy`
- `hideReview`
- `keepReviewVisible`

The context exists because one user action often affects many server resources. For example, returning a loan may change loans, reservations, notifications, and book availability.

### 4.2 TanStack Query State

Server state is handled by TanStack Query. This includes:

- Books.
- Book facets.
- Favorites.
- Loans.
- Reservations.
- Notifications.
- Admin reviews.

This avoids manually synchronizing remote data in many components.

### 4.3 Local Component State

Local state is used for temporary UI-only data:

- Form inputs.
- Dialog open/closed state.
- Selected PDF file.
- Current PDF page.
- PDF zoom.
- Chat input.
- Date filter fields.

This keeps global state smaller and easier to reason about.

## 5. HTTP Layer

### 5.1 `src/api/http.ts`

Responsibilities:

- Stores and restores the auth token.
- Builds API URLs.
- Adds `Authorization` headers.
- Sends JSON requests.
- Parses JSON or text responses.
- Builds `ApiError`.
- Clears session on `401`.

Important functions:

- `setAuthToken`
- `restoreTokenFromStorage`
- `getAuthToken`
- `buildApiUrl`
- `http<T>`
- `getApiErrorMessage`
- `isUnauthorizedError`
- `isForbiddenError`

The project uses the browser `fetch` API through a custom wrapper. Axios is not used. This gives the project direct control over JSON requests and streaming responses.

### 5.2 Unauthorized Flow

When the API returns `401`, `http.ts`:

1. Clears the token.
2. Removes the stored user.
3. Dispatches `bookworm:unauthorized`.

`AppContext` listens to that event and redirects the user to login while clearing sensitive cached queries.

## 6. API Modules

### 6.1 `src/api/auth.ts`

Exports:

- `authApi.login`
- `authApi.register`
- `authApi.logout`

Types:

- `Gender`
- `Role`
- `LoginRequest`
- `LoginResponse`
- `RegisterRequest`
- `RegisterResponse`

### 6.2 `src/api/books.ts`

Exports:

- `booksApi.list`
- `booksApi.get`
- `booksApi.facets`
- `booksApi.create`
- `booksApi.update`
- `booksApi.updateStatus`
- `booksApi.deleteBook`
- `booksApi.availability`
- `booksApi.copies`
- `booksApi.createCopy`
- `booksApi.deleteCopy`
- `booksApi.uploadPdf`
- `booksApi.downloadPdf`
- `booksApi.pdfUrl`

`uploadPdf` uses `FormData`, so it uses a custom authenticated `fetch` call instead of the JSON `http<T>` wrapper.

### 6.3 `src/api/chat.ts`

Exports:

- `chatStream`

The chat API uses streaming. It reads a `ReadableStream`, parses SSE-style events, and calls:

- `onChunk`
- `onDone`
- `onError`

### 6.4 `src/api/favorites.ts`

Exports:

- `favoritesApi.list`
- `favoritesApi.toggle`

### 6.5 `src/api/loans.ts`

Exports:

- `loansApi.listMine`
- `loansApi.listAll`
- `loansApi.create`
- `loansApi.returnLoan`
- `loansApi.renew`
- `loansApi.history`

### 6.6 `src/api/reservations.ts`

Exports:

- `reservationsApi.listMine`
- `reservationsApi.create`
- `reservationsApi.cancel`

### 6.7 `src/api/reviews.ts`

Exports:

- `reviewsApi.byBook`
- `reviewsApi.list`
- `reviewsApi.create`
- `reviewsApi.hide`
- `reviewsApi.show`

### 6.8 `src/api/notifications.ts`

Exports:

- `notificationsApi.list`
- `notificationsApi.unreadCount`
- `notificationsApi.markAsRead`
- `notificationsApi.markAllAsRead`

### 6.9 `src/api/users.ts`

Exports:

- `usersApi.me`
- `usersApi.updateMe`
- `usersApi.changePassword`

## 7. Mappers

File:

```text
src/api/mappers.ts
```

Functions:

- `mapBackendRole`
- `userFromBackend`
- `bookFromBackend`
- `bookToUpsert`
- `reviewFromBackend`
- `loanFromBackend`
- `reservationFromBackend`

Mappers exist because backend DTOs and frontend UI models are not identical. The backend uses numeric ids and uppercase enum-like statuses. The frontend uses string ids in its UI model and lowercase role/status labels. Centralizing conversions prevents every page from duplicating mapping logic.

## 8. Query Layer

### 8.1 `src/lib/queryClient.ts`

Configuration:

```text
staleTime: 30000
retry: 1
refetchOnWindowFocus: false
```

### 8.2 `src/hooks/queryKeys.ts`

Central query keys:

- `books(params)`
- `book(id)`
- `bookFacets`
- `favorites`
- `loans(scope)`
- `reservations`
- `notifications.all`
- `notifications.list(status, page, size)`
- `notifications.unreadCount`
- `reviewsByBook(bookId)`
- `adminReviews`

Centralized keys reduce cache invalidation mistakes.

### 8.3 `src/hooks/useLibraryQueries.ts`

Hooks:

- `useBooksQuery`
- `useBookFacetsQuery`
- `useFavoritesQuery`
- `useLoansQuery`
- `useReservationsQuery`
- `useNotificationsQuery`
- `useUnreadNotificationsCountQuery`
- `useMarkNotificationReadMutation`
- `useMarkAllNotificationsReadMutation`
- `useAdminReviewsQuery`

These hooks hide API details from pages and apply mappers close to the data-fetching boundary.

## 9. Responsive Hook

File:

```text
src/hooks/use-mobile.ts
```

Hook:

```text
useIsMobile()
```

It uses `window.matchMedia("(max-width: 767px)")` and returns a boolean.

Why it exists:

- Centralizes the mobile breakpoint.
- Avoids repeated resize logic.
- Cleans up listeners.
- Makes responsive decisions explicit.

At the time of this documentation, there are no detected usages of `useIsMobile`, but it is still part of the shared hook layer.

## 10. Visual Models

File:

```text
src/mocks/mockData.ts
```

Interfaces:

- `Book`
- `Review`
- `User`
- `Loan`
- `Reservation`

Although the file name includes `mockData`, the interfaces are used as frontend view models.

## 11. Validation Utilities

File:

```text
src/utils/validation.ts
```

Functions:

- `localDateIso`
- `maxBirthDateForMinimumAge`
- `firstError`
- `validateLogin`
- `validateRegister`
- `validateProfile`
- `validatePasswordChange`
- `validateReview`
- `validateLoan`
- `validateBook`

Frontend validation improves user experience. Backend validation still remains the source of truth for security and consistency.

Important mirrored rules:

- Minimum registration age: 13 years.
- Loan and reservation duration: 5 to 10080 minutes.
- Review rating: 1 to 5.
- Review comment maximum: 2000 characters.
- Book title, author, and ISBN are required.

## 12. Display Utilities

File:

```text
src/utils/display.ts
```

`formatLanguage` normalizes language labels for display. For example, it can present `espanol` as `Español`.

## 13. Layouts

### 13.1 `Sidebar`

Responsibilities:

- Displays navigation.
- Shows the current view.
- Shows user info.
- Supports collapsed state.
- Shows administration only for staff.

Uses `AppContext` for navigation and user state.

### 13.2 `Topbar`

Responsibilities:

- Displays current section title.
- Toggles theme.
- Shows notification bell.
- Handles logout confirmation.

## 14. Pages

### 14.1 `LoginPage`

Responsibilities:

- Captures email and password.
- Validates input.
- Toggles password visibility.
- Calls `login`.
- Shows toast errors.

### 14.2 `RegisterPage`

Responsibilities:

- Captures profile and credential fields.
- Validates password strength.
- Validates birth date and minimum age.
- Calls `register`.
- Automatically signs the user in after successful registration.

### 14.3 `HomePage`

Responsibilities:

- Shows library summary.
- Shows category shortcuts.
- Shows featured or recent books.
- Navigates to catalog or book detail.

### 14.4 `CatalogPage`

Responsibilities:

- Shows search input.
- Shows filters.
- Shows book grid.
- Shows pagination.
- Requires authentication for favorite actions.

Uses:

- `BookFilters`
- `BookCard`
- `CatalogPagination`
- `BookGridSkeleton`
- `AuthRequiredDialog`
- `EmptyState`

### 14.5 `FavoritesPage`

Responsibilities:

- Requires authentication.
- Shows favorite books.
- Reuses `BookCard`.
- Allows opening book detail or toggling favorite.

### 14.6 `BookDetailPage`

Responsibilities:

- Shows selected book details.
- Creates loan.
- Creates reservation.
- Opens reader.
- Creates review.
- Handles protected action prompts.

Uses:

- `BookOverview`
- `BookActionPanel`
- `ReviewSection`
- `ConfirmDialog`
- `AuthRequiredDialog`

### 14.7 `BookReaderPage`

Responsibilities:

- Loads the PDF.
- Renders pages.
- Handles zoom and pagination.
- Captures selected text.
- Shows the AI chat.
- Allows PDF URL loading for eligible users.

Uses:

- `booksApi.pdfUrl`
- `getAuthToken`
- `ChatPanel`
- `Dialog`
- `EmptyState`

### 14.8 `LoansPage`

Responsibilities:

- Requires authentication.
- Lists loans.
- Filters loans by date.
- Returns loans.
- Renews loans.
- Shows renewal history.

Uses:

- `LoanCard`
- `LoanDurationSelect`
- `LoanDateFilters`
- `Dialog`

### 14.9 `ReservationsPage`

Responsibilities:

- Requires authentication.
- Lists user reservations.
- Cancels waiting reservations.

Uses:

- `ReservationCard`
- `EmptyState`

### 14.10 `ReviewsPage`

Responsibilities:

- Requires authentication.
- Shows the current user's reviews.
- Opens related book detail.

Uses:

- `RatingStars`
- `ReviewComment`
- `EmptyState`

### 14.11 `ProfilePage`

Responsibilities:

- Shows user profile.
- Updates name.
- Changes password.
- Shows role badge.
- Shows user activity.

Uses:

- `RoleBadge`
- `PasswordStrength`
- `RatingStars`
- `EmptyState`

### 14.12 `AdminPage`

Responsibilities:

- Blocks non-staff users.
- Shows dashboard metrics.
- Shows charts.
- Manages books.
- Manages copies.
- Manages PDFs.
- Marks loans as returned.
- Moderates reviews.
- Deletes books through confirmation.

Uses:

- Chart.js and `react-chartjs-2`.
- `Table`
- `ConfirmDialog`
- `ReviewComment`
- `StatusBadge`
- `booksApi.copies`

## 15. Catalog And Book Detail Components

### 15.1 `BookCard`

Props:

- `book`
- `isFavorite`
- `onOpen`
- `onToggleFavorite`

Responsibilities:

- Shows cover.
- Shows title and author.
- Shows rating and review count.
- Shows category and language.
- Shows availability.
- Handles favorite action.
- Opens detail.

It is reused by catalog and favorites to keep visual behavior consistent.

### 15.2 `CatalogPagination`

Controls catalog page navigation.

### 15.3 `BookFilters`

Displays category, language, and availability filters.

### 15.4 `BookOverview`

Shows detailed book metadata, rating, status, and description.

### 15.5 `BookActionPanel`

Decides which transactional action is available:

- Borrow.
- Reserve.
- Read.
- Show cooldown.
- Show inventory.

### 15.6 `ReviewSection`

Shows review form and visible reviews for a book.

## 16. Loans, Reservations, And Reviews Components

### 16.1 `LoanCard`

Shows a loan, its status, dates, return action, and renewal action.

### 16.2 `LoanDurationSelect`

Centralizes allowed duration options and exports `formatLoanDuration`.

### 16.3 `LoanDateFilters`

Filters loans by loan date, due date, or return date.

### 16.4 `ReservationCard`

Shows reservation status, queue position, requested duration, and cancel action.

### 16.5 `ReviewComment`

Renders long comments safely without horizontal overflow. This matters because users can type very long strings with no spaces.

## 17. Notification Component

### 17.1 `NotificationBell`

Responsibilities:

- Shows unread count.
- Opens a notification panel.
- Lists notifications.
- Marks one notification as read.
- Marks all notifications as read.
- Navigates to `loans` or `reservations` based on `targetView`.
- Closes when clicking outside.

It uses notification query hooks and mutation hooks.

## 18. AI Chat Component

### 18.1 `ChatPanel`

Props:

- `bookId`
- `bookDescription`
- `selectedText`
- `onClearSelectedText`
- `onClose`

State:

- Messages.
- Current question.
- Loading state.
- Error state.
- Settings open state.
- Personal API key.

Responsibilities:

- Sends questions to the streaming chat API.
- Appends streamed assistant chunks.
- Supports aborting the current request.
- Stores a personal provider key in localStorage.
- Masks the key when displaying it.
- Renders Markdown responses.

## 19. Shared Components

### 19.1 `AuthRequiredDialog`

Prompts unauthenticated users to sign in or register before protected actions.

### 19.2 `ConfirmDialog`

Reusable confirmation dialog for destructive or important actions.

### 19.3 `EmptyState`

Consistent empty-state component with optional action.

### 19.4 `ImageWithFallback`

Handles image loading failures and displays a fallback.

### 19.5 `LoadingSkeleton`

Provides loading skeletons for cards, grids, tables, and book detail.

### 19.6 `PasswordStrength`

Calculates and displays password strength rules.

### 19.7 `RatingStars`

Displays read-only or interactive star ratings.

### 19.8 `RoleBadge`

Displays the user's role with icon and label.

### 19.9 `StatusBadge`

Normalizes status labels and colors across the UI.

## 20. UI Primitives

Folder:

```text
src/components/ui
```

Files:

- `button.tsx`
- `badge.tsx`
- `card.tsx`
- `dialog.tsx`
- `alert-dialog.tsx`
- `input.tsx`
- `textarea.tsx`
- `select.tsx`
- `table.tsx`
- `skeleton.tsx`
- `sonner.tsx`
- `utils.ts`

These wrappers provide consistent styles, reusable variants, and accessible behavior through Radix primitives.

`utils.ts` exports `cn`, which combines `clsx` and `tailwind-merge`. This prevents conflicting Tailwind classes from being applied accidentally.

## 21. Important UI Workflows

### 21.1 Login

```text
LoginPage
  -> validateLogin
  -> AppContext.login
  -> authApi.login
  -> setAuthToken
  -> persistSession
  -> invalidateBooks
```

### 21.2 Register

```text
RegisterPage
  -> validateRegister
  -> authApi.register
  -> AppContext.login
```

### 21.3 Catalog Filtering

```text
BookFilters
  -> update AppContext filters
  -> reset page to 0
  -> useBooksQuery refetches
```

### 21.4 Create Loan

```text
BookDetailPage
  -> BookActionPanel
  -> addLoan
  -> loansApi.create
  -> refresh affected queries
```

### 21.5 Create Reservation

```text
BookDetailPage
  -> BookActionPanel
  -> createReservation
  -> reservationsApi.create
  -> refresh affected queries
```

### 21.6 Return Loan

```text
LoansPage or AdminPage
  -> updateLoan(status: returned)
  -> loansApi.returnLoan
  -> refresh loans, reservations, notifications, books
```

### 21.7 Moderate Review

```text
AdminPage
  -> hideReview or keepReviewVisible
  -> reviewsApi.hide or reviewsApi.show
  -> refresh admin reviews and book data
```

### 21.8 Delete Book

```text
AdminPage
  -> ConfirmDialog
  -> booksApi.deleteBook
  -> clear related local state
  -> refresh affected queries
```

## 22. Technical Decisions

### 22.1 Why Mappers Exist

Mappers isolate backend DTO shape from frontend UI shape. This makes future API changes easier to absorb.

### 22.2 Why `BookCard` Is Shared

The same book representation appears in catalog and favorites. Sharing the component prevents visual drift.

### 22.3 Why Facets Come From The Backend

Categories and languages must represent all available data, not only the currently filtered page.

### 22.4 Why Actions Live In `AppContext`

Actions like borrowing or returning affect multiple resources. Centralizing the workflow ensures all related queries are refreshed consistently.

### 22.5 Why Chat Uses A Separate Stream Client

The chat response is not a single JSON payload. It is streamed chunk by chunk, so it needs a specialized client.

### 22.6 Why UI Primitives Are Wrapped

Local wrappers around Radix and Tailwind keep styling consistent and reduce duplicated class strings.

### 22.7 Why Validation Exists In Both Frontend And Backend

Frontend validation improves user experience. Backend validation enforces security and business correctness.

## 23. Developer Warnings

- Do not derive book availability only from `book.status`; use copy counts.
- Do not compute catalog facets from the filtered page.
- Do not store permanent provider secrets without a dedicated secure design.
- Do not update server-derived arrays manually when a query invalidation is required.
- Do not bypass backend business rules with frontend-only checks.

