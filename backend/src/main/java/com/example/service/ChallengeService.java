package com.example.service;

import com.example.dto.challenge.CreateChallengeRequest;
import com.example.dto.challenge.ChallengeResponse;
import com.example.dto.challenge.JoinChallengeRequest;
import com.example.dto.challenge.ParticipationResponse;
import com.example.dto.challenge.ChallengeSummaryResponse;
import com.example.dto.challenge.ChallengeDetailResponse;
import com.example.dto.challenge.UpdateChallengeRequest;
import com.example.dto.challenge.CompleteChallengeRequest;
import com.example.dto.challenge.ChallengeRankingResponse;
import com.example.entity.Challenge;
import com.example.entity.Participation;
import com.example.entity.User;
import com.example.repository.ChallengeRepository;
import com.example.repository.ParticipationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
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
                .targetSavingsAmount(request.getTargetSavingsAmount())
                .savingsGoalType(request.getSavingsGoalType())
                .foodGoalType(request.getFoodGoalType())
                .foodTargetAmount(request.getFoodTargetAmount())
                .foodTargetCount(request.getFoodTargetCount())
                .foodTargetRatio(request.getFoodTargetRatio())
                .cafeSnackGoalType(request.getCafeSnackGoalType())
                .cafeSnackTargetAmount(request.getCafeSnackTargetAmount())
                .cafeSnackTargetDailyAmount(request.getCafeSnackTargetDailyAmount())
                .cafeSnackTargetWeeklyCount(request.getCafeSnackTargetWeeklyCount())
                .build();

        if (challenge.getType() == Challenge.ChallengeType.PRIVATE) {
            challenge.setInviteCode(generateInviteCode());
        }

        Challenge savedChallenge = challengeRepository.save(challenge);

        // 생성자를 자동으로 참여자로 등록
        Participation participation = Participation.builder()
                .user(user)
                .challenge(savedChallenge)
                .joinDate(LocalDate.now())
                .successRate(0.0f)
                .rank(1)
                .build();

        participationRepository.save(participation);

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
        try {
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

            if (challenges == null) {
                log.error("[챌린지 목록 조회] 데이터베이스 조회 실패 - 카테고리: {}, 상태: {}", category, status);
                throw new IllegalStateException("데이터베이스 조회 실패");
            }

            return challenges.map(challenge -> {
                try {
                    Integer currentParticipants = participationRepository.countByChallenge(challenge).intValue();
                    Integer maxParticipants = challenge.getMaxParticipants();
                    if (maxParticipants == null) {
                        maxParticipants = 100; // 기본값 설정
                    }
                    return ChallengeSummaryResponse.from(challenge, currentParticipants);
                } catch (Exception e) {
                    log.error("[챌린지 목록 조회] 응답 변환 실패 - 챌린지 ID: {}, 오류: {}", 
                        challenge.getChallengeID(), e.getMessage());
                    throw new IllegalStateException("응답 변환 실패");
                }
            });
        } catch (Exception e) {
            log.error("[챌린지 목록 조회] 서비스 레이어 오류: {}", e.getMessage(), e);
            throw e;
        }
    }

    public ChallengeDetailResponse getChallengeDetail(Long challengeId, User user) {
        Challenge challenge = challengeRepository.findById(challengeId)
            .orElseThrow(() -> new IllegalArgumentException("챌린지를 찾을 수 없습니다."));

        // 비공개 챌린지인 경우 참여자만 접근 가능
        if (!challenge.isPublic() && (user == null || !participationRepository.existsByUserAndChallenge(user, challenge))) {
            throw new IllegalStateException("비공개 챌린지는 참여자만 접근할 수 있습니다.");
        }

        List<Participation> participants = participationRepository.findByChallenge(challenge);
        
        // 성공률 기준으로 참여자들을 정렬하고 랭크 계산
        calculateRanks(participants);
        
        return ChallengeDetailResponse.from(challenge, participants);
    }

    public ChallengeRankingResponse getUserRanking(Long challengeId, User user) {
        Challenge challenge = challengeRepository.findById(challengeId)
            .orElseThrow(() -> new IllegalArgumentException("챌린지를 찾을 수 없습니다."));

        Participation participation = participationRepository.findByUserAndChallenge(user, challenge)
            .orElseThrow(() -> new IllegalArgumentException("해당 챌린지에 참여하고 있지 않습니다."));

        // 참여자들의 랭크를 다시 계산
        List<Participation> participants = participationRepository.findByChallenge(challenge);
        calculateRanks(participants);

        // 업데이트된 랭크 정보로 응답 생성
        return ChallengeRankingResponse.from(participation);
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

    @Transactional
    public ChallengeResponse updateChallenge(Long challengeId, UpdateChallengeRequest request, User user) {
        Challenge challenge = challengeRepository.findById(challengeId)
            .orElseThrow(() -> new IllegalArgumentException("챌린지를 찾을 수 없습니다."));

        // 생성자 확인
        if (!challenge.getUser().getId().equals(user.getId())) {
            throw new IllegalStateException("챌린지 생성자만 수정할 수 있습니다.");
        }

        // 시작일 이전인지 확인
        if (LocalDate.now().isAfter(challenge.getStartDate())) {
            throw new IllegalStateException("이미 시작된 챌린지는 수정할 수 없습니다.");
        }

        // 수정 가능한 필드 업데이트
        if (request.getTitle() != null) {
            challenge.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            challenge.setDescription(request.getDescription());
        }
        if (request.getStartDate() != null) {
            challenge.setStartDate(request.getStartDate());
        }
        if (request.getEndDate() != null) {
            challenge.setEndDate(request.getEndDate());
        }
        if (request.getMaxParticipants() != null) {
            challenge.setMaxParticipants(request.getMaxParticipants());
        }
        if (request.getType() != null) {
            challenge.setType(request.getType());
        }
        if (request.getCategory() != null) {
            challenge.setCategory(request.getCategory());
        }

        Challenge updatedChallenge = challengeRepository.save(challenge);
        return ChallengeResponse.from(updatedChallenge);
    }

    @Transactional
    public void deleteChallenge(Long challengeId, User user) {
        Challenge challenge = challengeRepository.findById(challengeId)
            .orElseThrow(() -> new IllegalArgumentException("챌린지를 찾을 수 없습니다."));

        // 생성자 확인
        if (!challenge.getUser().getId().equals(user.getId())) {
            throw new IllegalStateException("챌린지 생성자만 삭제할 수 있습니다.");
        }

        // 참여자 확인
        long participantCount = participationRepository.countByChallenge(challenge);
        if (participantCount > 0) {
            throw new IllegalStateException("참여자가 있는 챌린지는 삭제할 수 없습니다.");
        }

        // 소프트 삭제
        challenge.setDeleted(true);
        challengeRepository.save(challenge);
    }

    @Transactional
    public void completeChallenge(Long challengeId, CompleteChallengeRequest request, User user) {
        Challenge challenge = challengeRepository.findById(challengeId)
            .orElseThrow(() -> new IllegalArgumentException("챌린지를 찾을 수 없습니다."));

        // 이미 완료된 챌린지인지 확인
        if (challenge.isCompleted()) {
            throw new IllegalStateException("이미 완료된 챌린지입니다.");
        }

        // 조기 완료인 경우 생성자/관리자 권한 확인
        if (request.isEarlyCompletion()) {
            if (!challenge.getUser().getId().equals(user.getId())) {
                throw new IllegalStateException("챌린지 생성자만 조기 완료 처리할 수 있습니다.");
            }
        }

        // 완료 처리
        challenge.setCompleted(true);
        challenge.setCompletionDate(LocalDate.now());
        challengeRepository.save(challenge);
    }

    @Scheduled(cron = "0 0 0 * * ?")  // 매일 자정에 실행
    @Transactional
    public void checkAndCompleteExpiredChallenges() {
        LocalDate today = LocalDate.now();
        List<Challenge> expiredChallenges = challengeRepository.findByEndDateBeforeAndIsCompletedFalse(today);

        for (Challenge challenge : expiredChallenges) {
            challenge.setCompleted(true);
            challenge.setCompletionDate(today);
            challengeRepository.save(challenge);
            log.info("[챌린지 자동 완료] 챌린지 ID: {}", challenge.getChallengeID());
        }
    }

    @Transactional
    public void abandonChallenge(Long challengeId, User user) {
        Challenge challenge = challengeRepository.findById(challengeId)
            .orElseThrow(() -> new IllegalArgumentException("챌린지를 찾을 수 없습니다."));

        // 이미 완료된 챌린지인지 확인
        if (challenge.isCompleted()) {
            throw new IllegalStateException("이미 완료된 챌린지는 포기할 수 없습니다.");
        }

        // 참여 정보 찾기
        Participation participation = participationRepository.findByUserAndChallenge(user, challenge)
            .orElseThrow(() -> new IllegalStateException("참여 중인 챌린지가 아닙니다."));

        // 이미 포기한 챌린지인지 확인
        if (participation.isAbandoned()) {
            throw new IllegalStateException("이미 포기한 챌린지입니다.");
        }

        // 포기 처리
        participation.setAbandoned(true);
        participation.setAbandonDate(LocalDate.now());
        participationRepository.save(participation);
    }

    public Challenge getChallengeById(Long challengeId) {
        return challengeRepository.findById(challengeId)
            .orElse(null);
    }

    public List<Participation> getParticipants(Challenge challenge) {
        return participationRepository.findByChallenge(challenge);
    }

    @Transactional
    public ChallengeResponse likeChallenge(Long challengeId, User user) {
        Challenge challenge = challengeRepository.findById(challengeId)
            .orElseThrow(() -> new IllegalArgumentException("챌린지를 찾을 수 없습니다."));
        challenge.setLikeCount(challenge.getLikeCount() + 1);
        Challenge saved = challengeRepository.save(challenge);
        return ChallengeResponse.from(saved);
    }

    @Transactional
    public ChallengeResponse unlikeChallenge(Long challengeId, User user) {
        Challenge challenge = challengeRepository.findById(challengeId)
            .orElseThrow(() -> new IllegalArgumentException("챌린지를 찾을 수 없습니다."));
        if (challenge.getLikeCount() > 0) {
            challenge.setLikeCount(challenge.getLikeCount() - 1);
        }
        Challenge saved = challengeRepository.save(challenge);
        return ChallengeResponse.from(saved);
    }
} 