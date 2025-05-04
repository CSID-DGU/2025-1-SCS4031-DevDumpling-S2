package com.example.entity.challenge;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ChallengeCategory {
    NEW_DISCOUNT("도서교육", AnalysisType.DISCOUNT_ANALYSIS),
    FOOD("식비", AnalysisType.EXPENSE_ANALYSIS),
    CAFE_SNACK("카페간식", AnalysisType.EXPENSE_ANALYSIS),
    SAVINGS("저축", AnalysisType.SAVINGS_ANALYSIS),
    ALCOHOL_ENTERTAINMENT("술유흥", AnalysisType.EXPENSE_ANALYSIS),
    SHOPPING("쇼핑", AnalysisType.EXPENSE_ANALYSIS),
    BEAUTY("미용", AnalysisType.EXPENSE_ANALYSIS),
    TRAVEL("여행", AnalysisType.EXPENSE_ANALYSIS),
    PET("반려동물", AnalysisType.EXPENSE_ANALYSIS),
    MART_CONVENIENCE("편의점마트잡화", AnalysisType.EXPENSE_ANALYSIS),
    GAME_OTT("게임", AnalysisType.SUBSCRIPTION_ANALYSIS),
    HOUSING_COMMUNICATION("주거통신", AnalysisType.FIXED_EXPENSE_ANALYSIS),
    TRANSPORTATION("교통", AnalysisType.EXPENSE_ANALYSIS),
    HEALTH_EXERCISE("의료건강피트니스", AnalysisType.SUBSCRIPTION_ANALYSIS);

    private final String displayName;
    private final AnalysisType analysisType;
} 