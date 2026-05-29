package com.lassriver.bookworm.config;

import com.lassriver.bookworm.entities.Book;
import com.lassriver.bookworm.entities.BookCopy;
import com.lassriver.bookworm.entities.Loan;
import com.lassriver.bookworm.entities.Review;
import com.lassriver.bookworm.entities.User;
import com.lassriver.bookworm.entities.enums.BookCopyStatus;
import com.lassriver.bookworm.entities.enums.Gender;
import com.lassriver.bookworm.entities.enums.LoanStatus;
import com.lassriver.bookworm.repositories.BookCopyRepository;
import com.lassriver.bookworm.repositories.BookRepository;
import com.lassriver.bookworm.repositories.LoanRepository;
import com.lassriver.bookworm.repositories.ReviewRepository;
import com.lassriver.bookworm.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@Profile("dev")
@RequiredArgsConstructor
public class DevelopmentDataSeeder implements CommandLineRunner {

    private static final String DEFAULT_PASSWORD = "LassRiver2026!";

    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;
    private final LoanRepository loanRepository;
    private final BookCopyRepository bookCopyRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        Map<String, Book> books = seedBooks();
        Map<String, User> users = seedUsers();
        seedLoans(books, users);
        seedReviews(books, users);
        log.info("Development seed data checked. Default mock user password: {}", DEFAULT_PASSWORD);
    }

    private Map<String, Book> seedBooks() {
        Map<String, Book> seeded = new LinkedHashMap<>();
        for (SeedBook seed : books()) {
            Book book = bookRepository.findByIsbn(seed.isbn()).orElseGet(Book::new);
            book.setTitle(seed.title());
            book.setAuthor(seed.author());
            book.setIsbn(seed.isbn());
            book.setCategory(seed.category());
            book.setLanguage(seed.language());
            book.setPublisher(seed.publisher());
            book.setPublishDate(seed.publishDate());
            book.setPages(seed.pages());
            book.setDescription(seed.description());
            book.setCoverUrl(seed.coverUrl());
            book.setStatus(seed.available() ? "ACTIVE" : "INACTIVE");
            book = bookRepository.save(book);
            ensureSeedCopy(book);
            seeded.put(seed.key(), book);
        }
        return seeded;
    }

    private Map<String, User> seedUsers() {
        Map<String, User> seeded = new LinkedHashMap<>();
        for (SeedUser seed : users()) {
            User user = userRepository.findByEmail(seed.email()).orElseGet(User::new);
            user.setName(seed.name());
            user.setEmail(seed.email());
            user.setPassword(passwordEncoder.encode(DEFAULT_PASSWORD));
            user.setRole(seed.role());
            user.setLanguage("es");
            user.setGender(seed.gender());
            user.setBirthDate(seed.birthDate());
            user = userRepository.save(user);
            seeded.put(seed.key(), user);
        }
        return seeded;
    }

    private void seedLoans(Map<String, Book> books, Map<String, User> users) {
        for (SeedLoan seed : loans()) {
            User user = users.get(seed.userKey());
            Book book = books.get(seed.bookKey());
            if (user == null || book == null || loanRepository.existsByUserIdAndBookId(user.getId(), book.getId())) {
                continue;
            }

            loanRepository.save(Loan.builder()
                    .user(user)
                    .book(book)
                    .copy(copyForSeedLoan(book, seed.status()))
                    .loanDate(seed.loanDate().atStartOfDay())
                    .dueDate(seed.loanDate().atStartOfDay().plusDays(7))
                    .returnedAt(seed.returnDate() == null ? null : seed.returnDate().atStartOfDay())
                    .status(LoanStatus.valueOf(seed.status()))
                    .renewalCount(0)
                    .createdAt(seed.loanDate().atStartOfDay())
                    .build());
        }
    }

    private void ensureSeedCopy(Book book) {
        if (bookCopyRepository.countByBookId(book.getId()) > 0) {
            return;
        }
        bookCopyRepository.save(BookCopy.builder()
                .book(book)
                .copyCode("BOOK-" + book.getId() + "-COPY-1")
                .status("ACTIVE".equalsIgnoreCase(book.getStatus()) ? BookCopyStatus.AVAILABLE : BookCopyStatus.INACTIVE)
                .build());
    }

    private BookCopy copyForSeedLoan(Book book, String loanStatus) {
        BookCopy copy = bookCopyRepository.findAllByBookIdOrderByIdAsc(book.getId()).stream()
                .findFirst()
                .orElseGet(() -> bookCopyRepository.save(BookCopy.builder()
                        .book(book)
                        .copyCode("BOOK-" + book.getId() + "-COPY-1")
                        .status(BookCopyStatus.AVAILABLE)
                        .build()));
        copy.setStatus("RETURNED".equalsIgnoreCase(loanStatus) ? BookCopyStatus.AVAILABLE : BookCopyStatus.LOANED);
        return copy;
    }

    private void seedReviews(Map<String, Book> books, Map<String, User> users) {
        for (SeedReview seed : reviews()) {
            User user = users.get(seed.userKey());
            Book book = books.get(seed.bookKey());
            if (user == null || book == null || reviewRepository.existsByUserIdAndBookId(user.getId(), book.getId())) {
                continue;
            }

            LocalDateTime createdAt = seed.date().atStartOfDay();
            reviewRepository.save(Review.builder()
                    .user(user)
                    .book(book)
                    .rating(seed.rating())
                    .comment(seed.comment())
                    .status(seed.flagged() ? "HIDDEN" : "VISIBLE")
                    .createdAt(createdAt)
                    .updatedAt(createdAt)
                    .build());
        }
    }

    private List<SeedBook> books() {
        return List.of(
                new SeedBook("1", "Cien Años de Soledad", "Gabriel García Márquez", "978-0307474728", "Ficción",
                        "Español", "Editorial Sudamericana", LocalDate.parse("1967-05-30"), 417,
                        "Una obra maestra del realismo mágico que narra la historia de la familia Buendía a lo largo de siete generaciones en el pueblo ficticio de Macondo.",
                        "https://images.unsplash.com/photo-1544947950-fa07a98d237f?w=300&h=450&fit=crop", true),
                new SeedBook("2", "El Amor en los Tiempos del Cólera", "Gabriel García Márquez", "978-0307389732", "Romance",
                        "Español", "Oveja Negra", LocalDate.parse("1985-09-05"), 368,
                        "Una historia de amor que trasciende el tiempo, contada con la maestría característica de García Márquez.",
                        "https://images.unsplash.com/photo-1543002588-bfa74002ed7e?w=300&h=450&fit=crop", true),
                new SeedBook("3", "Don Quijote de la Mancha", "Miguel de Cervantes", "978-8420412146", "Clásicos",
                        "Español", "Real Academia Española", LocalDate.parse("1605-01-16"), 863,
                        "La obra cumbre de la literatura española que narra las aventuras del ingenioso hidalgo Don Quijote y su fiel escudero Sancho Panza.",
                        "https://images.unsplash.com/photo-1512820790803-83ca734da794?w=300&h=450&fit=crop", false),
                new SeedBook("4", "La Sombra del Viento", "Carlos Ruiz Zafón", "978-0143126393", "Misterio",
                        "Español", "Planeta", LocalDate.parse("2001-04-17"), 487,
                        "Un joven descubre un libro maldito que cambiará su vida para siempre en la Barcelona de posguerra.",
                        "https://images.unsplash.com/photo-1541963463532-d68292c34b19?w=300&h=450&fit=crop", true),
                new SeedBook("5", "Rayuela", "Julio Cortázar", "978-8420471778", "Ficción",
                        "Español", "Alfaguara", LocalDate.parse("1963-06-28"), 600,
                        "Una novela experimental que puede leerse de múltiples formas, explorando la vida de Horacio Oliveira entre París y Buenos Aires.",
                        "https://images.unsplash.com/photo-1495446815901-a7297e633e8d?w=300&h=450&fit=crop", true),
                new SeedBook("6", "La Casa de los Espíritus", "Isabel Allende", "978-1501117015", "Ficción",
                        "Español", "Plaza & Janés", LocalDate.parse("1982-10-01"), 433,
                        "La saga de la familia Trueba a través de cuatro generaciones en un país latinoamericano innominado.",
                        "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=300&h=450&fit=crop", true),
                new SeedBook("7", "Ficciones", "Jorge Luis Borges", "978-0802130303", "Cuentos",
                        "Español", "Emecé", LocalDate.parse("1944-01-01"), 174,
                        "Colección de cuentos que exploran temas como el tiempo, el infinito, los espejos y los laberintos.",
                        "https://images.unsplash.com/photo-1519682337058-a94d519337bc?w=300&h=450&fit=crop", false),
                new SeedBook("8", "Pedro Páramo", "Juan Rulfo", "978-0802133908", "Ficción",
                        "Español", "Fondo de Cultura Económica", LocalDate.parse("1955-03-19"), 124,
                        "Una novela corta que narra el regreso de Juan Preciado al pueblo fantasmal de Comala en busca de su padre.",
                        "https://images.unsplash.com/photo-1481627834876-b7833e8f5570?w=300&h=450&fit=crop", true),
                new SeedBook("9", "El Túnel", "Ernesto Sábato", "978-8432217036", "Ficción Psicológica",
                        "Español", "Seix Barral", LocalDate.parse("1948-01-01"), 158,
                        "La confesión de un pintor sobre el asesinato de la única mujer que creyó comprenderlo.",
                        "https://images.unsplash.com/photo-1497633762265-9d179a990aa6?w=300&h=450&fit=crop", true),
                new SeedBook("10", "Los Detectives Salvajes", "Roberto Bolaño", "978-0374191481", "Ficción",
                        "Español", "Anagrama", LocalDate.parse("1998-01-01"), 609,
                        "Una novela que sigue a un grupo de poetas en su búsqueda de una poetisa perdida en el desierto de Sonora.",
                        "https://images.unsplash.com/photo-1506880018603-83d5b814b5a6?w=300&h=450&fit=crop", true),
                new SeedBook("11", "Crónica de una Muerte Anunciada", "Gabriel García Márquez", "978-0307475084", "Ficción",
                        "Español", "Editorial Sudamericana", LocalDate.parse("1981-04-01"), 122,
                        "La reconstrucción de un asesinato anunciado que todo el pueblo conocía pero nadie pudo evitar.",
                        "https://images.unsplash.com/photo-1532012197267-da84d127e765?w=300&h=450&fit=crop", true),
                new SeedBook("12", "La Ciudad y los Perros", "Mario Vargas Llosa", "978-8420412580", "Ficción",
                        "Español", "Alfaguara", LocalDate.parse("1963-10-01"), 408,
                        "Una novela ambientada en un colegio militar de Lima que explora temas de violencia, corrupción y lealtad.",
                        "https://images.unsplash.com/photo-1524995997946-a1c2e315a42f?w=300&h=450&fit=crop", false));
    }

    private List<SeedUser> users() {
        return List.of(
                new SeedUser("u1", "Daniel Lasso", "daniel.lasso@lassriver.com", "USER", Gender.M, LocalDate.parse("1998-04-15")),
                new SeedUser("u2", "Ana Rivera", "ana.rivera@lassriver.com", "LIBRARIAN", Gender.F, LocalDate.parse("1997-08-21")),
                new SeedUser("u3", "Admin LassRiver", "admin@lassriver.com", "ADMIN", Gender.NR, LocalDate.parse("1990-01-01")),
                new SeedUser("u4", "Carlos Mendoza", "carlos.mendoza@lassriver.com", "USER", Gender.M, LocalDate.parse("1995-11-12")),
                new SeedUser("u5", "Maria Gonzalez", "maria.gonzalez@lassriver.com", "USER", Gender.F, LocalDate.parse("1996-03-09")));
    }

    private List<SeedLoan> loans() {
        return List.of(
                new SeedLoan("u1", "3", LocalDate.parse("2026-04-15"), null, "ACTIVE"),
                new SeedLoan("u1", "7", LocalDate.parse("2026-04-01"), null, "OVERDUE"),
                new SeedLoan("u2", "12", LocalDate.parse("2026-04-20"), null, "ACTIVE"),
                new SeedLoan("u1", "1", LocalDate.parse("2026-03-15"), LocalDate.parse("2026-04-14"), "RETURNED"),
                new SeedLoan("u2", "1", LocalDate.parse("2026-04-01"), LocalDate.parse("2026-04-12"), "RETURNED"),
                new SeedLoan("u3", "4", LocalDate.parse("2026-04-05"), LocalDate.parse("2026-04-20"), "RETURNED"),
                new SeedLoan("u5", "4", LocalDate.parse("2026-04-10"), LocalDate.parse("2026-04-24"), "RETURNED"));
    }

    private List<SeedReview> reviews() {
        return List.of(
                new SeedReview("u1", "1", 5, "Una obra maestra absoluta. La narrativa de García Márquez es hipnotizante y la historia de los Buendía es inolvidable.", LocalDate.parse("2026-04-15"), false),
                new SeedReview("u2", "1", 5, "El realismo mágico en su máxima expresión. Cada página es un descubrimiento.", LocalDate.parse("2026-04-20"), false),
                new SeedReview("u1", "2", 4, "Una historia de amor épica y conmovedora. García Márquez demuestra su maestría narrativa una vez más.", LocalDate.parse("2026-04-10"), false),
                new SeedReview("u5", "4", 5, "Zafón crea un universo literario fascinante en la Barcelona de posguerra. Absolutamente cautivador.", LocalDate.parse("2026-04-25"), false),
                new SeedReview("u1", "3", 5, "El clásico por excelencia de la literatura española. Una lectura obligatoria.", LocalDate.parse("2026-03-30"), false),
                new SeedReview("u4", "7", 3, "Contenido inapropiado que debería ser revisado.", LocalDate.parse("2026-04-28"), true));
    }

    private record SeedBook(String key, String title, String author, String isbn, String category, String language,
                            String publisher, LocalDate publishDate, Integer pages, String description,
                            String coverUrl, boolean available) {
    }

    private record SeedUser(String key, String name, String email, String role, Gender gender, LocalDate birthDate) {
    }

    private record SeedLoan(String userKey, String bookKey, LocalDate loanDate, LocalDate returnDate, String status) {
    }

    private record SeedReview(String userKey, String bookKey, Integer rating, String comment, LocalDate date,
                              boolean flagged) {
    }
}
