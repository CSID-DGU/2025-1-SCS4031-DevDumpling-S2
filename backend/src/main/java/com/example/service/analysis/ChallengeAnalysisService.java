package com.example.service.analysis;

import com.example.entity.Challenge;
import com.example.entity.challenge.AnalysisType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChallengeAnalysisService {
    private final AnalyzerFactory analyzerFactory;

    public void analyzeChallenge(Challenge challenge) {
        log.info("[챌린지 분석 시작] 챌린지 ID: {}", challenge.getChallengeID());
        
        AnalysisType analysisType = challenge.getCategory().getAnalysisType();
        ChallengeAnalyzer analyzer = analyzerFactory.getAnalyzer(analysisType);
        
        analyzer.analyze(challenge);
        
        log.info("[챌린지 분석 완료] 챌린지 ID: {}", challenge.getChallengeID());
    }
} 