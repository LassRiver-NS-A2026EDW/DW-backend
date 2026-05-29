# Arquitectura Frontend De BookWorm

## 1. Propósito Del Documento

Este documento explica cómo está construido el frontend de BookWorm. Está escrito para una persona junior que entra al proyecto sin conocer el flujo de negocio.

El frontend no es una colección aislada de pantallas. Es una capa de presentación que:

1. Mantiene sesión local.
2. Consume una API Spring Boot.
3. Cachea datos remotos con TanStack Query.
4. Usa `AppContext` para coordinar navegación y acciones.
5. Renderiza vistas de biblioteca, préstamos, reservas, reseñas, administración, lector PDF y chat IA.

## 2. Tecnologías Principales

### 2.1 React 18

React se usa para construir interfaces declarativas basadas en componentes. Cada pantalla se representa como una función que devuelve JSX.

### 2.2 Vite

Vite provee servidor de desarrollo y build rápido. El punto de entrada es `src/main.tsx`.

### 2.3 TypeScript

TypeScript define contratos de datos en el frontend. Esto reduce errores al consumir respuestas del backend.

### 2.4 TanStack Query

TanStack Query maneja estado remoto:

- Cache.
- Carga.
- Error.
- Refetch.
- Invalidaciones.
- Mutations para notificaciones.

### 2.5 Radix UI

Radix se usa para componentes accesibles base:

- Dialog.
- Alert Dialog.
- Select.
- Slot.

El proyecto envuelve esos primitivos en `src/components/ui`.

### 2.6 Sonner

`sonner` muestra notificaciones visuales temporales de frontend, como “Libro creado correctamente”. No debe confundirse con las notificaciones persistidas del backend.

### 2.7 Chart.js

`chart.js` y `react-chartjs-2` se usan en el dashboard de administración.

### 2.8 React Markdown

`react-markdown` y `remark-gfm` renderizan respuestas del chat IA con formato Markdown.

## 3. Arranque De La Aplicación

### 3.1 `src/main.tsx`

Responsabilidad:

- Importar React DOM.
- Importar `App`.
- Importar CSS global.
- Montar la aplicación en `#root`.

Flujo:

```text
createRoot(document.getElementById("root")!).render(<App />)
```

No contiene lógica de negocio porque el archivo de entrada debe ser estable y simple.

### 3.2 `src/App.tsx`

`App` envuelve toda la UI con:

```tsx
<QueryClientProvider client={queryClient}>
  <AppProvider>
    <AppContent />
    <Toaster />
  </AppProvider>
</QueryClientProvider>
```

Decisión técnica:

- `QueryClientProvider` debe estar arriba de `AppProvider` porque `AppContext` usa `useQueryClient` y hooks basados en TanStack Query.
- `Toaster` se ubica dentro del provider para estar disponible en toda la app.

### 3.3 `AppContent`

`AppContent` lee `currentView` desde `useApp`.

Si `currentView` es:

- `login`: renderiza `Login`.
- `register`: renderiza `Register`.
- Cualquier otra vista: renderiza layout con `Sidebar`, `Topbar` y la página correspondiente.

Vistas soportadas:

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

## 4. Estado Global Y Estado Remoto

### 4.1 `AppContext`

Archivo:

```text
src/context/AppContext.tsx
```

`AppContext` expone:

- `currentUser`
- `books`
- `bookCategories`
- `bookLanguages`
- `reviews`
- `loans`
- `reservations`
- `favorites`
- Estado de filtros del catálogo.
- Estado de navegación.
- Acciones de autenticación.
- Acciones de libros.
- Acciones de préstamos.
- Acciones de reservas.
- Acciones de reseñas.
- Acciones de favoritos.
- Acciones de PDF.
- Acciones de tema y sidebar.

El contexto existe porque varias pantallas necesitan compartir decisiones. Por ejemplo, `CatalogPage`, `FavoritesPage` y `BookDetailPage` usan libros y favoritos. Si cada pantalla hiciera sus propias peticiones sin coordinación, sería más fácil dejar datos viejos en pantalla.

### 4.2 Qué Vive En `AppContext`

Vive en `AppContext` lo que representa estado de experiencia de usuario:

- Vista actual.
- Libro seleccionado.
- Usuario actual.
- Filtros seleccionados.
- Tema.
- Sidebar colapsado.
- Métodos de alto nivel.

### 4.3 Qué Vive En TanStack Query

Vive en TanStack Query lo que viene del backend:

- Página de libros.
- Facetas.
- Favoritos.
- Préstamos.
- Reservas.
- Notificaciones.
- Reseñas de administración.

Esto permite invalidar queries en vez de intentar reconstruir manualmente todo el estado.

### 4.4 Qué Vive En Estado Local

Vive en `useState` local lo que solo importa dentro de un componente:

- Texto escrito en un formulario.
- Si un modal está abierto.
- Archivo PDF seleccionado.
- Filtro temporal de fechas.
- Página actual del PDF.
- Zoom del lector.
- Pregunta actual del chat.

Esta separación evita que `AppContext` crezca con estados que no necesitan ser globales.

## 5. Cliente HTTP

### 5.1 `src/api/http.ts`

Responsabilidades:

- Guardar token en memoria y `localStorage`.
- Restaurar token.
- Construir URLs.
- Adjuntar `Authorization`.
- Serializar JSON.
- Parsear respuestas JSON o texto.
- Convertir errores en `ApiError`.
- Limpiar sesión si llega un `401`.

Funciones:

- `setAuthToken(token)`
- `restoreTokenFromStorage()`
- `getAuthToken()`
- `getApiErrorMessage(err, fallback)`
- `isUnauthorizedError(err)`
- `isForbiddenError(err)`
- `buildApiUrl(path, query)`
- `http<T>(path, options)`

Decisión técnica:

El proyecto usa `fetch` con wrapper propio, no Axios. Esto reduce dependencias y mantiene control exacto sobre streaming, headers, serialización y manejo de 401.

### 5.2 Manejo De 401

Cuando `http.ts` recibe `401`:

1. Borra token.
2. Borra usuario local.
3. Dispara evento `bookworm:unauthorized`.

`AppContext` escucha ese evento y:

- Limpia usuario.
- Limpia queries sensibles.
- Redirige a `login`.

## 6. APIs Del Frontend

### 6.1 `src/api/auth.ts`

Expone:

- `authApi.login(payload)`
- `authApi.register(payload)`
- `authApi.logout()`

Tipos:

- `Gender`
- `Role`
- `LoginRequest`
- `LoginResponse`
- `RegisterRequest`
- `RegisterResponse`

`login` guarda token llamando `setAuthToken`.

### 6.2 `src/api/books.ts`

Expone:

- `booksApi.list(params)`
- `booksApi.get(id)`
- `booksApi.facets()`
- `booksApi.create(payload)`
- `booksApi.update(id, payload)`
- `booksApi.updateStatus(id, status)`
- `booksApi.deleteBook(id)`
- `booksApi.availability(id)`
- `booksApi.copies(id)`
- `booksApi.createCopy(id)`
- `booksApi.deleteCopy(id, copyId)`
- `booksApi.uploadPdf(id, file)`
- `booksApi.downloadPdf(id, url)`
- `booksApi.pdfUrl(id)`

Decisión técnica:

`uploadPdf` usa `FormData` y `fetchWithAuth` porque no puede enviarse como JSON. El resto de operaciones usa `http<T>`.

### 6.3 `src/api/chat.ts`

Expone:

- `chatStream(payload, handlers)`

No usa `http<T>` porque la respuesta es un stream SSE, no un JSON único.

Maneja:

- `onChunk`
- `onDone`
- `onError`
- `AbortSignal`

### 6.4 `src/api/favorites.ts`

Expone:

- `favoritesApi.list()`
- `favoritesApi.toggle(bookId)`

El backend usa un endpoint tipo toggle; por eso el frontend recibe `favorite: true` o `favorite: false`.

### 6.5 `src/api/loans.ts`

Expone:

- `loansApi.listMine()`
- `loansApi.listAll()`
- `loansApi.create(bookId, durationMinutes)`
- `loansApi.returnLoan(id)`
- `loansApi.renew(id, durationMinutes)`
- `loansApi.history(id)`

### 6.6 `src/api/reservations.ts`

Expone:

- `reservationsApi.listMine()`
- `reservationsApi.create(bookId, requestedLoanDurationMinutes)`
- `reservationsApi.cancel(id)`

### 6.7 `src/api/reviews.ts`

Expone:

- `reviewsApi.byBook(bookId)`
- `reviewsApi.list(status)`
- `reviewsApi.create(payload)`
- `reviewsApi.hide(id)`
- `reviewsApi.show(id)`

### 6.8 `src/api/notifications.ts`

Expone:

- `notificationsApi.list(status, page, size)`
- `notificationsApi.unreadCount()`
- `notificationsApi.markAsRead(id)`
- `notificationsApi.markAllAsRead()`

### 6.9 `src/api/users.ts`

Expone:

- `usersApi.me()`
- `usersApi.updateMe(payload)`
- `usersApi.changePassword(payload)`

### 6.10 `src/api/mappers.ts`

Este archivo traduce DTOs del backend al modelo visual del frontend.

Funciones:

- `mapBackendRole(role)`
- `userFromBackend(user, email?)`
- `bookFromBackend(b)`
- `bookToUpsert(book)`
- `reviewFromBackend(r)`
- `loanFromBackend(l)`
- `reservationFromBackend(r)`

Decisión técnica:

El backend y el frontend no usan exactamente los mismos nombres ni los mismos tipos. Por ejemplo:

- Backend usa rol `ADMIN`; frontend usa `admin`.
- Backend usa ids numéricos; frontend usa strings en `mockData`.
- Backend usa `BookResponse.availableCopies`; frontend necesita `book.available`.

El mapper concentra esas conversiones en un solo lugar.

## 7. TanStack Query

### 7.1 `src/lib/queryClient.ts`

Configuración:

```ts
staleTime: 30_000
retry: 1
refetchOnWindowFocus: false
```

Esta configuración evita recargas excesivas y mantiene UX estable durante navegación.

### 7.2 `src/hooks/queryKeys.ts`

Define llaves centralizadas:

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

Decisión técnica:

Centralizar keys evita errores como invalidar `["book"]` cuando la query real era `["books"]`.

### 7.3 `src/hooks/useLibraryQueries.ts`

Hooks:

- `useBooksQuery(params)`
- `useBookFacetsQuery()`
- `useFavoritesQuery(enabled)`
- `useLoansQuery(canManageLibrary, enabled)`
- `useReservationsQuery(enabled)`
- `useNotificationsQuery(enabled, status, page, size)`
- `useUnreadNotificationsCountQuery(enabled)`
- `useMarkNotificationReadMutation()`
- `useMarkAllNotificationsReadMutation()`
- `useAdminReviewsQuery(enabled)`

Decisión técnica:

Los hooks encapsulan llamadas API y mappers. Las páginas no necesitan saber cómo convertir `BookResponse` a `Book`; solo consumen `books`.

## 8. Hook De Responsividad

### 8.1 `src/hooks/use-mobile.ts`

Hook:

```ts
useIsMobile()
```

Funciona con:

```text
window.matchMedia("(max-width: 767px)")
```

Retorna booleano.

Por qué existe:

- Centraliza el breakpoint móvil.
- Evita repetir listeners en cada componente.
- Limpia el listener al desmontar.
- Reduce riesgo de inconsistencias entre componentes.
- Hace que la intención sea explícita: el componente pregunta “¿estoy en móvil?” y no reimplementa lógica de viewport.

Nota técnica:

Actualmente no hay usos detectados de `useIsMobile` en el código. Aun así, está documentado porque existe como hook reusable y representa una decisión de arquitectura para responsividad.

## 9. Modelos Visuales

Archivo:

```text
src/mocks/mockData.ts
```

Interfaces:

- `Book`
- `Review`
- `User`
- `Loan`
- `Reservation`

Aunque el nombre del archivo diga `mocks`, estas interfaces se usan como modelo visual del frontend.

### 9.1 `Book`

Representa libro en UI. Incluye:

- Datos bibliográficos.
- Estado activo/disponible.
- Métricas de reseña.
- PDF.
- Copias.
- Cola.
- Cooldown.

### 9.2 `Review`

Representa reseña visible u oculta:

- `flagged` indica si está oculta o marcada.
- `flagReason` guarda razón o estado textual.

### 9.3 `Loan`

Representa préstamo:

- Estado visual en minúscula: `active`, `overdue`, `returned`.
- `canRenew`.
- `blockedReason`.
- Copia asociada.

### 9.4 `Reservation`

Representa reserva:

- `waiting`
- `fulfilled`
- `cancelled`

Incluye posición de cola y préstamo cumplido si ya fue asignada.

## 10. Validaciones Frontend

Archivo:

```text
src/utils/validation.ts
```

Funciones:

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

Decisión técnica:

El frontend valida para dar respuesta rápida al usuario. El backend valida de nuevo para seguridad y consistencia. Nunca se debe confiar solamente en el frontend.

Reglas duplicadas importantes:

- Edad mínima 13 años.
- Préstamos y reservas entre 5 y 10080 minutos.
- Reseñas entre 1 y 5.
- Comentario máximo 2000.
- Libro con título, autor e ISBN obligatorios.

## 11. Utilidades De Presentación

### 11.1 `src/utils/display.ts`

`formatLanguage(language)` corrige etiquetas como:

```text
espanol -> Español
```

Esto existe porque los seeders o datos externos pueden venir sin tilde. La UI debe presentar texto legible aunque la base tenga una variación.

## 12. Layouts

### 12.1 `Sidebar`

Archivo:

```text
src/layouts/Sidebar.tsx
```

Responsabilidades:

- Mostrar navegación principal.
- Resaltar vista actual.
- Mostrar usuario actual.
- Permitir colapsar menú.
- Mostrar administración solo si el usuario es admin o librarian.

Se comunica con `AppContext` mediante:

- `currentUser`
- `currentView`
- `setCurrentView`
- `sidebarCollapsed`
- `toggleSidebar`

### 12.2 `Topbar`

Archivo:

```text
src/layouts/Topbar.tsx
```

Responsabilidades:

- Mostrar título de la vista.
- Alternar tema.
- Mostrar campana de notificaciones.
- Abrir confirmación de logout.

Usa:

- `NotificationBell`
- `ConfirmDialog`

## 13. Páginas

### 13.1 `LoginPage`

Componente:

```text
Login
```

Responsabilidades:

- Capturar email y contraseña.
- Validar campos.
- Mostrar/ocultar contraseña.
- Llamar `login`.
- Navegar a registro.
- Mostrar errores con `toast`.

### 13.2 `RegisterPage`

Componente:

```text
Register
```

Responsabilidades:

- Capturar nombre, email, password, confirmación, género y fecha de nacimiento.
- Validar fuerza de password.
- Validar edad mínima usando `maxBirthDateForMinimumAge`.
- Llamar `register`.
- Iniciar sesión automáticamente después del registro.

### 13.3 `HomePage`

Componente:

```text
Home
```

Responsabilidades:

- Mostrar resumen de biblioteca.
- Mostrar categorías.
- Mostrar libros destacados o recientes.
- Usar datos del catálogo y categorías.
- Navegar a catálogo o detalle.

### 13.4 `CatalogPage`

Componente:

```text
Catalog
```

Responsabilidades:

- Mostrar búsqueda.
- Mostrar filtros.
- Mostrar grid de `BookCard`.
- Mostrar paginación.
- Abrir diálogo si usuario no autenticado intenta favorito.

Usa:

- `BookFilters`
- `BookCard`
- `CatalogPagination`
- `BookGridSkeleton`
- `AuthRequiredDialog`
- `EmptyState`

Decisión técnica:

`BookCard` está separado porque catálogo y favoritos comparten la misma representación de libro. Esto evita duplicar layout, badges, rating y botón de favorito.

### 13.5 `FavoritesPage`

Componente:

```text
Favorites
```

Responsabilidades:

- Exigir usuario autenticado.
- Filtrar libros favoritos a partir de `favorites`.
- Reutilizar `BookCard`.
- Permitir abrir detalle.
- Permitir quitar favorito.

### 13.6 `BookDetailPage`

Componente:

```text
BookDetail
```

Responsabilidades:

- Mostrar detalle del libro seleccionado.
- Crear préstamo.
- Crear reserva.
- Abrir lector.
- Crear reseña.
- Eliminar reseña local.
- Mostrar confirmaciones.
- Exigir login para acciones protegidas.

Usa:

- `BookOverview`
- `BookActionPanel`
- `ReviewSection`
- `ConfirmDialog`
- `AuthRequiredDialog`

### 13.7 `BookReaderPage`

Componente:

```text
BookReader
```

Responsabilidades:

- Cargar PDF.
- Renderizar página actual.
- Manejar zoom.
- Navegar páginas.
- Capturar texto seleccionado.
- Abrir o cerrar chat IA.
- Permitir cargar PDF por URL si corresponde.

Usa:

- `booksApi.pdfUrl`
- `getAuthToken`
- `ChatPanel`
- `Dialog`
- `EmptyState`

### 13.8 `LoansPage`

Componente:

```text
Loans
```

Responsabilidades:

- Exigir usuario autenticado.
- Mostrar préstamos.
- Filtrar por fecha.
- Devolver préstamo.
- Renovar préstamo.
- Consultar historial de renovaciones.

Usa:

- `LoanCard`
- `LoanDurationSelect`
- `LoanDateFilters`
- `Dialog`

Filtros:

- Campo: fecha de préstamo, vencimiento o devolución.
- Rango: fecha inicial y fecha final.

### 13.9 `ReservationsPage`

Componente:

```text
Reservations
```

Responsabilidades:

- Exigir usuario autenticado.
- Mostrar reservas del usuario.
- Cancelar reservas pendientes.

Usa:

- `ReservationCard`
- `EmptyState`

### 13.10 `ReviewsPage`

Componente:

```text
Reviews
```

Responsabilidades:

- Exigir usuario autenticado.
- Mostrar reseñas del usuario actual.
- Mostrar libro asociado.
- Abrir detalle del libro.

Usa:

- `RatingStars`
- `ReviewComment`
- `EmptyState`

### 13.11 `ProfilePage`

Componente:

```text
Profile
```

Responsabilidades:

- Mostrar datos del usuario.
- Editar nombre.
- Cambiar contraseña.
- Mostrar rol.
- Mostrar actividad del usuario.
- Mostrar favoritos y reseñas propias.

Usa:

- `RoleBadge`
- `PasswordStrength`
- `RatingStars`
- `EmptyState`

### 13.12 `AdminPage`

Componente:

```text
Admin
```

Responsabilidades:

- Bloquear acceso a usuarios sin rol.
- Mostrar dashboard.
- Gestionar libros.
- Gestionar copias.
- Gestionar préstamos.
- Moderar reseñas.
- Subir o descargar PDFs.
- Eliminar libro con confirmación.

Tabs:

- `dashboard`
- `books`
- `loans`
- `reviews`

Usa:

- `Chart.js`
- `Bar`
- `Doughnut`
- `Table`
- `ConfirmDialog`
- `ReviewComment`
- `StatusBadge`
- `booksApi.copies`

Funciones internas relevantes:

- `openAddBook`
- `openEditBook`
- `closeBookForm`
- `updateBookForm`
- `submitBookForm`
- `loadCopies`
- `toggleCopies`
- `retireCopy`
- `confirmDeleteBook`
- `BookCopiesPanel`
- `formatCopyStatus`
- `getCopyRetireBlockedReason`

## 14. Componentes De Catálogo Y Detalle

### 14.1 `BookCard`

Archivo:

```text
src/components/catalog/BookCard.tsx
```

Props:

- `book`
- `isFavorite`
- `onOpen`
- `onToggleFavorite`

Responsabilidades:

- Mostrar portada.
- Mostrar título.
- Mostrar autor.
- Mostrar rating.
- Mostrar número de reseñas al lado del puntaje.
- Mostrar categoría e idioma.
- Mostrar disponibilidad.
- Ejecutar favorito.
- Abrir detalle.

Decisión técnica:

Se reutiliza en catálogo y favoritos para que una tarjeta tenga la misma apariencia en toda la app.

### 14.2 `CatalogPagination`

Props:

- `page`
- `totalPages`
- `onPageChange`

Responsabilidad:

Controlar navegación entre páginas sin que `CatalogPage` repita lógica de botones.

### 14.3 `BookFilters`

Props:

- Categorías.
- Idiomas.
- Filtro actual de categoría.
- Filtro actual de idioma.
- Filtro actual de disponibilidad.
- Callbacks de cambio.

Responsabilidad:

Mostrar selects de filtro. Usa `formatLanguage` para corregir presentación.

### 14.4 `BookOverview`

Props:

- `book`
- `reviewCount`

Responsabilidad:

Mostrar información detallada: autor, categoría, idioma, editorial, fecha, páginas, descripción, rating y estado.

### 14.5 `BookActionPanel`

Props:

- Libro.
- Usuario actual.
- Favorito.
- Préstamos.
- Reservas.
- Duración seleccionada.
- Callbacks de préstamo, reserva, favorito y lectura.

Responsabilidad:

Decidir qué acción mostrar según estado transaccional:

- Prestar.
- Reservar.
- Leer.
- Mostrar cooldown.
- Mostrar inventario.
- Mostrar posición de cola.

Subcomponentes internos:

- `InventorySummary`
- `InventoryStat`
- `LoanActionButton`
- `CooldownNotice`

### 14.6 `ReviewSection`

Responsabilidades:

- Mostrar formulario de reseña.
- Mostrar reseñas visibles.
- Mostrar `EmptyState` si no hay reseñas.
- Usar `RatingStars`.
- Usar `ReviewComment` para prevenir desbordes.

## 15. Componentes De Préstamos, Reservas Y Reseñas

### 15.1 `LoanCard`

Props:

- `loan`
- `onReturn`
- `onRenew`

Responsabilidad:

Mostrar préstamo individual con estado, fechas, botón de devolución y botón de renovación cuando aplica.

### 15.2 `LoanDurationSelect`

Props:

- `value`
- `onChange`
- `label`

Responsabilidad:

Centralizar opciones de duración. También exporta `formatLoanDuration(minutes)`.

### 15.3 `LoanDateFilters`

Props:

- `field`
- `startDate`
- `endDate`
- `onFieldChange`
- `onStartDateChange`
- `onEndDateChange`
- `onClear`

Responsabilidad:

Filtrar préstamos por fecha sin mezclar controles de filtros dentro de `LoansPage`.

### 15.4 `ReservationCard`

Props:

- `reservation`
- `onCancel`

Responsabilidad:

Mostrar reserva, estado, duración solicitada, posición de cola y acción de cancelar.

### 15.5 `ReviewComment`

Props:

- `children`

Responsabilidad:

Renderizar comentarios largos sin desbordar horizontalmente. Este componente existe porque las reseñas pueden contener texto largo sin espacios y eso puede romper la UI.

## 16. Componentes De Notificaciones

### 16.1 `NotificationBell`

Responsabilidades:

- Mostrar icono de campana.
- Mostrar contador de no leídas.
- Abrir panel desplegable.
- Listar notificaciones.
- Marcar una como leída.
- Marcar todas como leídas.
- Navegar según `targetView`.
- Cerrar al hacer clic fuera.

Hooks usados:

- `useNotificationsQuery`
- `useUnreadNotificationsCountQuery`
- `useMarkNotificationReadMutation`
- `useMarkAllNotificationsReadMutation`

Subcomponentes internos:

- `NotificationItem`
- `formatNotificationDate`

## 17. Componente De Chat IA

### 17.1 `ChatPanel`

Props:

- `bookId`
- `bookDescription`
- `selectedText`
- `onClearSelectedText`
- `onClose`

Estado local:

- `messages`
- `question`
- `isLoading`
- `error`
- `settingsOpen`
- `personalApiKey`
- `apiKeyInput`

Responsabilidades:

- Enviar pregunta al backend.
- Mostrar streaming de respuesta.
- Mantener historial local.
- Permitir cancelar stream con `AbortController`.
- Permitir configurar API key personal.
- Censurar API key en pantalla.
- Renderizar Markdown.

Funciones internas:

- `maskApiKey`
- `MessageAvatar`
- `MarkdownMessage`
- `markLastAssistantDone`

Decisión técnica:

La API key personal queda en `localStorage`. No se manda a backend salvo cuando el usuario hace una pregunta. No se guarda en base de datos.

## 18. Componentes Compartidos

### 18.1 `AuthRequiredDialog`

Muestra diálogo cuando una acción requiere login. Puede navegar a:

- Login.
- Registro.

### 18.2 `ConfirmDialog`

Envuelve `AlertDialog` para confirmaciones destructivas o importantes.

Se usa en:

- Logout.
- Eliminar libro.
- Otras acciones que requieren confirmación explícita.

### 18.3 `EmptyState`

Muestra estados vacíos consistentes.

Props:

- `icon`
- `title`
- `description`
- `actionLabel`
- `onAction`

### 18.4 `ImageWithFallback`

Renderiza imagen y maneja error de carga. Si falla, muestra fallback visual.

### 18.5 `LoadingSkeleton`

Exporta:

- `BookCardSkeleton`
- `BookGridSkeleton`
- `TableRowSkeleton`
- `TableSkeleton`
- `BookDetailSkeleton`

Responsabilidad:

Mostrar esqueletos visuales de carga mientras TanStack Query obtiene datos.

### 18.6 `PasswordStrength`

Exporta:

- `PasswordStrength`
- `isPasswordStrong`
- `passwordStrengthScore`

Responsabilidad:

Mostrar reglas visuales de contraseña y calcular fortaleza.

### 18.7 `RatingStars`

Muestra estrellas para lectura o selección de rating.

Props:

- `rating`
- `onRatingChange`
- `readonly`
- `size`

### 18.8 `RoleBadge`

Muestra rol del usuario con icono y etiqueta.

### 18.9 `StatusBadge`

Normaliza estados visuales:

- Disponible.
- No disponible.
- Activo.
- Vencido.
- Devuelto.
- Reservado.
- Oculto.

## 19. Componentes UI Base

La carpeta:

```text
src/components/ui
```

contiene wrappers reutilizables.

### 19.1 `button.tsx`

Define `Button` y variantes con `class-variance-authority`.

Decisión:

Evita repetir clases Tailwind de botones en cada pantalla.

### 19.2 `badge.tsx`

Define `Badge` para etiquetas visuales.

### 19.3 `card.tsx`

Define:

- `Card`
- `CardHeader`
- `CardTitle`
- `CardDescription`
- `CardAction`
- `CardContent`
- `CardFooter`

### 19.4 `dialog.tsx`

Wrapper sobre Radix Dialog:

- `Dialog`
- `DialogTrigger`
- `DialogPortal`
- `DialogClose`
- `DialogOverlay`
- `DialogContent`
- `DialogHeader`
- `DialogFooter`
- `DialogTitle`
- `DialogDescription`

### 19.5 `alert-dialog.tsx`

Wrapper sobre Radix Alert Dialog para confirmaciones.

### 19.6 `input.tsx`

Input base con estilos consistentes.

### 19.7 `textarea.tsx`

Textarea base con estilos consistentes.

### 19.8 `select.tsx`

Wrapper sobre Radix Select:

- `Select`
- `SelectGroup`
- `SelectValue`
- `SelectTrigger`
- `SelectContent`
- `SelectLabel`
- `SelectItem`
- `SelectSeparator`
- `SelectScrollUpButton`
- `SelectScrollDownButton`

### 19.9 `table.tsx`

Define tabla estilizada:

- `Table`
- `TableHeader`
- `TableBody`
- `TableFooter`
- `TableRow`
- `TableHead`
- `TableCell`
- `TableCaption`

### 19.10 `skeleton.tsx`

Define `Skeleton`.

### 19.11 `sonner.tsx`

Exporta `Toaster` configurado.

### 19.12 `utils.ts`

Define:

```ts
cn(...inputs)
```

Combina `clsx` y `tailwind-merge`. Esto permite componer clases condicionales sin duplicados conflictivos.

## 20. Flujos De UI Importantes

### 20.1 Login

```text
LoginPage
  -> validateLogin
  -> AppContext.login
  -> authApi.login
  -> http("/auth/login")
  -> backend
  -> setAuthToken
  -> persistSession
  -> invalidateBooks
```

### 20.2 Registro

```text
RegisterPage
  -> validateRegister
  -> AppContext.register
  -> authApi.register
  -> AppContext.login
```

### 20.3 Filtrar Catálogo

```text
BookFilters / búsqueda
  -> setCategoryFilter / setLanguageFilter / setAvailabilityFilter
  -> setBookPage(0)
  -> bookQueryParams cambia
  -> useBooksQuery refetch
  -> booksApi.list
```

### 20.4 Crear Préstamo

```text
BookDetailPage
  -> BookActionPanel
  -> addLoan
  -> loansApi.create
  -> refreshLoans
  -> refreshReservations
  -> refreshNotifications
  -> invalidateBooks
  -> refetchSelectedBook
```

### 20.5 Crear Reserva

```text
BookDetailPage
  -> BookActionPanel
  -> createReservation
  -> reservationsApi.create
  -> refreshReservations
  -> refreshNotifications
  -> invalidateBooks
  -> refetchSelectedBook
```

### 20.6 Devolver Préstamo

```text
LoansPage / AdminPage
  -> updateLoan(status: "returned")
  -> loansApi.returnLoan
  -> refreshLoans
  -> refreshReservations
  -> refreshNotifications
  -> invalidateBooks
  -> refetchSelectedBook
```

### 20.7 Moderar Reseña

```text
AdminPage
  -> hideReview / keepReviewVisible
  -> reviewsApi.hide / reviewsApi.show
  -> refreshAdminReviews
  -> invalidateBooks
  -> refetchSelectedBook
```

### 20.8 Eliminar Libro

```text
AdminPage
  -> ConfirmDialog
  -> deleteBook
  -> booksApi.deleteBook
  -> limpiar caches locales
  -> removeQueries(book)
  -> refreshBookFacets
  -> refreshFavorites
  -> refreshLoans
  -> refreshReservations
  -> refreshAdminReviews
```

## 21. Decisiones Técnicas Importantes

### 21.1 Por Qué Hay Mappers

Los mappers evitan acoplar toda la UI al backend. Si el backend cambia un enum o agrega un campo, se toca un archivo central antes de tocar todas las páginas.

### 21.2 Por Qué Reutilizar `BookCard`

La tarjeta de libro se ve en catálogo y favoritos. Si cada pantalla tuviera su propia tarjeta, los cambios visuales se duplicarían y podrían quedar inconsistentes.

### 21.3 Por Qué Las Facetas Vienen Del Backend

Las categorías e idiomas deben representar el universo completo de libros, no solo la página o filtro actual. Esto evita que el selector se reduzca accidentalmente después de elegir una categoría.

### 21.4 Por Qué Las Acciones Están En `AppContext`

Acciones como prestar o devolver afectan múltiples queries. Centralizarlas evita que cada componente recuerde qué invalidar.

### 21.5 Por Qué El Chat No Usa `http<T>`

`http<T>` espera una respuesta terminada. El chat recibe chunks. Por eso `chatStream` usa `fetch`, `ReadableStream` y callbacks.

### 21.6 Por Qué Hay Componentes UI Propios

Los componentes de `ui` envuelven Radix y clases Tailwind. Esto permite:

- Accesibilidad base.
- Estilos consistentes.
- Menos duplicación.
- Cambios visuales globales desde un solo lugar.

### 21.7 Por Qué Hay Validación En Frontend Y Backend

Frontend valida para UX rápida. Backend valida para seguridad real. Cualquier persona puede saltarse el frontend y llamar la API directamente, por eso las reglas críticas viven en backend.

## 22. Riesgos Y Puntos De Atención Para Nuevos Desarrolladores

### 22.1 No Derivar Estado Servidor Manualmente

Si una acción cambia backend, usar invalidaciones o refetch. No asumir que actualizar un arreglo local es suficiente.

### 22.2 No Romper `queryKeys`

Si se agrega una nueva query, agregar key centralizada.

### 22.3 No Guardar Secretos Permanentes En Backend Sin Diseño

La API key personal de DeepSeek vive en `localStorage`, no en base de datos. Si se quisiera persistir, habría que cifrar, controlar acceso y permitir borrado.

### 22.4 No Confiar En `available` Sin Copias

La disponibilidad real depende de:

- `book.status`
- `book.availableCopies`

### 22.5 No Repetir Lógica De Reglas De Negocio En Páginas

Las páginas pueden decidir qué mostrar, pero las reglas finales las impone backend.
