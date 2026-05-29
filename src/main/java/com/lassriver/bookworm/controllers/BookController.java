package com.lassriver.bookworm.controllers;

import com.lassriver.bookworm.dtos.request.BookUpsertRequest;
import com.lassriver.bookworm.dtos.request.PdfDownloadRequest;
import com.lassriver.bookworm.dtos.response.BookAvailabilityResponse;
import com.lassriver.bookworm.dtos.response.BookCopyResponse;
import com.lassriver.bookworm.dtos.response.BookFacetsResponse;
import com.lassriver.bookworm.dtos.response.BookPdfResource;
import com.lassriver.bookworm.dtos.response.BookPdfResponse;
import com.lassriver.bookworm.dtos.response.BookResponse;
import com.lassriver.bookworm.dtos.response.PageResponse;
import com.lassriver.bookworm.services.BookCopyService;
import com.lassriver.bookworm.services.BookPdfService;
import com.lassriver.bookworm.services.BookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.CacheControl;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.core.io.Resource;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;
    private final BookPdfService bookPdfService;
    private final BookCopyService bookCopyService;

    @GetMapping
    public ResponseEntity<PageResponse<BookResponse>> getBooks(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String language,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String availability,
            Pageable pageable,
            @AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails == null ? null : userDetails.getUsername();
        return ResponseEntity.ok(PageResponse.from(bookService.getBooks(search, title, category, language, status, availability, pageable, email)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookResponse> getBook(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails == null ? null : userDetails.getUsername();
        return ResponseEntity.ok(bookService.getBook(id, email));
    }

    @GetMapping("/facets")
    public ResponseEntity<BookFacetsResponse> getBookFacets() {
        return ResponseEntity.ok(bookService.getBookFacets());
    }

    @GetMapping("/{id}/availability")
    public ResponseEntity<BookAvailabilityResponse> getAvailability(@PathVariable Long id) {
        return ResponseEntity.ok(bookCopyService.getAvailability(id));
    }

    @GetMapping("/{id}/copies")
    public ResponseEntity<java.util.List<BookCopyResponse>> getCopies(@PathVariable Long id) {
        return ResponseEntity.ok(bookCopyService.getCopies(id));
    }

    @PostMapping("/{id}/copies")
    public ResponseEntity<BookCopyResponse> createCopy(@PathVariable Long id) {
        return new ResponseEntity<>(bookCopyService.createCopy(id), HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}/copies/{copyId}")
    public ResponseEntity<BookCopyResponse> retireCopy(@PathVariable Long id, @PathVariable Long copyId) {
        return ResponseEntity.ok(bookCopyService.retireCopy(id, copyId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping
    public ResponseEntity<BookResponse> createBook(@Valid @RequestBody BookUpsertRequest request) {
        BookResponse response = bookService.createBook(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BookResponse> updateBook(@PathVariable Long id, @Valid @RequestBody BookUpsertRequest request) {
        return ResponseEntity.ok(bookService.updateBook(id, request));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<BookResponse> updateBookStatus(
            @PathVariable Long id,
            @RequestParam(defaultValue = "INACTIVE") String status) {
        return ResponseEntity.ok(bookService.updateBookStatus(id, status));
    }

    @PostMapping(value = "/{id}/pdf/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<BookPdfResponse> uploadPdf(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(bookPdfService.uploadPdf(id, file));
    }

    @PostMapping("/{id}/pdf/download")
    public ResponseEntity<BookPdfResponse> downloadPdf(
            @PathVariable Long id,
            @Valid @RequestBody PdfDownloadRequest request) {
        return ResponseEntity.ok(bookPdfService.downloadPdfFromUrl(id, request.getUrl()));
    }

    @GetMapping(value = "/{id}/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<Resource> servePdf(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        BookPdfResource pdf = bookPdfService.loadPdfForReading(id, userDetails.getUsername());
        return ResponseEntity.ok()
                .cacheControl(CacheControl.maxAge(0, TimeUnit.SECONDS).cachePrivate().mustRevalidate())
                .header("X-Accel-Buffering", "no")
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.inline()
                        .filename(pdf.getFilename())
                        .build()
                        .toString())
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf.getResource());
    }
}
