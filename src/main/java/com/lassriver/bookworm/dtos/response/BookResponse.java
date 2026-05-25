package com.lassriver.bookworm.dtos.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class BookResponse {
    private Long id;
    private String title;
    private String author;
    private String isbn;
    private String category;
    private String language;
    private String status;
    private String coverUrl;
    private String publisher;
    private LocalDate publishDate;
    private Integer pages;
    private String description;
    private Double rating;
    private Long reviewCount;
    private Boolean hasPdf;
    private String pdfUrl;
    private Boolean reservedByMe;
    private LocalDateTime createdAt;
}
