package com.example.dto.challenge;

import com.example.entity.Participation;
import lombok.Data;
import java.util.Map;
import java.util.HashMap;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;

@Data
public class ChallengeRankingResponse {
    private Long participationId;
    private Long userId;
    private String nickname;
    private Integer overallRank;
    private Map<String, Integer> detailedRanks;
    private Map<String, Double> metrics;
    
    public static ChallengeRankingResponse from(Participation participation) {
        ChallengeRankingResponse response = new ChallengeRankingResponse();
        response.setParticipationId(participation.getParticipationID());
        response.setUserId(participation.getUser().getId());
        response.setNickname(participation.getUser().getNickname());
        response.setOverallRank(participation.getRank());
        
        // JSON 형식의 rankDetails를 Map으로 변환
        response.setDetailedRanks(parseRankDetails(participation.getRankDetails()));
        
        // 추가 지표 정보 설정
        response.setMetrics(parseMetrics(participation.getMetrics()));
        
        return response;
    }
    
    private static Map<String, Integer> parseRankDetails(String rankDetails) {
        if (rankDetails == null) {
            return new HashMap<>();
        }
        try {
            return new ObjectMapper().readValue(rankDetails, new TypeReference<Map<String, Integer>>() {});
        } catch (JsonProcessingException e) {
            return new HashMap<>();
        }
    }
    
    private static Map<String, Double> parseMetrics(String metrics) {
        if (metrics == null) {
            return new HashMap<>();
        }
        try {
            return new ObjectMapper().readValue(metrics, new TypeReference<Map<String, Double>>() {});
        } catch (JsonProcessingException e) {
            return new HashMap<>();
        }
    }
} 