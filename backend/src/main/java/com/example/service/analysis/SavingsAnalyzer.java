package com.example.service.analysis;

import com.example.entity.Challenge;
import com.example.entity.Participation;
import com.example.entity.User;
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

        // 2. 챌린지 목표 파싱 (가정: 챌린지 설명에 목표 금액이 명시되어 있다고 가정)
        //    예: "100만원 모으기", "월 50만원 저축"
        //    실제로는 더 정교한 파싱 로직 필요
        SavingsChallengeGoal goal = parseSavingsChallengeGoal(challenge.getDescription());
        if (goal == null) {
            log.warn("[저축 분석] 챌린지 목표를 파싱할 수 없습니다. ID: {}", challenge.getChallengeID());
            // 목표 파싱 실패 시 분석을 어떻게 처리할지 결정 (예: 기본 목표 설정 또는 분석 중단)
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

            // 3.2. 각 저축 계좌별로 챌린지 기간 동안의 입금 내역 분석
            for (BankBalance account : userSavingAccounts) {
                List<BankTransaction> transactions = bankTransactionRepository
                        .findByUserIdAndAccountNumberAndTransactionDateBetween(
                                user.getId(),
                                account.getAccountNumber(),
                                challengeStartDate.atStartOfDay(), // LocalDate를 LocalDateTime으로 변환
                                challengeEndDate.plusDays(1).atStartOfDay() // endDate 포함
                        )
                        .stream()
                        .filter(tx -> "입금".equals(tx.getTransactionType()) || "이자".equals(tx.getTransactionType())) // 입금 또는 이자 거래만
                        .collect(Collectors.toList());

                for (BankTransaction tx : transactions) {
                    totalSavingsAmount += tx.getAmount();
                    totalSavingsCount++;
                }
                log.info("[저축 분석] 사용자 ID: {}, 계좌: {}, 기간 내 입금/이자 건수: {}, 금액: {}",
                         user.getId(), account.getAccountNumber(), transactions.size(),
                         transactions.stream().mapToLong(BankTransaction::getAmount).sum());
            }

            // 3.3. 목표 달성률 계산
            double achievementRate = calculateAchievementRate(totalSavingsAmount, goal);
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

    // 챌린지 설명으로부터 저축 목표를 파싱하는 메소드 (구체적인 로직 필요)
    private SavingsChallengeGoal parseSavingsChallengeGoal(String description) {
        if (description == null || description.trim().isEmpty()) {
            log.warn("저축 목표 파싱 실패: 설명이 비어있습니다.");
            return new SavingsChallengeGoal(1000000L, SavingsGoalType.TOTAL_AMOUNT); // 기본 목표
        }

        // 정규 표현식 패턴 정의
        // 패턴1: "1,000,000원 모으기", "1000000원 저축" (총액 목표)
        Pattern totalAmountPattern1 = Pattern.compile("(\\d{1,3}(,\\d{3})*|\\d+)\\s*원(?:\\s*(모으기|저축|달성))?");
        // 패턴2: "100만원", "50 만원" (단위 포함 총액 목표) - "만원"만 우선 처리
        Pattern totalAmountPattern2 = Pattern.compile("(\\d+)\\s*만\\s*원(?:\\s*(모으기|저축|달성))?");
        // 패턴3: "매월 500,000원 저축", "월 50만원 납입" (월간 목표)
        Pattern monthlyAmountPattern1 = Pattern.compile("(?:매월|월)\\s*(\\d{1,3}(,\\d{3})*|\\d+)\\s*원(?:\\s*(모으기|저축|납입))?");
        Pattern monthlyAmountPattern2 = Pattern.compile("(?:매월|월)\\s*(\\d+)\\s*만\\s*원(?:\\s*(모으기|저축|납입))?");

        Matcher matcher;

        // 월간 목표 우선 검사 (만원 단위)
        matcher = monthlyAmountPattern2.matcher(description);
        if (matcher.find()) {
            try {
                long amount = Long.parseLong(matcher.group(1).replaceAll("[^0-9]", "")) * 10000;
                return new SavingsChallengeGoal(amount, SavingsGoalType.MONTHLY_AMOUNT);
            } catch (NumberFormatException e) {
                log.error("월간 목표 금액(만원) 파싱 오류: {}", matcher.group(1), e);
            }
        }

        // 월간 목표 우선 검사 (원 단위)
        matcher = monthlyAmountPattern1.matcher(description);
        if (matcher.find()) {
            try {
                long amount = Long.parseLong(matcher.group(1).replaceAll("[^0-9]", ""));
                return new SavingsChallengeGoal(amount, SavingsGoalType.MONTHLY_AMOUNT);
            } catch (NumberFormatException e) {
                log.error("월간 목표 금액(원) 파싱 오류: {}", matcher.group(1), e);
            }
        }

        // 총액 목표 검사 (만원 단위)
        matcher = totalAmountPattern2.matcher(description);
        if (matcher.find()) {
            try {
                long amount = Long.parseLong(matcher.group(1).replaceAll("[^0-9]", "")) * 10000;
                return new SavingsChallengeGoal(amount, SavingsGoalType.TOTAL_AMOUNT);
            } catch (NumberFormatException e) {
                log.error("총액 목표 금액(만원) 파싱 오류: {}", matcher.group(1), e);
            }
        }
        
        // 총액 목표 검사 (원 단위)
        matcher = totalAmountPattern1.matcher(description);
        if (matcher.find()) {
            try {
                long amount = Long.parseLong(matcher.group(1).replaceAll("[^0-9]", ""));
                return new SavingsChallengeGoal(amount, SavingsGoalType.TOTAL_AMOUNT);
            } catch (NumberFormatException e) {
                log.error("총액 목표 금액(원) 파싱 오류: {}", matcher.group(1), e);
            }
        }
        
        // 기존 키워드 기반 파싱 (Fallback)
        try {
            if (description.contains("목표금액:")) {
                String amountStr = description.split("목표금액:")[1].replaceAll("[^0-9]", "");
                long targetAmount = Long.parseLong(amountStr);
                return new SavingsChallengeGoal(targetAmount, SavingsGoalType.TOTAL_AMOUNT);
            } else if (description.contains("월간목표:")) {
                 String amountStr = description.split("월간목표:")[1].replaceAll("[^0-9]", "");
                long targetAmount = Long.parseLong(amountStr);
                return new SavingsChallengeGoal(targetAmount, SavingsGoalType.MONTHLY_AMOUNT);
            }
        } catch (Exception e) {
            log.error("기존 방식 목표 파싱 중 오류 발생: {}", description, e);
            // Fallthrough to default
        }

        log.warn("저축 목표 파싱 실패, 기본 목표로 설정: {}", description);
        return new SavingsChallengeGoal(1000000L, SavingsGoalType.TOTAL_AMOUNT); // 기본 목표 100만원
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
    private double calculateAchievementRate(long currentSavings, SavingsChallengeGoal goal) {
        if (goal == null || goal.getTargetAmount() <= 0) {
            return 0.0; // 목표가 없거나 0 이하면 달성률 0
        }
        // 목표 유형에 따라 계산 방식 변경 가능 (예: 월별 목표, 기간 총액 목표 등)
        // 현재는 단순 총액 목표로 가정
        double rate = (double) currentSavings / goal.getTargetAmount();
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
        private final long targetAmount;
        private final SavingsGoalType goalType; // 목표 유형 (총액, 월별 등)

        public SavingsChallengeGoal(long targetAmount, SavingsGoalType goalType) {
            this.targetAmount = targetAmount;
            this.goalType = goalType;
        }

        public long getTargetAmount() {
            return targetAmount;
        }

        public SavingsGoalType getGoalType() {
            return goalType;
        }
    }

    private enum SavingsGoalType {
        TOTAL_AMOUNT, // 챌린지 기간 전체 목표 금액
        MONTHLY_AMOUNT // 월별 목표 금액 (더 복잡한 계산 필요)
    }
} 