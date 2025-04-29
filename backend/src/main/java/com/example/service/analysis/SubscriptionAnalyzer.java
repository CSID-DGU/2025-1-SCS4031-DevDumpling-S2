package com.example.service.analysis;

import com.example.entity.Challenge;
import com.example.entity.Challenge.ChallengeCategory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SubscriptionAnalyzer implements ChallengeAnalyzer {
    @Override
    public void analyze(Challenge challenge) {
        log.info("[구독 서비스 분석] 시작 - 챌린지 ID: {}", challenge.getChallengeID());

        ChallengeCategory category = challenge.getCategory();
        
        if (category == ChallengeCategory.GAME_OTT) {
            analyzeGameOtt(challenge);
        } else if (category == ChallengeCategory.HEALTH_EXERCISE) {
            analyzeHealthExercise(challenge);
        }
    }

    private void analyzeGameOtt(Challenge challenge) {
        log.info("[게임/OTT 구독 분석] 시작 - 챌린지 ID: {}", challenge.getChallengeID());
        // TODO: 게임/OTT 구독 분석 로직 구현
        // 1. 구독 중인 서비스 현황 분석
        // 2. 서비스별 이용 빈도 분석
        // 3. 중복되는 서비스 확인
        // 4. 구독 최적화 추천
        // 5. 패밀리 플랜 전환 가능 여부 분석
    }

    private void analyzeHealthExercise(Challenge challenge) {
        log.info("[건강/운동 구독 분석] 시작 - 챌린지 ID: {}", challenge.getChallengeID());
        // TODO: 건강/운동 구독 분석 로직 구현
        // 1. 헬스장/피트니스 이용권 분석
        // 2. 운동 관련 구독 서비스 현황
        // 3. 이용 빈도 대비 비용 효율성 분석
        // 4. 최적 요금제/이용권 추천
        // 5. 건강 관련 앱 구독 현황 분석
    }
} 