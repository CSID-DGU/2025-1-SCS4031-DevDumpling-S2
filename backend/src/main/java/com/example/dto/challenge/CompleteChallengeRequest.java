package com.example.dto.challenge;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CompleteChallengeRequest {
    private boolean isEarlyCompletion;  // 조기 완료 여부
} 