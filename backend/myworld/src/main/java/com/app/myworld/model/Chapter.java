package com.app.myworld.model;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
    name = "chapters",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_chapter_book_number", columnNames = {"book_id", "number"})
    }
)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Chapter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private Integer number;

    @Lob
    @Column(nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy= "chapter", cascade= CascadeType.REMOVE, orphanRemoval= true)
    private Set<Comment> comments = new HashSet<>();

    @OneToMany(mappedBy = "chapter", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ChapterRead> reads = new HashSet<>();

    @Column(name = "like_count", nullable = false)
    private Integer likeCount;

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        if (this.likeCount == null) {
            this.likeCount = 0;
        }
    }

    public void addComment(Comment comment) {
        Objects.requireNonNull(comment);
        if (comment.getChapter() != this) {
            comment.setChapter(this);
        }
        if (!comments.contains(comment)) {
            comments.add(comment);
        }
    }

    public void removeComment(Comment comment) {
        if (comments.remove(comment)) {
            comment.setChapter(null);
        }
    }
}
