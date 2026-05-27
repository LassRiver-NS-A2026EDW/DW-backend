package com.lassriver.bookworm.services.impl;

import com.lassriver.bookworm.dtos.request.BookUpsertRequest;
import com.lassriver.bookworm.dtos.response.BookResponse;
import com.lassriver.bookworm.entities.Book;
import com.lassriver.bookworm.exceptions.ResourceNotFoundException;
import com.lassriver.bookworm.repositories.BookCopyRepository;
import com.lassriver.bookworm.repositories.BookRepository;
import com.lassriver.bookworm.repositories.LoanRepository;
import com.lassriver.bookworm.repositories.ReservationRepository;
import com.lassriver.bookworm.repositories.ReviewRepository;
import com.lassriver.bookworm.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceImplTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private LoanRepository loanRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookCopyRepository bookCopyRepository;

    @Mock
    private ReservationRepository reservationRepository;

    @InjectMocks
    private BookServiceImpl bookService;

    @Test
    void getBooks_WithFilters_ReturnsPagedResponse() {
        Book book = Book.builder().id(1L).title("Clean Code").author("Robert C. Martin").isbn("123").build();
        Page<Book> page = new PageImpl<>(List.of(book), PageRequest.of(0, 10), 1);

        when(bookRepository.findAll(any(Specification.class), any(PageRequest.class))).thenReturn(page);

        Page<BookResponse> response = bookService.getBooks("clean", null, "software", null, null, PageRequest.of(0, 10));

        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
        assertEquals("Clean Code", response.getContent().getFirst().getTitle());
    }

    @Test
    void createBook_HappyPath_ReturnsBookResponse() {
        BookUpsertRequest request = new BookUpsertRequest();
        request.setTitle("Domain-Driven Design");
        request.setAuthor("Eric Evans");
        request.setIsbn("9780321125217");

        Book saved = Book.builder()
                .id(10L)
                .title(request.getTitle())
                .author(request.getAuthor())
                .isbn(request.getIsbn())
                .status("ACTIVE")
                .build();

        when(bookRepository.save(any(Book.class))).thenReturn(saved);

        BookResponse response = bookService.createBook(request);

        assertEquals(10L, response.getId());
        assertEquals("Domain-Driven Design", response.getTitle());
        verify(bookRepository, times(1)).save(any(Book.class));
    }

    @Test
    void updateBook_NotFound_ThrowsResourceNotFoundException() {
        when(bookRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> bookService.updateBook(99L, new BookUpsertRequest()));

        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    void updateBookStatus_HappyPath_ChangesStatusToInactive() {
        Book book = Book.builder().id(5L).title("Book").status("ACTIVE").build();
        Book updated = Book.builder().id(5L).title("Book").status("INACTIVE").build();

        when(bookRepository.findById(5L)).thenReturn(Optional.of(book));
        when(bookRepository.save(eq(book))).thenReturn(updated);

        BookResponse response = bookService.updateBookStatus(5L, "INACTIVE");

        assertEquals("INACTIVE", response.getStatus());
        verify(bookRepository, times(1)).save(book);
    }
}
