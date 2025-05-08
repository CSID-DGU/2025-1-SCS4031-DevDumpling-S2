package com.example.dto.challenge;

import com.example.entity.ChallengeMessage;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ChallengeMessageResponse {
    private Long messageId;
    private Long challengeId;
    private Long userId;
    private String nickname;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean isDeleted;

    public static ChallengeMessageResponse from(ChallengeMessage message) {
        ChallengeMessageResponse response = new ChallengeMessageResponse();
        response.setMessageId(message.getMessageId());
        response.setChallengeId(message.getChallenge().getChallengeID());
        response.setUserId(message.getUser().getId());
        response.setNickname(message.getUser().getNickname());
        response.setContent(message.getContent());
        response.setCreatedAt(message.getCreatedAt());
        response.setUpdatedAt(message.getUpdatedAt());
        response.setDeleted(message.isDeleted());
        return response;
    }
} 