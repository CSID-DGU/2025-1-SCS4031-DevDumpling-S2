package com.example.service.analysis;

import com.example.entity.Challenge;
import com.example.entity.Participation;
import com.example.entity.User;
import com.example.entity.Challenge.SavingsGoalType;
import com.example.dummy.entity.BankBalance; // 은행 계좌 정보 엔티티
import com.example.dummy.entity.BankTransaction; // 은행 거래내역 엔티티
import com.example.dummy.repository.BankBalanceRepository; // 은행 계좌 리포지토리
import com.example.dummy.repository.BankTransactionRepository; // 은행 거래내역 리포지토리
import com.example.repository.ParticipationRepository; // 참여자 정보 리포지토리
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class SavingsAnalyzer implements ChallengeAnalyzer {

    private final ParticipationRepository participationRepository;
    private final BankBalanceRepository bankBalanceRepository;
    private final BankTransactionRepository bankTransactionRepository;

    @Override
    @Transactional // 분석 과정에서 여러 DB 조회가 발생할 수 있으므로 트랜잭션 처리
    public void analyze(Challenge challenge) {
        log.info("[저축 분석] 시작 - 챌린지 ID: {}, 카테고리: {}",
                challenge.getChallengeID(),
                challenge.getCategory());

        // 0. 챌린지 기간 가져오기
        LocalDate challengeStartDate = challenge.getStartDate();
        LocalDate challengeEndDate = challenge.getEndDate();

        // 1. 챌린지 참여자 목록 가져오기
        List<Participation> participants = participationRepository.findByChallenge(challenge);
        if (participants.isEmpty()) {
            log.info("[저축 분석] 참여자가 없는 챌린지입니다. ID: {}", challenge.getChallengeID());
            return;
        }

        // 2. 챌린지 목표 가져오기 (Challenge 엔티티에서 직접 사용)
        SavingsChallengeGoal goal = new SavingsChallengeGoal(challenge.getTargetSavingsAmount(), challenge.getSavingsGoalType());
        
        if (goal.getTargetAmount() == null || goal.getTargetAmount() <= 0 || goal.getGoalType() == null) {
            log.warn("[저축 분석] 유효한 챌린지 목표가 설정되지 않았습니다. ID: {}. 목표 금액: {}, 목표 유형: {}", 
                     challenge.getChallengeID(), challenge.getTargetSavingsAmount(), challenge.getSavingsGoalType());
            // 목표가 유효하지 않을 경우 분석을 어떻게 처리할지 결정
            // (예: 참여자 결과에 "목표 미설정"으로 기록하고 분석 중단 또는 기본값 사용)
             participants.forEach(p -> updateParticipationResult(p, 0.0, "챌린지 목표 미설정"));
            return;
        }

        // 3. 각 참여자별 저축 분석 수행
        for (Participation participation : participants) {
            User user = participation.getUser();
            log.info("[저축 분석] 사용자 ID: {} 분석 시작", user.getId());

            // 3.1. 사용자의 활성화된 예금/적금 계좌 목록 조회
            List<BankBalance> userSavingAccounts = bankBalanceRepository.findByUserId(user.getId())
                    .stream()
                    .filter(BankBalance::getIsActive) // 활성화된 계좌만
                    .filter(account -> isSavingsAccount(account.getAccountType())) // 저축성 계좌 필터링
                    .collect(Collectors.toList());

            if (userSavingAccounts.isEmpty()) {
                log.info("[저축 분석] 사용자 ID: {} 활성화된 저축 계좌 없음", user.getId());
                updateParticipationResult(participation, 0.0, "저축 계좌 없음"); // 참여 결과 업데이트
                continue;
            }

            long totalSavingsAmount = 0;
            int totalSavingsCount = 0;
            // Declare a list to aggregate all transactions for the current user over the period
            List<BankTransaction> allUserTransactionsInPeriod = new ArrayList<>();

            // 3.2. 각 저축 계좌별로 챌린지 기간 동안의 입금 내역 분석
            for (BankBalance account : userSavingAccounts) {
                List<BankTransaction> transactionsForAccount = bankTransactionRepository
                        .findByUserIdAndAccountNumberAndTransactionDateBetween(
                                user.getId(),
                                account.getAccountNumber(),
                                challengeStartDate.atStartOfDay(), // LocalDate를 LocalDateTime으로 변환
                                challengeEndDate.plusDays(1).atStartOfDay() // endDate 포함
                        )
                        .stream()
                        .filter(tx -> "입금".equals(tx.getTransactionType()) || "이자".equals(tx.getTransactionType())) // 입금 또는 이자 거래만
                        .collect(Collectors.toList());

                for (BankTransaction tx : transactionsForAccount) {
                    totalSavingsAmount += tx.getAmount();
                    totalSavingsCount++;
                }
                allUserTransactionsInPeriod.addAll(transactionsForAccount); // Aggregate transactions

                log.info("[저축 분석] 사용자 ID: {}, 계좌: {}, 기간 내 입금/이자 건수: {}, 금액: {}",
                         user.getId(), account.getAccountNumber(), transactionsForAccount.size(),
                         transactionsForAccount.stream().mapToLong(BankTransaction::getAmount).sum());
            }

            // 3.3. 목표 달성률 계산
            // Pass the aggregated list of all transactions for the user
            double achievementRate = calculateAchievementRate(totalSavingsAmount, goal, challenge, allUserTransactionsInPeriod);
            log.info("[저축 분석] 사용자 ID: {}, 총 저축액: {}, 총 저축 건수: {}, 목표 달성률: {}%",
                     user.getId(), totalSavingsAmount, totalSavingsCount, String.format("%.2f", achievementRate * 100));

            // 3.4. 분석 결과 저장 (Participation 엔티티 또는 별도 분석 결과 엔티티)
            //     예시: Participation 엔티티에 successRate, metrics (JSON) 필드가 있다고 가정
            String analysisDetails = String.format("총 저축액: %d원, 저축 횟수: %d회", totalSavingsAmount, totalSavingsCount);
            updateParticipationResult(participation, achievementRate, analysisDetails);

            log.info("[저축 분석] 사용자 ID: {} 분석 완료", user.getId());
        }

        // 4. (선택) 챌린지 전체 결과 집계 및 알림 등 후처리

        log.info("[저축 분석] 완료 - 챌린지 ID: {}", challenge.getChallengeID());
    }

    // 계좌 유형 문자열을 보고 저축성 계좌인지 판별하는 메소드 (개선 필요)
    private boolean isSavingsAccount(String accountType) {
        if (accountType == null || accountType.trim().isEmpty()) {
            return false;
        }
        String lowerAccountType = accountType.toLowerCase();

        // 명확히 저축성 계좌가 아닌 유형 (대출, 카드 등) 우선 제외
        if (lowerAccountType.contains("대출") || 
            lowerAccountType.contains("카드") || // 신용카드, 체크카드 등
            lowerAccountType.contains("론") ||   // loan
            lowerAccountType.contains("펀드") || 
            lowerAccountType.contains("보험")) {
            return false;
        }

        // 저축성 계좌로 볼 수 있는 키워드 포함 여부 확인
        // (상품명에 따라 매우 다양할 수 있으므로 지속적인 관리가 필요)
        return lowerAccountType.contains("예금") ||
               lowerAccountType.contains("적금") ||
               lowerAccountType.contains("저축") ||
               lowerAccountType.contains("입출금") || // 자유입출금 등
               lowerAccountType.contains("보통") ||   // 보통예금
               lowerAccountType.contains("자유") ||   // 자유적금, 자유예금
               lowerAccountType.contains("청약") ||   // 주택청약종합저축 등
               lowerAccountType.contains("cma") ||    // CMA
               lowerAccountType.contains("mmda");     // MMDA
    }

    // 목표 달성률 계산
    private double calculateAchievementRate(long currentSavings, SavingsChallengeGoal goal, Challenge challenge, List<BankTransaction> userTransactionsInChallengePeriod) {
        if (goal == null || goal.getTargetAmount() <= 0 || goal.getGoalType() == null) {
            return 0.0; // 목표가 없거나 0 이하면 달성률 0
        }

        double rate = 0.0;

        if (goal.getGoalType() == SavingsGoalType.TOTAL_AMOUNT_IN_PERIOD) {
            rate = (double) currentSavings / goal.getTargetAmount();
        } else if (goal.getGoalType() == SavingsGoalType.MONTHLY_TARGET) {
            // 월별 목표액 달성 로직 (구현 필요)
            // 예시: 챌린지 기간 동안 매월 말일 기준으로 userTransactionsInChallengePeriod를 분석하여
            // 각 월별 목표 달성 여부 확인 후 전체 챌린지 성공 여부 판단
            log.warn("[저축 분석] 월별 목표 달성 방식에 대한 분석 로직은 아직 구현되지 않았습니다. 챌린지 ID: {}", challenge.getChallengeID());
            // 우선 총액 기준으로 계산하거나, 별도 처리 필요
            // 여기서는 임시로 총액 기준으로 계산
             rate = (double) currentSavings / goal.getTargetAmount(); 
        }
        
        return Math.min(rate, 1.0); // 최대 100% (1.0)
    }

    // 참여 결과 업데이트 (Participation 엔티티에 저장한다고 가정)
    private void updateParticipationResult(Participation participation, double achievementRate, String details) {
        participation.setSuccessRate((float) achievementRate); // float로 형변환
        // participation.setMetrics(details); // 만약 metrics 필드가 있다면
        participationRepository.save(participation);
    }

    // 저축 목표를 나타내는 내부 클래스 (또는 별도 DTO)
    private static class SavingsChallengeGoal {
        private final Long targetAmount; // 목표 금액
        private final Challenge.SavingsGoalType goalType; // 목표 유형 (총액, 월별 등)

        public SavingsChallengeGoal(Long targetAmount, Challenge.SavingsGoalType goalType) {
            this.targetAmount = targetAmount;
            this.goalType = goalType;
        }

        public Long getTargetAmount() {
            return targetAmount;
        }

        public Challenge.SavingsGoalType getGoalType() {
            return goalType;
        }
    }
} 