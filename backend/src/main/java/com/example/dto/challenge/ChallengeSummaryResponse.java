package com.example.dto.challenge;

import com.example.entity.Challenge;
import lombok.Data;
import java.time.LocalDate;

@Data
public class ChallengeSummaryResponse {
    private Long challengeId;
    private String title;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer maxParticipants;
    private Integer currentParticipants;
    private Boolean isPrivate;
    private String category;
    private Double participationRate;  // 참여율 (현재 참여자 / 최대 참여자 * 100)
    private Integer likeCount;

    public static ChallengeSummaryResponse from(Challenge challenge, Integer currentParticipants) {
        ChallengeSummaryResponse response = new ChallengeSummaryResponse();
        response.setChallengeId(challenge.getChallengeID());
        response.setTitle(challenge.getTitle());
        response.setDescription(challenge.getDescription());
        response.setStartDate(challenge.getStartDate());
        response.setEndDate(challenge.getEndDate());
        response.setMaxParticipants(challenge.getMaxParticipants());
        response.setCurrentParticipants(currentParticipants);
        response.setIsPrivate(challenge.getType() == Challenge.ChallengeType.PRIVATE);
        response.setCategory(challenge.getCategory().name());
        response.setParticipationRate(
            (double) currentParticipants / challenge.getMaxParticipants() * 100
        );
        response.setLikeCount(challenge.getLikeCount());
        return response;
    }
} 