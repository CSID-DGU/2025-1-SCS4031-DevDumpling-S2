package com.example.entity;

import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalDateTime;
import java.time.LocalDate;

@Entity
@Table(name = "quiz")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Quiz {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "article_id")
    @JsonIgnoreProperties({"quizzes", "scrappedBy"})
    private Article article; // 기사

    @Enumerated(EnumType.STRING)
    private UserType userType; // 사용자 유형

    @Column(columnDefinition = "TEXT")
    private String question; // 퀴즈 질문

    @Column(columnDefinition = "TEXT")
    private String options; // JSON 형태로 저장 {"option1": "내용1", "option2": "내용2", "option3": "내용3", "option4": "내용4"}

    private String answer; // 정답 (1, 2, 3, 4 중 하나)

    @Column(columnDefinition = "TEXT")
    private String explanation; // 해설
}
