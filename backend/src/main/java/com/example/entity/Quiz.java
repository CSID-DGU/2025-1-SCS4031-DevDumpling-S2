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
    private Article article; // 기사

    @Enumerated(EnumType.STRING)
    private UserType userType; // 사용자 유형

    @Column(columnDefinition = "TEXT")
    private String question; // 퀴즈 질문
    
    @Column(length = 20)
    private String type; // 퀴즈 유형
    private String options; // 객관식 보기: "보기1|보기2|보기3|보기4" 형태
    private String answer; // 정답

    @Column(columnDefinition = "TEXT")
    private String explanation; // 해설
}
