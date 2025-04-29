package com.example.dto.challenge;

import com.example.entity.Challenge;
import com.example.entity.Participation;
import lombok.Data;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class ChallengeDetailResponse {
    private Long challengeId;
    private String title;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer maxParticipants;
    private Integer currentParticipants;
    private Boolean isPrivate;
    private String category;
    private String status;  // ACTIVE, UPCOMING, COMPLETED
    private Double participationRate;
    private String creatorNickname;
    private List<ParticipantInfo> participants;

    @Data
    public static class ParticipantInfo {
        private String nickname;
        private LocalDate joinDate;
        private Float successRate;
        private Integer rank;

        public static ParticipantInfo from(Participation participation) {
            ParticipantInfo info = new ParticipantInfo();
            info.setNickname(participation.getUser().getNickname());
            info.setJoinDate(participation.getJoinDate());
            info.setSuccessRate(participation.getSuccessRate());
            info.setRank(participation.getRank());
            return info;
        }
    }

    public static ChallengeDetailResponse from(Challenge challenge, List<Participation> participants) {
        ChallengeDetailResponse response = new ChallengeDetailResponse();
        response.setChallengeId(challenge.getChallengeID());
        response.setTitle(challenge.getTitle());
        response.setDescription(challenge.getDescription());
        response.setStartDate(challenge.getStartDate());
        response.setEndDate(challenge.getEndDate());
        response.setMaxParticipants(challenge.getMaxParticipants());
        response.setCurrentParticipants(participants.size());
        response.setIsPrivate(challenge.getType() == Challenge.ChallengeType.PRIVATE);
        response.setCategory(challenge.getCategory().name());
        response.setCreatorNickname(challenge.getUser().getNickname());
        
        // 참여율 계산
        response.setParticipationRate(
            (double) participants.size() / challenge.getMaxParticipants() * 100
        );

        // 챌린지 상태 설정
        LocalDate now = LocalDate.now();
        if (now.isBefore(challenge.getStartDate())) {
            response.setStatus("UPCOMING");
        } else if (now.isAfter(challenge.getEndDate())) {
            response.setStatus("COMPLETED");
        } else {
            response.setStatus("ACTIVE");
        }

        // 참여자 정보 설정
        response.setParticipants(
            participants.stream()
                .map(ParticipantInfo::from)
                .collect(Collectors.toList())
        );

        return response;
    }
} 