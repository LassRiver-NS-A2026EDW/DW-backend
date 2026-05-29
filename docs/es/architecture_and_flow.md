# Arquitectura Y Flujo End-To-End De BookWorm

## 1. Propósito Del Documento

Este documento explica cómo funciona BookWorm de extremo a extremo. La intención no es solamente describir que existe un frontend y un backend, sino explicar qué ocurre desde que una persona hace clic en la interfaz hasta que los datos quedan persistidos en PostgreSQL y vuelven a representarse en React.

BookWorm es una aplicación web de biblioteca digital. Permite registrar usuarios, iniciar sesión, consultar libros, leer PDFs dentro de la aplicación, gestionar préstamos, crear reservas cuando no hay ejemplares disponibles, escribir reseñas, moderarlas desde administración, consultar notificaciones transaccionales y usar un asistente IA dentro del lector.

El sistema está dividido en dos aplicaciones principales:

- `DW-frontend-ViteReact`: aplicación cliente hecha con React, Vite y TypeScript.
- `DW-backend-SpringBoot`: API REST hecha con Spring Boot, Spring Security, Spring Data JPA, Flyway y PostgreSQL.

La base de datos es relacional. Esto es importante porque el dominio depende de relaciones fuertes: usuarios tienen préstamos, libros tienen ejemplares, los préstamos toman un ejemplar concreto, las reservas se ordenan por cola, las reseñas pertenecen a usuarios y libros, y las notificaciones pertenecen a usuarios.

## 2. Diagrama Textual De Arquitectura

```text
┌─────────────────────────────────────────────────────────────────┐
│ Usuario                                                         │
│ - Hace clic                                                     │
│ - Llena formularios                                             │
│ - Lee libros                                                    │
│ - Consulta notificaciones                                       │
└──────────────────────────────┬──────────────────────────────────┘
                               │
                               ▼
┌─────────────────────────────────────────────────────────────────┐
│ Frontend: React + Vite + TypeScript                             │
│                                                                 │
│ App.tsx                                                         │
│ ├─ QueryClientProvider                                          │
│ ├─ AppProvider                                                  │
│ ├─ Sidebar / Topbar                                             │
│ └─ Páginas por currentView                                      │
│                                                                 │
│ Estado cliente                                                  │
│ ├─ AppContext: sesión, navegación y acciones de alto nivel       │
│ ├─ TanStack Query: datos remotos cacheados                      │
│ └─ Estado local: formularios, modales, filtros visuales          │
└──────────────────────────────┬──────────────────────────────────┘
                               │ fetch / SSE
                               ▼
┌─────────────────────────────────────────────────────────────────┐
│ API HTTP                                                        │
│ Base URL: /api                                                  │
│                                                                 │
│ src/api/http.ts                                                 │
│ ├─ Construye URLs                                               │
│ ├─ Adjunta Authorization: Bearer <token>                        │
│ ├─ Serializa JSON                                               │
│ ├─ Normaliza errores                                            │
│ └─ Borra sesión si recibe 401                                   │
└──────────────────────────────┬──────────────────────────────────┘
                               │
                               ▼
┌─────────────────────────────────────────────────────────────────┐
│ Backend: Spring Boot                                            │
│                                                                 │
│ SecurityConfig                                                  │
│ ├─ CORS                                                         │
│ ├─ CSRF deshabilitado para API stateless                        │
│ ├─ Reglas por ruta y rol                                        │
│ └─ JwtAuthenticationFilter                                      │
│                                                                 │
│ Controllers                                                     │
│ ├─ Validan DTOs con jakarta.validation                          │
│ ├─ Extraen path/query/body                                      │
│ └─ Delegan a services                                           │
│                                                                 │
│ Services                                                        │
│ ├─ Contienen reglas de negocio                                  │
│ ├─ Usan @Transactional                                          │
│ ├─ Bloquean filas cuando hay concurrencia crítica               │
│ └─ Orquestan entidades relacionadas                             │
│                                                                 │
│ Repositories                                                    │
│ ├─ JpaRepository                                                │
│ ├─ JpaSpecificationExecutor                                     │
│ ├─ Queries derivadas                                            │
│ └─ Locks pesimistas cuando aplica                               │
└──────────────────────────────┬──────────────────────────────────┘
                               │ JDBC
                               ▼
┌─────────────────────────────────────────────────────────────────┐
│ PostgreSQL                                                      │
│                                                                 │
│ Flyway controla el esquema                                      │
│ ├─ users                                                        │
│ ├─ books                                                        │
│ ├─ book_copies                                                  │
│ ├─ loans                                                        │
│ ├─ loan_renewals                                                │
│ ├─ reservations                                                 │
│ ├─ reviews                                                      │
│ ├─ user_favorites                                               │
│ └─ notifications                                                │
└─────────────────────────────────────────────────────────────────┘
```

## 3. Capas Del Frontend

### 3.1 `main.tsx`

`main.tsx` es el punto de entrada del frontend. Su responsabilidad es mínima y deliberada:

```text
main.tsx
  -> createRoot(...)
    -> render(<App />)
```

No contiene lógica de negocio. Esto evita mezclar arranque técnico con reglas de navegación, autenticación o datos.

### 3.2 `App.tsx`

`App.tsx` compone los providers principales:

- `QueryClientProvider`: habilita TanStack Query en toda la aplicación.
- `AppProvider`: expone el contexto propio de BookWorm.
- `Toaster`: muestra mensajes globales con `sonner`.

Después renderiza `AppContent`, que decide qué pantalla mostrar según `currentView`.

La navegación principal no usa rutas URL tradicionales. Aunque el proyecto tiene `react-router` como dependencia, el flujo actual usa `currentView` en `AppContext`. Esto permite cambiar de pantalla con acciones internas como `setCurrentView("catalog")`, `setCurrentView("book-detail")` o `setCurrentView("admin")`.

### 3.3 `AppContext`

`AppContext` es el coordinador del frontend. No reemplaza a TanStack Query; más bien organiza:

- Usuario autenticado.
- Vista actual.
- Libro seleccionado.
- Tema visual.
- Sidebar abierto o colapsado.
- Filtros de catálogo.
- Acciones de alto nivel: prestar, reservar, devolver, renovar, reseñar, crear libro, borrar libro, subir PDF.

La razón técnica de tener un contexto es que muchas pantallas necesitan reaccionar a las mismas acciones. Por ejemplo, devolver un préstamo afecta:

- La lista de préstamos.
- La disponibilidad del libro.
- La cola de reservas.
- Las notificaciones.
- El detalle del libro seleccionado.

Si cada página resolviera esto por su cuenta, el proyecto tendría lógica duplicada. `AppContext` centraliza la coordinación.

### 3.4 TanStack Query

TanStack Query se usa para datos que vienen del backend:

- Libros.
- Facetas de filtros.
- Favoritos.
- Préstamos.
- Reservas.
- Notificaciones.
- Reseñas de administración.

El `QueryClient` está configurado con:

- `staleTime: 30_000`: durante 30 segundos los datos se consideran frescos.
- `retry: 1`: una petición fallida se reintenta una vez.
- `refetchOnWindowFocus: false`: no recarga automáticamente al volver a enfocar la pestaña.

Esta decisión evita estados manuales frágiles. Por ejemplo, cuando se agrega un ejemplar, no se intenta recalcular todo en memoria; se invalidan queries y el backend vuelve a entregar la verdad.

## 4. Capas Del Backend

### 4.1 Controllers

Los controllers reciben HTTP. En BookWorm existen controllers para:

- Autenticación.
- Usuarios.
- Libros.
- PDFs.
- Copias.
- Préstamos.
- Reservas.
- Reseñas.
- Favoritos.
- Notificaciones.
- Chat IA.

Los controllers no contienen reglas profundas. Su función es:

1. Declarar ruta y método HTTP.
2. Recibir `@RequestBody`, `@PathVariable` y `@RequestParam`.
3. Ejecutar validación con `@Valid`.
4. Extraer el usuario autenticado con `@AuthenticationPrincipal`.
5. Delegar al servicio.
6. Retornar `ResponseEntity`.

### 4.2 DTOs

Los DTOs separan el contrato público del modelo de base de datos.

Ejemplo: `Book` contiene `pdfPath`, que es una ruta interna del servidor. El frontend no debe depender de esa ruta. Por eso `BookResponse` expone:

- `hasPdf`.
- `pdfUrl`.

Esta separación protege al sistema. Si mañana cambia el almacenamiento de PDFs a S3, el frontend puede seguir usando `pdfUrl` sin conocer detalles internos.

### 4.3 Services

Los services contienen la lógica de negocio. Aquí viven reglas como:

- Un usuario no puede tener más de 3 préstamos activos.
- Un préstamo dura mínimo 5 minutos y máximo 10080 minutos.
- Una reserva solo se crea si no hay copias disponibles.
- Una reserva no puede duplicarse para el mismo usuario y libro.
- Un libro no se elimina si tiene préstamos activos o reservas en espera.
- Una copia no se retira si está prestada o si es la última copia operativa.
- Una reseña solo se crea si el usuario ya tuvo un préstamo del libro.
- La lectura de PDF requiere préstamo activo, salvo para admin o librarian.
- El chat IA del libro requiere préstamo activo, salvo para admin o librarian.

### 4.4 Repositories

Los repositories encapsulan acceso a datos con Spring Data JPA.

Hay métodos simples, como `findByEmail`, y métodos críticos con bloqueo:

- `BookRepository.findLockedById`
- `LoanRepository.findLockedById`
- `BookCopyRepository.findByIdAndBookId`
- `ReservationRepository.findAllByBookIdAndStatusOrderByCreatedAtAsc`

Los locks pesimistas son relevantes porque préstamos, devoluciones y reservas son operaciones concurrentes. Si dos usuarios intentan tomar el último ejemplar al mismo tiempo, la transacción debe evitar que ambos reciban la misma copia.

### 4.5 Entidades

Las entidades representan tablas:

- `User`: identidad, credenciales y rol.
- `Book`: metadatos bibliográficos y PDF.
- `BookCopy`: ejemplares físicos o lógicos prestables.
- `Loan`: préstamo activo, vencido o devuelto.
- `LoanRenewal`: historial de renovaciones.
- `Reservation`: cola de espera.
- `Review`: reseña visible u oculta.
- `UserFavorite`: relación usuario-libro para favoritos.
- `Notification`: evento persistido para un usuario.

## 5. Seguridad End-To-End

### 5.1 Registro

El registro se hace con:

```text
POST /api/auth/register
```

El frontend valida:

- Nombre.
- Email.
- Password.
- Confirmación de password.
- Género.
- Fecha de nacimiento.
- Edad mínima.

El backend vuelve a validar porque nunca se debe confiar solamente en el cliente. En `UserServiceImpl` existe una regla de edad mínima de 13 años.

La contraseña se cifra con `Argon2PasswordEncoder`, no se guarda en texto plano.

### 5.2 Login

El login se hace con:

```text
POST /api/auth/login
```

El backend usa `AuthenticationManager`. Si las credenciales son válidas:

1. Busca el usuario.
2. Genera un JWT.
3. Retorna `token`, `id`, `name` y `role`.

El frontend guarda:

- Token en `localStorage`.
- Usuario mínimo en `localStorage`.
- Token también en memoria mediante `setAuthToken`.

### 5.3 Peticiones Autenticadas

`http.ts` adjunta:

```text
Authorization: Bearer <token>
```

El backend usa `JwtAuthenticationFilter`, que:

1. Lee el header.
2. Extrae el token.
3. Obtiene el email desde el subject.
4. Carga `UserDetails`.
5. Valida firma y expiración.
6. Pobla `SecurityContextHolder`.

Desde ese punto, los controllers pueden recibir `UserDetails` con `@AuthenticationPrincipal`.

### 5.4 Roles

Roles del sistema:

- `USER`: usuario lector.
- `LIBRARIAN`: bibliotecario.
- `ADMIN`: administrador.

Operaciones de administración requieren `ADMIN` o `LIBRARIAN`, por ejemplo:

- Crear libros.
- Editar libros.
- Cambiar estado.
- Subir o descargar PDF desde URL.
- Gestionar copias.
- Consultar todos los préstamos.
- Moderar reseñas.
- Eliminar libros.

## 6. Flujo De Catálogo

### 6.1 Acción Del Usuario

El usuario entra a `Catálogo`, escribe una búsqueda o selecciona filtros.

Los filtros disponibles son:

- Búsqueda general.
- Categoría.
- Idioma.
- Disponibilidad.
- Paginación.

### 6.2 Frontend

`CatalogPage` toma datos desde `useApp`:

- `books`
- `bookCategories`
- `bookLanguages`
- `searchQuery`
- `categoryFilter`
- `languageFilter`
- `availabilityFilter`
- `bookPage`
- `bookTotalPages`

Cuando el usuario cambia un filtro, `AppContext` reinicia la página a cero. Esto evita pedir una página que quizá ya no existe después del filtrado.

`AppContext` construye `bookQueryParams` y los pasa a `useBooksQuery`.

### 6.3 Backend

El frontend llama:

```text
GET /api/books
```

`BookServiceImpl` construye una `Specification<Book>` dinámica. Esto permite combinar filtros sin crear un endpoint distinto por cada combinación.

La disponibilidad se calcula consultando `book_copies`, no solamente el `status` del libro. Un libro está disponible si:

- El libro está `ACTIVE`.
- Existe al menos una copia `AVAILABLE`.

Esto corrige el caso en el que un libro activo no tiene ejemplares libres.

### 6.4 Respuesta

El backend devuelve `PageResponse<BookResponse>`.

El frontend transforma cada `BookResponse` con `bookFromBackend`. Esa función adapta nombres y tipos al modelo visual `Book`.

## 7. Flujo De Facetas De Filtros

Las categorías e idiomas se piden con:

```text
GET /api/books/facets
```

La decisión importante es que las facetas vienen del backend y no de la lista ya filtrada en el frontend.

Si el frontend calcula categorías con los libros actualmente visibles, aparece un error de UX: al filtrar por una categoría, la lista de categorías se reduce a esa única categoría. El usuario queda atrapado visualmente. Por eso las facetas deben venir de la base completa.

## 8. Flujo De Detalle De Libro

### 8.1 Acción

El usuario hace clic en una tarjeta de libro.

### 8.2 Frontend

`BookCard` llama `onOpen(book)`. La página o el contexto hacen:

```text
setSelectedBook(book)
setCurrentView("book-detail")
```

Al seleccionar un libro, `AppContext` también intenta refrescar el libro específico y sus reseñas.

### 8.3 Backend

Se llama:

```text
GET /api/books/{id}
GET /api/reviews/book/{bookId}
```

`BookResponse` incluye datos agregados:

- Promedio de reseñas visibles.
- Cantidad de reseñas visibles.
- Si tiene PDF.
- Copias totales.
- Copias disponibles.
- Reservas en espera.
- Si el usuario tiene préstamo activo del libro.
- Si hay cooldown por devolución reciente.

## 9. Flujo De Préstamo

### 9.1 Acción

El usuario elige duración de préstamo y presiona el botón de préstamo.

La duración debe estar entre:

- Mínimo: 5 minutos.
- Máximo: 10080 minutos, que equivalen a 7 días.

### 9.2 Frontend

`BookActionPanel` muestra las acciones disponibles según:

- Usuario autenticado.
- Disponibilidad.
- Copias libres.
- Cola existente.
- Cooldown activo.
- Préstamo activo del usuario.

La acción llega a `AppContext.addLoan`.

Después de crear el préstamo, el frontend invalida o refresca:

- Préstamos.
- Reservas.
- Notificaciones.
- Libros.
- Libro seleccionado.

### 9.3 Backend

Se llama:

```text
POST /api/loans
```

`LoanServiceImpl.createLoan` corre dentro de `@Transactional`.

Reglas críticas:

1. Busca usuario autenticado.
2. Bloquea el libro con `findLockedById`.
3. Valida duración.
4. Valida libro activo.
5. Valida que el usuario no tenga préstamo activo del mismo libro.
6. Valida cooldown de 24 horas después de devolución.
7. Valida máximo 3 préstamos activos.
8. Busca copia disponible.
9. Cambia copia a `LOANED`.
10. Crea `Loan`.
11. Crea notificación de préstamo.

La transacción es necesaria porque cambiar copia y crear préstamo deben confirmarse juntos. Si el préstamo se crea pero la copia no cambia, el inventario queda inconsistente. Si la copia cambia pero falla el préstamo, se pierde disponibilidad.

## 10. Flujo De Devolución

### 10.1 Acción

El usuario o un administrador marca un préstamo como devuelto.

### 10.2 Backend

Se llama:

```text
PUT /api/loans/{id}/return
```

`LoanServiceImpl.returnLoan`:

1. Bloquea el préstamo.
2. Verifica que el usuario sea dueño del préstamo o tenga rol privilegiado.
3. Evita devolver dos veces.
4. Cambia `Loan.status` a `RETURNED`.
5. Asigna `returnedAt`.
6. Libera la copia con estado `AVAILABLE`.
7. Notifica devolución.
8. Si el libro está activo, intenta cumplir la siguiente reserva.

### 10.3 Asignación Automática De Reserva

Si hay cola:

1. Toma la primera reserva `WAITING` por fecha de creación.
2. Verifica que el usuario no haya alcanzado máximo de préstamos.
3. Verifica que no tenga ya préstamo abierto de ese libro.
4. Cambia la copia otra vez a `LOANED`.
5. Crea un préstamo para el usuario reservado.
6. Cambia reserva a `FULFILLED`.
7. Guarda `fulfilledAt`.
8. Vincula `fulfilledLoan`.
9. Notifica al usuario y al staff.

Este es uno de los flujos más transaccionales del sistema porque una devolución puede disparar un préstamo nuevo y cambiar la cola.

## 11. Flujo De Reserva

### 11.1 Cuándo Se Reserva

Una reserva no es un préstamo. Una reserva representa una posición en cola cuando no existen copias disponibles.

### 11.2 Frontend

El usuario ve un botón de reserva si:

- Está autenticado.
- El libro está activo.
- No hay copias disponibles.
- No tiene préstamo activo del mismo libro.
- No está en cooldown.

`createReservation(bookId, durationMinutes)` llama al backend y refresca:

- Reservas.
- Notificaciones.
- Libros.
- Libro seleccionado.

### 11.3 Backend

Se llama:

```text
POST /api/reservations
```

`ReservationServiceImpl.createReservation` valida:

- Duración entre 5 y 10080 minutos.
- Libro activo.
- Cero copias disponibles.
- El usuario no tiene préstamo abierto del libro.
- El usuario no está en cooldown de 24 horas por devolución reciente.
- El usuario no tiene otra reserva `WAITING` para el mismo libro.

Después crea `Reservation` con estado `WAITING`.

### 11.4 Posición En Cola

La posición se calcula contando reservas del mismo libro, con estado `WAITING`, cuyo `id` sea menor al de la reserva actual. A ese conteo se le suma 1.

```text
queuePosition = reservasPreviasDelLibro + 1
```

## 12. Flujo De Renovación

### 12.1 Acción

El usuario elige una duración adicional y presiona renovar.

### 12.2 Backend

Se llama:

```text
POST /api/loans/{id}/renew
```

Reglas:

- No se renueva préstamo devuelto.
- No se renueva préstamo vencido.
- Máximo 2 renovaciones por préstamo.
- No se renueva si hay usuarios esperando ese libro en cola.
- La duración adicional también debe estar entre 5 minutos y 7 días.

Se crea un registro en `loan_renewals`. Esto es importante para auditoría: no solo se modifica la fecha final, también queda historial de cuándo y cuánto se renovó.

## 13. Flujo De Reseñas

### 13.1 Creación

El usuario crea reseña desde el detalle del libro.

Se llama:

```text
POST /api/reviews
```

Reglas:

- Rating entre 1 y 5.
- Comentario obligatorio.
- Máximo 2000 caracteres.
- Solo una reseña por usuario y libro.
- El usuario debe haber tenido al menos un préstamo de ese libro.

### 13.2 Consulta Pública

El detalle usa:

```text
GET /api/reviews/book/{bookId}
```

Solo devuelve reseñas `VISIBLE`.

### 13.3 Moderación

Administración usa:

```text
GET /api/reviews?status=ALL
PATCH /api/reviews/{id}/hide
PATCH /api/reviews/{id}/show
```

Ocultar no borra. Cambia `status` a `HIDDEN`. Esto preserva auditoría y permite volver a mostrar la reseña.

## 14. Flujo De Favoritos

Favoritos es una relación usuario-libro.

El frontend llama:

```text
POST /api/favorites/{bookId}
GET /api/favorites
```

El `POST` funciona como toggle:

- Si no existe favorito, lo crea.
- Si existe, lo elimina.

La tabla tiene restricción única `(user_id, book_id)` para impedir duplicados incluso si hay doble clic o concurrencia.

## 15. Flujo De Notificaciones

### 15.1 Qué Son

Las notificaciones son eventos persistidos para usuarios. No son simples mensajes temporales de UI.

Tipos:

- `LOAN_CREATED`
- `LOAN_RENEWED`
- `LOAN_RETURNED`
- `LOAN_DUE_SOON`
- `LOAN_OVERDUE`
- `RESERVATION_CREATED`
- `RESERVATION_CANCELLED`
- `RESERVATION_FULFILLED`

### 15.2 Para Quién Se Crean

Cuando ocurre un evento:

- El usuario dueño recibe una notificación.
- Staff (`ADMIN`, `LIBRARIAN`) también recibe notificación operativa.
- El staff que sea el mismo usuario del evento no se duplica.

### 15.3 Antiduplicados

`Notification` tiene `dedupeKey`. Antes de crear una notificación, el servicio pregunta si ya existe una con esa clave.

Esto evita que el scheduler o una operación repetida creen notificaciones idénticas.

### 15.4 Frontend

`NotificationBell`:

- Consulta lista de notificaciones.
- Consulta conteo de no leídas.
- Permite marcar una como leída.
- Permite marcar todas como leídas.
- Puede navegar a `loans` o `reservations` según `targetView`.
- Refresca cada 30 segundos si hay usuario autenticado.

## 16. Flujo De Administración

El panel de administración está protegido en frontend y backend.

### 16.1 Dashboard

Muestra:

- Total de libros.
- Préstamos activos.
- Préstamos vencidos.
- Reseñas ocultas.
- Gráfico de estado de préstamos.
- Gráfico de inventario operativo.
- Gráfico de libros por categoría.

Usa `chart.js` y `react-chartjs-2`.

### 16.2 Gestión De Libros

Permite:

- Crear libro.
- Editar metadatos.
- Activar o inactivar libro.
- Subir PDF.
- Descargar PDF desde URL pública permitida.
- Agregar ejemplares.
- Retirar ejemplares.
- Eliminar libro con sus relaciones permitidas.

### 16.3 Eliminar Libro

El backend no permite eliminar si:

- Hay préstamos activos.
- Hay préstamos vencidos.
- Hay reservas `WAITING`.

Si pasa validaciones, borra en orden:

1. Reservas.
2. Renovaciones.
3. Préstamos históricos.
4. Reseñas.
5. Favoritos.
6. Copias.
7. Libro.

El orden importa por claves foráneas.

## 17. Flujo Del Lector PDF

### 17.1 Carga Del PDF

`BookReaderPage` usa `booksApi.pdfUrl(id)` y carga el PDF desde:

```text
GET /api/books/{id}/pdf
```

El backend exige:

- Usuario autenticado.
- Admin/librarian o préstamo activo del libro.
- Que el libro tenga `pdfPath`.
- Que el archivo exista y sea legible.

### 17.2 Renderizado

La página maneja:

- Documento PDF.
- Página actual.
- Total de páginas.
- Zoom.
- Estado de carga.
- Error de carga.
- Texto seleccionado.
- Chat abierto o cerrado.

El lector no delega todo al navegador porque necesita seleccionar texto y pasarlo al chat como contexto.

## 18. Flujo Del Chat IA

### 18.1 Frontend

`ChatPanel` envía:

- `bookId`.
- `selectedText`.
- `context`.
- `question`.
- `providerApiKey`.
- `history`.

El transporte no usa JSON clásico de respuesta única. Usa streaming mediante `fetch` y lectura de `ReadableStream`.

### 18.2 Backend

Se llama:

```text
POST /api/chat
Accept: text/event-stream
```

`ChatServiceImpl`:

1. Busca usuario.
2. Si hay `bookId`, valida acceso.
3. Construye mensajes para el proveedor IA.
4. Crea un `SseEmitter`.
5. Lanza un hilo virtual.
6. Envía chunks al frontend.
7. Envía `[DONE]` al terminar.
8. Envía `[ERROR] ...` si falla.

### 18.3 Clave IA Del Sistema Y Clave Personal

`OpenAiCompatibleChatClient` intenta usar la API key del sistema. Si no existe o falla, puede usar `providerApiKey`.

Esto permite que la aplicación funcione en ambientes donde la clave global no está configurada o expiró, sin obligar a reiniciar backend.

El frontend guarda la clave personal en `localStorage` del navegador y la muestra censurada. No se persiste en la base de datos.

## 19. Manejo De Errores

El backend usa `GlobalExceptionHandler`.

Errores importantes:

- `ResourceNotFoundException`: 404.
- `EmailAlreadyExistsException`: 409.
- `BusinessRuleException`: 400.
- `MethodArgumentNotValidException`: 400 con mapa de campos.
- `HttpMessageNotReadableException`: 400 por JSON mal formado o enum inválido.
- `BadCredentialsException`: 401.
- `AccessDeniedException`: 403.
- `Exception`: 500.

El frontend recibe estos errores en `http.ts`, crea un `ApiError`, conserva:

- `status`.
- `code`.
- `fields`.
- `payload`.

Si el backend responde 401, el frontend limpia token, limpia usuario local y dispara `bookworm:unauthorized`.

## 20. Por Qué El Proyecto Es Transaccional

BookWorm no es solo CRUD porque varias operaciones cambian múltiples tablas y tienen reglas de consistencia.

Ejemplos:

- Crear préstamo cambia `book_copies` y crea `loans`.
- Devolver préstamo cambia `loans`, cambia `book_copies`, puede crear otro `loan`, cambia `reservations` y crea `notifications`.
- Renovar cambia `loans` y crea `loan_renewals`.
- Reservar crea `reservations` y crea `notifications`.
- Eliminar libro borra relaciones en varias tablas, pero solo si no hay operaciones vivas.
- Notificaciones usan `dedupeKey` para evitar duplicados transaccionales.
- Descargar PDF desde URL valida seguridad de red y actualiza metadatos del libro.

Esto requiere `@Transactional`, validaciones de negocio, locks pesimistas y restricciones en base de datos.

