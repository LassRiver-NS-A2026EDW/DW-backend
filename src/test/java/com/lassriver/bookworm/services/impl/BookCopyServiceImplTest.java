package com.lassriver.bookworm.services.impl;

import com.lassriver.bookworm.dtos.response.BookCopyResponse;
import com.lassriver.bookworm.entities.Book;
import com.lassriver.bookworm.entities.BookCopy;
import com.lassriver.bookworm.entities.enums.BookCopyStatus;
import com.lassriver.bookworm.entities.enums.ReservationStatus;
import com.lassriver.bookworm.exceptions.BusinessRuleException;
import com.lassriver.bookworm.repositories.BookCopyRepository;
import com.lassriver.bookworm.repositories.BookRepository;
import com.lassriver.bookworm.repositories.ReservationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookCopyServiceImplTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BookCopyRepository bookCopyRepository;

    @Mock
    private ReservationRepository reservationRepository;

    @InjectMocks
    private BookCopyServiceImpl bookCopyService;

    @Test
    void retireCopy_WhenAvailableAndNoQueue_RetiresCopy() {
        Book book = book();
        BookCopy copy = copy(book, BookCopyStatus.AVAILABLE);
        when(bookRepository.findLockedById(1L)).thenReturn(Optional.of(book));
        when(bookCopyRepository.findByIdAndBookId(10L, 1L)).thenReturn(Optional.of(copy));
        when(reservationRepository.existsByBookIdAndStatus(1L, ReservationStatus.WAITING)).thenReturn(false);
        when(bookCopyRepository.countByBookIdAndStatusIn(eq(1L), anyCollection())).thenReturn(2L);
        when(bookCopyRepository.save(copy)).thenReturn(copy);

        BookCopyResponse response = bookCopyService.retireCopy(1L, 10L);

        assertEquals("INACTIVE", response.getStatus());
        assertEquals(BookCopyStatus.INACTIVE, copy.getStatus());
        verify(bookCopyRepository).save(copy);
    }

    @Test
    void retireCopy_WhenWaitingReservationsExist_ThrowsBusinessRuleException() {
        Book book = book();
        BookCopy copy = copy(book, BookCopyStatus.AVAILABLE);
        when(bookRepository.findLockedById(1L)).thenReturn(Optional.of(book));
        when(bookCopyRepository.findByIdAndBookId(10L, 1L)).thenReturn(Optional.of(copy));
        when(reservationRepository.existsByBookIdAndStatus(1L, ReservationStatus.WAITING)).thenReturn(true);

        assertThrows(BusinessRuleException.class, () -> bookCopyService.retireCopy(1L, 10L));

        verify(bookCopyRepository, never()).save(copy);
    }

    @Test
    void retireCopy_WhenCopyIsNotAvailable_ThrowsBusinessRuleException() {
        Book book = book();
        BookCopy copy = copy(book, BookCopyStatus.LOANED);
        when(bookRepository.findLockedById(1L)).thenReturn(Optional.of(book));
        when(bookCopyRepository.findByIdAndBookId(10L, 1L)).thenReturn(Optional.of(copy));
        when(reservationRepository.existsByBookIdAndStatus(1L, ReservationStatus.WAITING)).thenReturn(false);

        assertThrows(BusinessRuleException.class, () -> bookCopyService.retireCopy(1L, 10L));

        verify(bookCopyRepository, never()).save(copy);
    }

    @Test
    void retireCopy_WhenLastCountedCopy_ThrowsBusinessRuleException() {
        Book book = book();
        BookCopy copy = copy(book, BookCopyStatus.AVAILABLE);
        when(bookRepository.findLockedById(1L)).thenReturn(Optional.of(book));
        when(bookCopyRepository.findByIdAndBookId(10L, 1L)).thenReturn(Optional.of(copy));
        when(reservationRepository.existsByBookIdAndStatus(1L, ReservationStatus.WAITING)).thenReturn(false);
        when(bookCopyRepository.countByBookIdAndStatusIn(eq(1L), anyCollection())).thenReturn(1L);

        assertThrows(BusinessRuleException.class, () -> bookCopyService.retireCopy(1L, 10L));

        verify(bookCopyRepository, never()).save(copy);
    }

    private Book book() {
        return Book.builder()
                .id(1L)
                .title("Clean Architecture")
                .author("Robert Martin")
                .isbn("ISBN-001")
                .status("ACTIVE")
                .build();
    }

    private BookCopy copy(Book book, BookCopyStatus status) {
        return BookCopy.builder()
                .id(10L)
                .book(book)
                .copyCode("BOOK-1-COPY-1")
                .status(status)
                .build();
    }
}
