package com.lassriver.bookworm.config;

import com.lassriver.bookworm.entities.Book;
import com.lassriver.bookworm.entities.Loan;
import com.lassriver.bookworm.entities.Review;
import com.lassriver.bookworm.entities.User;
import com.lassriver.bookworm.entities.enums.Gender;
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
                    .loanDate(seed.loanDate().atStartOfDay())
                    .returnedAt(seed.returnDate() == null ? null : seed.returnDate().atStartOfDay())
                    .status(seed.status())
                    .createdAt(seed.loanDate().atStartOfDay())
                    .build());
        }
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
                new SeedBook("1", "Cien Anos de Soledad", "Gabriel Garcia Marquez", "978-0307474728", "Ficcion",
                        "Espanol", "Editorial Sudamericana", LocalDate.parse("1967-05-30"), 417,
                        "Una obra maestra del realismo magico que narra la historia de la familia Buendia a lo largo de siete generaciones en el pueblo ficticio de Macondo.",
                        "https://images.unsplash.com/photo-1544947950-fa07a98d237f?w=300&h=450&fit=crop", true),
                new SeedBook("2", "El Amor en los Tiempos del Colera", "Gabriel Garcia Marquez", "978-0307389732", "Romance",
                        "Espanol", "Oveja Negra", LocalDate.parse("1985-09-05"), 368,
                        "Una historia de amor que trasciende el tiempo, contada con la maestria caracteristica de Garcia Marquez.",
                        "https://images.unsplash.com/photo-1543002588-bfa74002ed7e?w=300&h=450&fit=crop", true),
                new SeedBook("3", "Don Quijote de la Mancha", "Miguel de Cervantes", "978-8420412146", "Clasicos",
                        "Espanol", "Real Academia Espanola", LocalDate.parse("1605-01-16"), 863,
                        "La obra cumbre de la literatura espanola que narra las aventuras del ingenioso hidalgo Don Quijote y su fiel escudero Sancho Panza.",
                        "https://images.unsplash.com/photo-1512820790803-83ca734da794?w=300&h=450&fit=crop", false),
                new SeedBook("4", "La Sombra del Viento", "Carlos Ruiz Zafon", "978-0143126393", "Misterio",
                        "Espanol", "Planeta", LocalDate.parse("2001-04-17"), 487,
                        "Un joven descubre un libro maldito que cambiara su vida para siempre en la Barcelona de posguerra.",
                        "https://images.unsplash.com/photo-1541963463532-d68292c34b19?w=300&h=450&fit=crop", true),
                new SeedBook("5", "Rayuela", "Julio Cortazar", "978-8420471778", "Ficcion",
                        "Espanol", "Alfaguara", LocalDate.parse("1963-06-28"), 600,
                        "Una novela experimental que puede leerse de multiples formas, explorando la vida de Horacio Oliveira entre Paris y Buenos Aires.",
                        "https://images.unsplash.com/photo-1495446815901-a7297e633e8d?w=300&h=450&fit=crop", true),
                new SeedBook("6", "La Casa de los Espiritus", "Isabel Allende", "978-1501117015", "Ficcion",
                        "Espanol", "Plaza & Janes", LocalDate.parse("1982-10-01"), 433,
                        "La saga de la familia Trueba a traves de cuatro generaciones en un pais latinoamericano innominado.",
                        "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=300&h=450&fit=crop", true),
                new SeedBook("7", "Ficciones", "Jorge Luis Borges", "978-0802130303", "Cuentos",
                        "Espanol", "Emece", LocalDate.parse("1944-01-01"), 174,
                        "Coleccion de cuentos que exploran temas como el tiempo, el infinito, los espejos y los laberintos.",
                        "https://images.unsplash.com/photo-1519682337058-a94d519337bc?w=300&h=450&fit=crop", false),
                new SeedBook("8", "Pedro Paramo", "Juan Rulfo", "978-0802133908", "Ficcion",
                        "Espanol", "Fondo de Cultura Economica", LocalDate.parse("1955-03-19"), 124,
                        "Una novela corta que narra el regreso de Juan Preciado al pueblo fantasmal de Comala en busca de su padre.",
                        "https://images.unsplash.com/photo-1481627834876-b7833e8f5570?w=300&h=450&fit=crop", true),
                new SeedBook("9", "El Tunel", "Ernesto Sabato", "978-8432217036", "Ficcion Psicologica",
                        "Espanol", "Seix Barral", LocalDate.parse("1948-01-01"), 158,
                        "La confesion de un pintor sobre el asesinato de la unica mujer que creyo comprenderlo.",
                        "https://images.unsplash.com/photo-1497633762265-9d179a990aa6?w=300&h=450&fit=crop", true),
                new SeedBook("10", "Los Detectives Salvajes", "Roberto Bolano", "978-0374191481", "Ficcion",
                        "Espanol", "Anagrama", LocalDate.parse("1998-01-01"), 609,
                        "Una novela que sigue a un grupo de poetas en su busqueda de una poetisa perdida en el desierto de Sonora.",
                        "https://images.unsplash.com/photo-1506880018603-83d5b814b5a6?w=300&h=450&fit=crop", true),
                new SeedBook("11", "Cronica de una Muerte Anunciada", "Gabriel Garcia Marquez", "978-0307475084", "Ficcion",
                        "Espanol", "Editorial Sudamericana", LocalDate.parse("1981-04-01"), 122,
                        "La reconstruccion de un asesinato anunciado que todo el pueblo conocia pero nadie pudo evitar.",
                        "https://images.unsplash.com/photo-1532012197267-da84d127e765?w=300&h=450&fit=crop", true),
                new SeedBook("12", "La Ciudad y los Perros", "Mario Vargas Llosa", "978-8420412580", "Ficcion",
                        "Espanol", "Alfaguara", LocalDate.parse("1963-10-01"), 408,
                        "Una novela ambientada en un colegio militar de Lima que explora temas de violencia, corrupcion y lealtad.",
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
                new SeedReview("u1", "1", 5, "Una obra maestra absoluta. La narrativa de Garcia Marquez es hipnotizante y la historia de los Buendia es inolvidable.", LocalDate.parse("2026-04-15"), false),
                new SeedReview("u2", "1", 5, "El realismo magico en su maxima expresion. Cada pagina es un descubrimiento.", LocalDate.parse("2026-04-20"), false),
                new SeedReview("u1", "2", 4, "Una historia de amor epica y conmovedora. Garcia Marquez demuestra su maestria narrativa una vez mas.", LocalDate.parse("2026-04-10"), false),
                new SeedReview("u5", "4", 5, "Zafon crea un universo literario fascinante en la Barcelona de posguerra. Absolutamente cautivador.", LocalDate.parse("2026-04-25"), false),
                new SeedReview("u1", "3", 5, "El clasico por excelencia de la literatura espanola. Una lectura obligatoria.", LocalDate.parse("2026-03-30"), false),
                new SeedReview("u4", "7", 3, "Contenido inapropiado que deberia ser revisado.", LocalDate.parse("2026-04-28"), true));
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
