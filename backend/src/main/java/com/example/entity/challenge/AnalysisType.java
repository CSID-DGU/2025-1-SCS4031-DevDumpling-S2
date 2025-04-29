package com.example.entity.challenge;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AnalysisType {
    EXPENSE_ANALYSIS("일반 지출 분석"),
    SAVINGS_ANALYSIS("저축 분석"),
    DISCOUNT_ANALYSIS("할인 분석"),
    SUBSCRIPTION_ANALYSIS("구독 서비스 분석"),
    FIXED_EXPENSE_ANALYSIS("고정 지출 분석");

    private final String description;
} 