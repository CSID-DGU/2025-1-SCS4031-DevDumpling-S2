package com.example.service.analysis;

import com.example.entity.Challenge;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class FixedExpenseAnalyzer implements ChallengeAnalyzer {
    @Override
    public void analyze(Challenge challenge) {
        log.info("[고정 지출 분석] 시작 - 챌린지 ID: {}", challenge.getChallengeID());
        
        // TODO: 고정 지출 분석 로직 구현
        // 1. 월별 고정 지출 항목 분석
        // 2. 통신비 요금제 최적화 분석
        // 3. 주거 비용 분석 (월세/관리비/공과금)
        // 4. 고정 지출 절감 가능 항목 도출
        // 5. 연간 고정 지출 추이 분석
        // 6. 소득 대비 고정 지출 비율 분석
        // 7. 유사 조건 사용자 대비 지출 수준 비교
    }
} 