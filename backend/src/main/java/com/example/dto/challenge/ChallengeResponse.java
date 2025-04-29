package com.example.dto.challenge;

import com.example.entity.Challenge;
import com.example.entity.Challenge.ChallengeType;
import com.example.entity.Challenge.ChallengeCategory;
import lombok.Data;
import java.time.LocalDate;

@Data
public class ChallengeResponse {
    private Long challengeId;
    private String title;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer maxParticipants;
    private ChallengeType type;
    private ChallengeCategory category;
    private String inviteCode;
    
    public static ChallengeResponse from(Challenge challenge) {
        ChallengeResponse response = new ChallengeResponse();
        response.setChallengeId(challenge.getChallengeID());
        response.setTitle(challenge.getTitle());
        response.setDescription(challenge.getDescription());
        response.setStartDate(challenge.getStartDate());
        response.setEndDate(challenge.getEndDate());
        response.setMaxParticipants(challenge.getMaxParticipants());
        response.setType(challenge.getType());
        response.setCategory(challenge.getCategory());
        response.setInviteCode(challenge.getInviteCode());
        return response;
    }
} 