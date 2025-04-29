package com.example.dto.challenge;

import lombok.Data;

@Data
public class JoinChallengeRequest {
    private Long challengeId;
    private String inviteCode;  // 비공개 챌린지 참여 시 사용
} 