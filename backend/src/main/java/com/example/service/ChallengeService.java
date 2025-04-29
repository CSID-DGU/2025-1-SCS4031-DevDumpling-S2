package com.example.service;

import com.example.dto.challenge.CreateChallengeRequest;
import com.example.dto.challenge.ChallengeResponse;
import com.example.dto.challenge.JoinChallengeRequest;
import com.example.dto.challenge.ParticipationResponse;
import com.example.dto.challenge.ChallengeSummaryResponse;
import com.example.dto.challenge.ChallengeDetailResponse;
import com.example.entity.Challenge;
import com.example.entity.Participation;
import com.example.entity.User;
import com.example.repository.ChallengeRepository;
import com.example.repository.ParticipationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChallengeService {

    private final ChallengeRepository challengeRepository;
    private final ParticipationRepository participationRepository;

    @Transactional
    public ChallengeResponse createChallenge(CreateChallengeRequest request, User user) {
        Challenge challenge = Challenge.builder()
                .user(user)
                .title(request.getTitle())
                .description(request.getDescription())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
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

    @Transactional
    public ParticipationResponse joinChallenge(JoinChallengeRequest request, User user) {
        Challenge challenge = challengeRepository.findById(request.getChallengeId())
            .orElseThrow(() -> new IllegalArgumentException("챌린지를 찾을 수 없습니다."));

        // 이미 참여 중인지 확인
        if (participationRepository.existsByUserAndChallenge(user, challenge)) {
            throw new IllegalStateException("이미 참여 중인 챌린지입니다.");
        }

        // 참여 가능 인원 확인
        long currentParticipants = participationRepository.countByChallenge(challenge);
        if (currentParticipants >= challenge.getMaxParticipants()) {
            throw new IllegalStateException("챌린지 참여 인원이 초과되었습니다.");
        }

        // 비공개 챌린지인 경우 초대 코드 확인
        if (challenge.getType() == Challenge.ChallengeType.PRIVATE) {
            if (!challenge.getInviteCode().equals(request.getInviteCode())) {
                throw new IllegalArgumentException("잘못된 초대 코드입니다.");
            }
        }

        // 참여 정보 생성
        Participation participation = Participation.builder()
            .user(user)
            .challenge(challenge)
            .joinDate(LocalDate.now())
            .successRate(0.0f)
            .rank(0)
            .build();

        Participation savedParticipation = participationRepository.save(participation);
        return ParticipationResponse.from(savedParticipation);
    }

    private String generateInviteCode() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

    public Page<ChallengeSummaryResponse> getChallengesByCategory(
            Challenge.ChallengeCategory category,
            String status,
            Pageable pageable) {
        
        LocalDate currentDate = LocalDate.now();
        Page<Challenge> challenges;

        switch (status != null ? status.toUpperCase() : "ALL") {
            case "ACTIVE":
                challenges = challengeRepository.findActiveChallengesByCategory(
                    category, currentDate, pageable);
                break;
            case "UPCOMING":
                challenges = challengeRepository.findUpcomingChallengesByCategory(
                    category, currentDate, pageable);
                break;
            case "COMPLETED":
                challenges = challengeRepository.findCompletedChallengesByCategory(
                    category, currentDate, pageable);
                break;
            default:
                challenges = challengeRepository.findByCategory(category, pageable);
        }

        return challenges.map(challenge -> {
            Integer currentParticipants = participationRepository.countByChallenge(challenge).intValue();
            return ChallengeSummaryResponse.from(challenge, currentParticipants);
        });
    }

    public ChallengeDetailResponse getChallengeDetail(Long challengeId) {
        Challenge challenge = challengeRepository.findById(challengeId)
            .orElseThrow(() -> new IllegalArgumentException("챌린지를 찾을 수 없습니다."));

        List<Participation> participants = participationRepository.findByChallenge(challenge);
        
        // 성공률 기준으로 참여자들을 정렬하고 랭크 계산
        calculateRanks(participants);
        
        return ChallengeDetailResponse.from(challenge, participants);
    }

    /**
     * 참여자들의 성공률을 기준으로 랭크를 계산합니다.
     * 동일한 성공률을 가진 참여자들은 같은 순위를 가집니다.
     */
    private void calculateRanks(List<Participation> participants) {
        // 성공률 내림차순으로 정렬
        participants.sort((p1, p2) -> Float.compare(p2.getSuccessRate(), p1.getSuccessRate()));
        
        int currentRank = 1;
        float previousSuccessRate = -1;
        int sameRankCount = 0;

        for (Participation p : participants) {
            // 이전 참여자와 성공률이 다르면 현재 순위를 사용
            if (p.getSuccessRate() != previousSuccessRate) {
                currentRank = currentRank + sameRankCount;
                sameRankCount = 1;
            } else {
                // 성공률이 같으면 같은 순위 부여
                sameRankCount++;
            }
            
            p.setRank(currentRank);
            previousSuccessRate = p.getSuccessRate();
        }
    }
} 