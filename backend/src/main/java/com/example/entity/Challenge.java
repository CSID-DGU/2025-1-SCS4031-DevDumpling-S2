package com.example.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import com.example.entity.challenge.AnalysisType;

@Entity
@Table(name = "Challenge")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Challenge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long challengeID;

    @ManyToOne
    @JoinColumn(name = "userID")
    private User user;

    private String title;

    @Lob
    private String description;

    private LocalDate startDate;
    private LocalDate endDate;

    private Integer maxParticipants;
    
    @Enumerated(EnumType.STRING)
    private ChallengeType type;
    
    @Column(nullable = false)
    private ChallengeCategory category;
    
    private String inviteCode;

    @Column(nullable = false)
    private boolean isDeleted = false;

    @Column(nullable = false)
    private boolean isCompleted = false;

    @Column
    private LocalDate completionDate;

    public enum ChallengeType {
        PUBLIC, PRIVATE
    }

    public enum ChallengeCategory {
        NEW_DISCOUNT(AnalysisType.DISCOUNT_ANALYSIS),
        FOOD(AnalysisType.EXPENSE_ANALYSIS),
        CAFE_SNACK(AnalysisType.EXPENSE_ANALYSIS),
        SAVINGS(AnalysisType.SAVINGS_ANALYSIS),
        ALCOHOL_ENTERTAINMENT(AnalysisType.EXPENSE_ANALYSIS),
        SHOPPING(AnalysisType.EXPENSE_ANALYSIS),
        BEAUTY(AnalysisType.EXPENSE_ANALYSIS),
        TRAVEL(AnalysisType.EXPENSE_ANALYSIS),
        PET(AnalysisType.EXPENSE_ANALYSIS),
        MART_CONVENIENCE(AnalysisType.EXPENSE_ANALYSIS),
        GAME_OTT(AnalysisType.SUBSCRIPTION_ANALYSIS),
        HOUSING_COMMUNICATION(AnalysisType.FIXED_EXPENSE_ANALYSIS),
        TRANSPORTATION(AnalysisType.EXPENSE_ANALYSIS),
        HEALTH_EXERCISE(AnalysisType.SUBSCRIPTION_ANALYSIS);

        private final AnalysisType analysisType;

        ChallengeCategory(AnalysisType analysisType) {
            this.analysisType = analysisType;
        }

        public AnalysisType getAnalysisType() {
            return analysisType;
        }
    }
}
