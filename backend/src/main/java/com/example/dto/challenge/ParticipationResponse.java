package com.example.dto.challenge;

import com.example.entity.Participation;
import lombok.Data;
import java.time.LocalDate;

@Data
public class ParticipationResponse {
    private Long participationId;
    private Long challengeId;
    private Long userId;
    private LocalDate joinDate;
    private Float successRate;
    private Integer rank;

    public static ParticipationResponse from(Participation participation) {
        ParticipationResponse response = new ParticipationResponse();
        response.setParticipationId(participation.getParticipationID());
        response.setChallengeId(participation.getChallenge().getChallengeID());
        response.setUserId(participation.getUser().getId());
        response.setJoinDate(participation.getJoinDate());
        response.setSuccessRate(participation.getSuccessRate());
        response.setRank(participation.getRank());
        return response;
    }
} 