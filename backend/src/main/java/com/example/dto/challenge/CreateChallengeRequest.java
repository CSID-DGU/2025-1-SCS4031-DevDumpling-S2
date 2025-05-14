package com.example.dto.challenge;

import com.example.entity.Challenge.ChallengeType;
import com.example.entity.Challenge.ChallengeCategory;
import lombok.Data;
import java.time.LocalDate;
import com.example.entity.Challenge.SavingsGoalType;
import com.example.entity.Challenge.FoodChallengeGoalType;
import com.example.entity.Challenge.CafeSnackChallengeGoalType;

@Data
public class CreateChallengeRequest {
    private String title;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer maxParticipants;
    private ChallengeType type;
    private ChallengeCategory category;

    // Fields for Savings Challenge
    private Long targetSavingsAmount;
    private SavingsGoalType savingsGoalType;

    // Fields for Food Challenge
    private FoodChallengeGoalType foodGoalType;
    private Long foodTargetAmount;
    private Integer foodTargetCount;
    private Integer foodTargetRatio;

    // Fields for Cafe/Snack Challenge
    private CafeSnackChallengeGoalType cafeSnackGoalType;
    private Long cafeSnackTargetAmount;
    private Long cafeSnackTargetDailyAmount;
    private Integer cafeSnackTargetWeeklyCount;
} 