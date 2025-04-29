package com.example.entity.challenge;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ChallengeCategory {
    NEW_DISCOUNT("새 할인", "challenge/icons/new-discount.png", AnalysisType.DISCOUNT_ANALYSIS),
    FOOD("식비", "challenge/icons/food.png", AnalysisType.EXPENSE_ANALYSIS),
    CAFE_SNACK("카페/간식", "challenge/icons/cafe.png", AnalysisType.EXPENSE_ANALYSIS),
    SAVINGS("저축", "challenge/icons/savings.png", AnalysisType.SAVINGS_ANALYSIS),
    ALCOHOL_ENTERTAINMENT("술/유흥", "challenge/icons/entertainment.png", AnalysisType.EXPENSE_ANALYSIS),
    SHOPPING("쇼핑", "challenge/icons/shopping.png", AnalysisType.EXPENSE_ANALYSIS),
    BEAUTY("미용", "challenge/icons/beauty.png", AnalysisType.EXPENSE_ANALYSIS),
    TRAVEL("여행", "challenge/icons/travel.png", AnalysisType.EXPENSE_ANALYSIS),
    PET("반려동물", "challenge/icons/pet.png", AnalysisType.EXPENSE_ANALYSIS),
    MART_CONVENIENCE("마트/편의점", "challenge/icons/mart.png", AnalysisType.EXPENSE_ANALYSIS),
    GAME_OTT("게임/OTT", "challenge/icons/game-ott.png", AnalysisType.SUBSCRIPTION_ANALYSIS),
    HOUSING_COMMUNICATION("주거/통신", "challenge/icons/housing.png", AnalysisType.FIXED_EXPENSE_ANALYSIS),
    TRANSPORTATION("교통", "challenge/icons/transport.png", AnalysisType.EXPENSE_ANALYSIS),
    HEALTH_EXERCISE("건강/운동", "challenge/icons/health.png", AnalysisType.SUBSCRIPTION_ANALYSIS);

    private final String displayName;
    private final String s3IconPath;
    private final AnalysisType analysisType;

    public String getIconUrl(String cdnDomain) {
        return cdnDomain + "/" + s3IconPath;
    }
} 