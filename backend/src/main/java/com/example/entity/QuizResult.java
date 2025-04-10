package com.example.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.time.LocalDate;

@Entity
@Table(name = "QuizResult")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuizResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long resultID;

    @ManyToOne
    @JoinColumn(name = "quizID")
    private Quiz quiz;

    @ManyToOne
    @JoinColumn(name = "userID")
    private User user;

    private String selectedAnswer;

    private Boolean isCorrect;

    private LocalDateTime submittedAt;
}
