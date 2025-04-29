package com.example.service.analysis;

import com.example.entity.Challenge;
import com.example.entity.Challenge.ChallengeCategory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ExpenseAnalyzer implements ChallengeAnalyzer {
    @Override
    public void analyze(Challenge challenge) {
        log.info("[일반 지출 분석] 챌린지 ID: {}, 카테고리: {}", 
            challenge.getChallengeID(), 
            challenge.getCategory());

        ChallengeCategory category = challenge.getCategory();
        
        switch (category) {
            case FOOD:
                analyzeFood(challenge);
                break;
            case CAFE_SNACK:
                analyzeCafeSnack(challenge);
                break;
            case TRAVEL:
                analyzeTravel(challenge);
                break;
            case PET:
                analyzePet(challenge);
                break;
            case MART_CONVENIENCE:
                analyzeMartConvenience(challenge);
                break;
            case TRANSPORTATION:
                analyzeTransportation(challenge);
                break;
            case SHOPPING:
                analyzeShopping(challenge);
                break;
            case BEAUTY:
                analyzeBeauty(challenge);
                break;
        }
    }

    private void analyzeFood(Challenge challenge) {
        log.info("[식비 분석] 시작 - 챌린지 ID: {}", challenge.getChallengeID());
        // TODO: 식비 분석 로직 구현
        // 1. 일일 평균 식비 계산
        // 2. 식사 시간대별 지출 패턴 분석
        // 3. 배달 vs 외식 비율 분석
    }

    private void analyzeCafeSnack(Challenge challenge) {
        log.info("[카페/간식 분석] 시작 - 챌린지 ID: {}", challenge.getChallengeID());
        // TODO: 카페/간식 분석 로직 구현
        // 1. 카페 방문 빈도 분석
        // 2. 선호 메뉴 패턴 분석
        // 3. 시간대별 소비 패턴 분석
    }

    private void analyzeTravel(Challenge challenge) {
        log.info("[여행 지출 분석] 시작 - 챌린지 ID: {}", challenge.getChallengeID());
        // TODO: 여행 지출 분석 로직 구현
        // 1. 여행 기간별 지출 분석
        // 2. 교통/숙박/식비/기타 비용 분류
        // 3. 지역별 지출 패턴 분석
    }

    private void analyzePet(Challenge challenge) {
        log.info("[반려동물 지출 분석] 시작 - 챌린지 ID: {}", challenge.getChallengeID());
        // TODO: 반려동물 지출 분석 로직 구현
        // 1. 사료/간식 지출 분석
        // 2. 의료비 지출 분석
        // 3. 용품 구매 패턴 분석
    }

    private void analyzeMartConvenience(Challenge challenge) {
        log.info("[마트/편의점 분석] 시작 - 챌린지 ID: {}", challenge.getChallengeID());
        // TODO: 마트/편의점 지출 분석 로직 구현
        // 1. 방문 빈도 분석
        // 2. 주요 구매 품목 분석
        // 3. 대형마트 vs 편의점 지출 비교
    }

    private void analyzeTransportation(Challenge challenge) {
        log.info("[교통 지출 분석] 시작 - 챌린지 ID: {}", challenge.getChallengeID());
        // TODO: 교통 지출 분석 로직 구현
        // 1. 대중교통 vs 택시 이용 비율
        // 2. 시간대별 이용 패턴
        // 3. 주요 이동 구간 분석
    }

    private void analyzeShopping(Challenge challenge) {
        log.info("[쇼핑 지출 분석] 시작 - 챌린지 ID: {}", challenge.getChallengeID());
        // TODO: 쇼핑 지출 분석 로직 구현
        // 1. 온라인 vs 오프라인 쇼핑 비율
        // 2. 품목별 지출 분석
        // 3. 계절별 구매 패턴 분석
    }

    private void analyzeBeauty(Challenge challenge) {
        log.info("[미용 지출 분석] 시작 - 챌린지 ID: {}", challenge.getChallengeID());
        // TODO: 미용 지출 분석 로직 구현
        // 1. 화장품 vs 시술 지출 비율
        // 2. 정기적 관리 항목 분석
        // 3. 브랜드별 선호도 분석
    }
} 