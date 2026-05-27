package com.lassriver.bookworm.entities;

import com.lassriver.bookworm.entities.enums.BookCopyStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "book_copies")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookCopy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @Column(name = "copy_code", nullable = false, unique = true, length = 60)
    private String copyCode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private BookCopyStatus status;

    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        if (this.status == null) {
            this.status = BookCopyStatus.AVAILABLE;
        }
    }
}
