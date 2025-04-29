package com.example.service;

import com.example.dto.challenge.CreateChallengeRequest;
import com.example.dto.challenge.ChallengeResponse;
import com.example.entity.Challenge;
import com.example.entity.User;
import com.example.repository.ChallengeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChallengeService {

    private final ChallengeRepository challengeRepository;

    @Transactional
    public ChallengeResponse createChallenge(CreateChallengeRequest request, User user) {
        Challenge challenge = Challenge.builder()
                .user(user)
                .title(request.getTitle())
                .description(request.getDescription())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .isOpen(request.getIsOpen())
                .maxParticipants(request.getMaxParticipants())
                .type(request.getType())
                .category(request.getCategory())
                .build();

        if (challenge.getType() == Challenge.ChallengeType.PRIVATE) {
            challenge.setInviteCode(generateInviteCode());
        }

        Challenge savedChallenge = challengeRepository.save(challenge);
        return ChallengeResponse.from(savedChallenge);
    }

    private String generateInviteCode() {
        return UUID.randomUUID().toString().substring(0, 8);
    }
} 