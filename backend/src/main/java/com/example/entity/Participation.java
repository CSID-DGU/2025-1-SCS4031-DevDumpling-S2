package com.example.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.time.LocalDate;

@Entity
@Table(name = "Participation")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Participation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long participationID;

    @ManyToOne
    @JoinColumn(name = "userID")
    private User user;

    @ManyToOne
    @JoinColumn(name = "challengeID")
    private Challenge challenge;

    private LocalDate joinDate;
    @Column(nullable = false)
    private float successRate;
    
    @Column(name = "user_rank", nullable = false)
    private Integer rank;

    @Column(nullable = false)
    private boolean isAbandoned = false;

    @Column
    private LocalDate abandonDate;

    private Double totalScore;  // 종합 점수 (0~100)
}
