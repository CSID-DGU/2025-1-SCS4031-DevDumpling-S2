package com.example.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Article")
public class Article {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Lob
    @Column(columnDefinition = "LONGTEXT", nullable = false)
    private String content;

    @Column(nullable = false)
    private String sourceUrl;

    @Column(nullable = false)
    private LocalDateTime publishDate;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String summary;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String explanation;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String termExplanations; // JSON 형태로 저장: [{"term": "용어", "explanation": "설명"}, ...]

    @Enumerated(EnumType.STRING)
    private ProcessingStatus status;

    public enum ProcessingStatus {
        PENDING,    // Gemini API 처리 전
        COMPLETED,  // 모든 처리가 완료됨
        FAILED      // 처리 실패
    }
}