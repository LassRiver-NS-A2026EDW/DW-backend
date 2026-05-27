package com.lassriver.bookworm.services.impl;

import com.lassriver.bookworm.dtos.response.BookAvailabilityResponse;
import com.lassriver.bookworm.dtos.response.BookCopyResponse;
import com.lassriver.bookworm.entities.Book;
import com.lassriver.bookworm.entities.BookCopy;
import com.lassriver.bookworm.entities.enums.BookCopyStatus;
import com.lassriver.bookworm.entities.enums.ReservationStatus;
import com.lassriver.bookworm.exceptions.BusinessRuleException;
import com.lassriver.bookworm.exceptions.ResourceNotFoundException;
import com.lassriver.bookworm.repositories.BookCopyRepository;
import com.lassriver.bookworm.repositories.BookRepository;
import com.lassriver.bookworm.repositories.ReservationRepository;
import com.lassriver.bookworm.services.BookCopyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookCopyServiceImpl implements BookCopyService {

    private final BookRepository bookRepository;
    private final BookCopyRepository bookCopyRepository;
    private final ReservationRepository reservationRepository;

    @Override
    @Transactional(readOnly = true)
    public BookAvailabilityResponse getAvailability(Long bookId) {
        if (!bookRepository.existsById(bookId)) {
            throw new ResourceNotFoundException("Libro no encontrado con id: " + bookId);
        }
        return availabilityFor(bookId);
    }

    @Override
    @Transactional
    public BookCopyResponse createCopy(Long bookId) {
        Book book = bookRepository.findLockedById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Libro no encontrado con id: " + bookId));
        if (!"ACTIVE".equalsIgnoreCase(book.getStatus())) {
            throw new BusinessRuleException("No se pueden agregar ejemplares a un libro inactivo.");
        }

        long nextCopyNumber = bookCopyRepository.countByBookId(book.getId()) + 1;
        BookCopy copy = BookCopy.builder()
                .book(book)
                .copyCode("BOOK-" + book.getId() + "-COPY-" + nextCopyNumber)
                .status(BookCopyStatus.AVAILABLE)
                .build();

        return toResponse(bookCopyRepository.save(copy));
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookCopyResponse> getCopies(Long bookId) {
        if (!bookRepository.existsById(bookId)) {
            throw new ResourceNotFoundException("Libro no encontrado con id: " + bookId);
        }
        return bookCopyRepository.findAllByBookIdOrderByIdAsc(bookId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private BookAvailabilityResponse availabilityFor(Long bookId) {
        return BookAvailabilityResponse.builder()
                .bookId(bookId)
                .totalCopies(bookCopyRepository.countByBookId(bookId))
                .availableCopies(bookCopyRepository.countByBookIdAndStatus(bookId, BookCopyStatus.AVAILABLE))
                .loanedCopies(bookCopyRepository.countByBookIdAndStatus(bookId, BookCopyStatus.LOANED))
                .reservedCopies(bookCopyRepository.countByBookIdAndStatus(bookId, BookCopyStatus.RESERVED))
                .waitingReservations(reservationRepository.countByBookIdAndStatus(bookId, ReservationStatus.WAITING))
                .build();
    }

    private BookCopyResponse toResponse(BookCopy copy) {
        return BookCopyResponse.builder()
                .id(copy.getId())
                .bookId(copy.getBook().getId())
                .bookTitle(copy.getBook().getTitle())
                .copyCode(copy.getCopyCode())
                .status(copy.getStatus().name())
                .createdAt(copy.getCreatedAt())
                .build();
    }
}
