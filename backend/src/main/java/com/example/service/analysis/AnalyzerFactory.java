package com.example.service.analysis;

import com.example.entity.challenge.AnalysisType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class AnalyzerFactory {
    private final Map<AnalysisType, ChallengeAnalyzer> analyzerMap;

    public ChallengeAnalyzer getAnalyzer(AnalysisType type) {
        ChallengeAnalyzer analyzer = analyzerMap.get(type);
        if (analyzer == null) {
            throw new IllegalArgumentException("Unsupported analysis type: " + type);
        }
        return analyzer;
    }
} 