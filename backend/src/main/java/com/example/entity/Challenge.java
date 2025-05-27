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

    @Column(nullable = false)
    @Builder.Default
    private Integer maxParticipants = 100; // 기본값 100명으로 설정
    
    @Enumerated(EnumType.STRING)
    private ChallengeType type;
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ChallengeCategory category;
    
    private String inviteCode;

    @Column(nullable = false)
    @Builder.Default
    private boolean isDeleted = false;

    @Column(nullable = false)
    @Builder.Default
    private boolean isCompleted = false;

    @Column
    private LocalDate completionDate;

    // Fields for Savings Challenge
    private Long targetSavingsAmount; // 목표 저축 금액

    @Enumerated(EnumType.STRING)
    private SavingsGoalType savingsGoalType; // 저축 목표 유형

    // Fields for Food Challenge
    @Enumerated(EnumType.STRING)
    private FoodChallengeGoalType foodGoalType; // 식비 챌린지 목표 유형

    private Long foodTargetAmount; // 식비 목표 금액 (e.g., 총 식비 OOO원 이하)

    private Integer foodTargetCount; // 식비 목표 횟수 (e.g., 배달/외식 O회 이하)

    private Integer foodTargetRatio; // 식비 목표 비율 (e.g., 집밥 OO% 이상)

    // Fields for Cafe/Snack Challenge
    @Enumerated(EnumType.STRING)
    private CafeSnackChallengeGoalType cafeSnackGoalType; // 카페/간식 챌린지 목표 유형

    private Long cafeSnackTargetAmount; // 총 카페/간식 목표 금액

    private Long cafeSnackTargetDailyAmount; // 하루 카페/간식 목표 금액

    private Integer cafeSnackTargetWeeklyCount; // 일주일 카페/간식 목표 횟수

    @Column(nullable = false)
    @Builder.Default
    private Integer likeCount = 0; // 좋아요 수

    public enum ChallengeType {
        PUBLIC, PRIVATE
    }

    public boolean isPublic() {
        return type == ChallengeType.PUBLIC;
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

    // Enum for Savings Challenge Goal Type
    public enum SavingsGoalType {
        TOTAL_AMOUNT_IN_PERIOD, // 챌린지 기간 내 총액 달성
        MONTHLY_TARGET          // 매월 목표액 달성
    }

    // Enum for Food Challenge Goal Type
    public enum FoodChallengeGoalType {
        TOTAL_FOOD_EXPENSE_LIMIT,   // 총 식비 OOO원 이하 사용
        DELIVERY_COUNT_LIMIT,       // 배달 음식 O회 이하 주문
        EATING_OUT_COUNT_LIMIT,     // 외식 O회 이하
        HOME_COOKED_MEAL_RATIO      // 집밥 비율 OO% 이상 달성
        // TODO: Add other possible goal types if needed
    }

    // Enum for Cafe/Snack Challenge Goal Type
    public enum CafeSnackChallengeGoalType {
        TOTAL_CAFE_SNACK_EXPENSE_LIMIT,    // 총 카페/간식 비용 OOO원 이하 사용
        DAILY_CAFE_SNACK_EXPENSE_LIMIT,    // 하루 카페/간식 비용 OOO원 이하 사용
        WEEKLY_CAFE_SNACK_COUNT_LIMIT      // 일주일 카페/간식 O회 이하 이용
        // TODO: Add other possible goal types if needed
    }
}
