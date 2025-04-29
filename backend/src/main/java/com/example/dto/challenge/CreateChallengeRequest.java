package com.example.dto.challenge;

import com.example.entity.Challenge.ChallengeType;
import com.example.entity.Challenge.ChallengeCategory;
import lombok.Data;
import java.time.LocalDate;

@Data
public class CreateChallengeRequest {
    private String title;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean isOpen;
    private Integer maxParticipants;
    private ChallengeType type;
    private ChallengeCategory category;
} 