package com.example.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.time.LocalDate;

@Entity
@Table(name = "Quiz")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Quiz {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "article_id")
    private Article article; // 기사

    @Enumerated(EnumType.STRING)
    private UserType userType; // 사용자 유형

    @Column(columnDefinition = "TEXT")
    private String question; // 퀴즈 질문
    
    @Column(length = 20)
    private String quizType; // "OX" 또는 "MULTIPLE_CHOICE"

    @Column(columnDefinition = "TEXT")
    private String options; // 4지선다의 경우 JSON 형태로 저장

    private String answer; // 정답

    @Column(columnDefinition = "TEXT")
    private String explanation; // 해설
}
