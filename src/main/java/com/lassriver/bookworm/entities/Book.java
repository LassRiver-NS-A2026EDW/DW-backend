package com.lassriver.bookworm.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "books")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String author;

    @Column(nullable = false, unique = true, length = 20)
    private String isbn;

    @Column(length = 100)
    private String category;

    @Column(length = 10)
    private String language;

    @Column(length = 20)
    private String status; // "ACTIVE", "INACTIVE"

    @Column(name = "cover_url", columnDefinition = "TEXT")
    private String coverUrl;

    @Column(length = 255)
    private String publisher;

    @Column(name = "publish_date")
    private LocalDate publishDate;

    private Integer pages;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = "ACTIVE"; // Por defecto, el libro está activo al crearse
        }
    }
}
