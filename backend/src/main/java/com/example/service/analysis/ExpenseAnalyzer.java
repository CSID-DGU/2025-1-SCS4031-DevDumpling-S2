package com.example.service.analysis;

import com.example.entity.Challenge;
import com.example.entity.Challenge.ChallengeCategory;
import com.example.entity.Participation;
import com.example.dummy.entity.CardTransaction;
import com.example.dummy.repository.CardTransactionRepository;
import com.example.repository.ParticipationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.Data;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExpenseAnalyzer implements ChallengeAnalyzer {
    private final CardTransactionRepository cardTransactionRepository;
    private final ParticipationRepository participationRepository;

    @Override
    public void analyze(Challenge challenge) {
        log.info("[일반 지출 분석] 챌린지 ID: {}, 카테고리: {}", 
            challenge.getChallengeID(), 
            challenge.getCategory());

        ChallengeCategory category = challenge.getCategory();
        
        switch (category) {
            case FOOD:
                analyzeFood(challenge);
                break;
            case CAFE_SNACK:
                analyzeCafeSnack(challenge);
                break;
            case TRAVEL:
                analyzeTravel(challenge);
                break;
            case PET:
                analyzePet(challenge);
                break;
            case MART_CONVENIENCE:
                analyzeMartConvenience(challenge);
                break;
            case TRANSPORTATION:
                analyzeTransportation(challenge);
                break;
            case SHOPPING:
                analyzeShopping(challenge);
                break;
            case BEAUTY:
                analyzeBeauty(challenge);
                break;
        }
    }

    @Transactional
    private void analyzeFood(Challenge challenge) {
        log.info("[식비 분석] 시작 - 챌린지 ID: {}", challenge.getChallengeID());
        
        List<Participation> participants = participationRepository.findByChallenge(challenge);
        LocalDate startDate = challenge.getStartDate();
        LocalDate endDate = challenge.getEndDate();
        
        // 챌린지 목표 유형 파싱
        FoodChallengeGoal goal = parseChallengeGoal(challenge.getDescription());
        
        Map<Long, FoodAnalysisResult> analysisResults = new HashMap<>();
        
        // 각 참여자의 식비 분석 수행
        for (Participation participation : participants) {
            Long userId = participation.getUser().getId();
            
            // 1. 기간 내 총 식비 지출 금액 분석
            List<CardTransaction> foodTransactions = cardTransactionRepository
                .findByUserIdAndTransactionDateBetween(userId, startDate, endDate)
                .stream()
                .filter(transaction -> isFoodTransaction(transaction))
                .collect(Collectors.toList());
            
            // 2. 식비 분야별 지출 금액 분석
            Map<String, Long> categoryExpenses = analyzeFoodCategories(foodTransactions);
            
            // 3. 목표 달성률 계산 (챌린지 유형에 따라 다르게 계산)
            double achievementRate = calculateAchievementRate(foodTransactions, goal);
            
            // 4. 추가 지표 계산
            Map<String, Double> additionalMetrics = calculateAdditionalMetrics(foodTransactions, goal);
            
            // 분석 결과 저장
            FoodAnalysisResult result = new FoodAnalysisResult(
                categoryExpenses,
                achievementRate,
                additionalMetrics
            );
            analysisResults.put(userId, result);
        }
        
        // 각 목표별 순위 계산
        Map<RankingType, List<Participation>> rankings = new EnumMap<>(RankingType.class);
        rankings.put(RankingType.ACHIEVEMENT, calculateAchievementRankings(participants, analysisResults));
        rankings.put(RankingType.EXPENSE, calculateExpenseRankings(participants, analysisResults));
        rankings.put(RankingType.DIVERSITY, calculateDiversityRankings(participants, analysisResults));
        
        // 목표 유형별 특화 순위 계산
        switch (goal.getType()) {
            case DELIVERY_REDUCTION:
                rankings.put(RankingType.DELIVERY, calculateDeliveryRankings(participants, analysisResults));
                break;
            case DINING_OUT_REDUCTION:
                rankings.put(RankingType.DINING_OUT, calculateDiningOutRankings(participants, analysisResults));
                break;
            case HOME_COOKING_INCREASE:
                rankings.put(RankingType.HOME_COOKING, calculateHomeCookingRankings(participants, analysisResults));
                break;
        }
        
        // 전체 순위 계산 (각 순위의 가중 평균)
        List<Participation> overallRankings = calculateOverallRankings(participants, rankings, goal);
        
        // 참여자들의 순위 업데이트
        updateParticipantRankings(participants, rankings, overallRankings);
    }
    
    private void updateParticipantRankings(
        List<Participation> participants,
        Map<RankingType, List<Participation>> rankings,
        List<Participation> overallRankings
    ) {
        // 각 참여자의 순위 정보 업데이트
        for (Participation participation : participants) {
            Long userId = participation.getUser().getId();
            
            // 각 순위 유형별 순위 저장
            Map<RankingType, Integer> rankMap = new EnumMap<>(RankingType.class);
            for (Map.Entry<RankingType, List<Participation>> entry : rankings.entrySet()) {
                int rank = entry.getValue().indexOf(participation) + 1;
                rankMap.put(entry.getKey(), rank);
            }
            
            // 전체 순위 저장
            int overallRank = overallRankings.indexOf(participation) + 1;
            rankMap.put(RankingType.OVERALL, overallRank);
            
            // 순위 정보를 JSON으로 변환하여 저장
            participation.setRankDetails(convertRankMapToJson(rankMap));
            participation.setRank(overallRank);
            participationRepository.save(participation);
        }
    }
    
    private String convertRankMapToJson(Map<RankingType, Integer> rankMap) {
        // 순위 정보를 JSON 문자열로 변환
        StringBuilder json = new StringBuilder("{");
        rankMap.forEach((type, rank) -> {
            json.append("\"").append(type.name()).append("\":").append(rank).append(",");
        });
        json.setLength(json.length() - 1); // 마지막 콤마 제거
        json.append("}");
        return json.toString();
    }
    
    private List<Participation> calculateOverallRankings(
        List<Participation> participants,
        Map<RankingType, List<Participation>> rankings,
        FoodChallengeGoal goal
    ) {
        // 각 순위 유형별 가중치 설정
        Map<RankingType, Double> weights = new EnumMap<>(RankingType.class);
        weights.put(RankingType.ACHIEVEMENT, 0.4); // 목표 달성률
        weights.put(RankingType.EXPENSE, 0.2);     // 지출 효율성
        weights.put(RankingType.DIVERSITY, 0.2);   // 다양성
        
        // 목표 유형별 특화 가중치 설정
        switch (goal.getType()) {
            case DELIVERY_REDUCTION:
                weights.put(RankingType.DELIVERY, 0.2);
                break;
            case DINING_OUT_REDUCTION:
                weights.put(RankingType.DINING_OUT, 0.2);
                break;
            case HOME_COOKING_INCREASE:
                weights.put(RankingType.HOME_COOKING, 0.2);
                break;
        }
        
        // 각 참여자의 종합 점수 계산
        Map<Long, Double> scores = new HashMap<>();
        for (Participation participant : participants) {
            double score = 0.0;
            for (Map.Entry<RankingType, List<Participation>> entry : rankings.entrySet()) {
                int rank = entry.getValue().indexOf(participant) + 1;
                double weight = weights.getOrDefault(entry.getKey(), 0.0);
                // 순위를 점수로 변환 (1등이 가장 높은 점수)
                double rankScore = (double) (participants.size() - rank + 1) / participants.size();
                score += rankScore * weight;
            }
            scores.put(participant.getUser().getId(), score);
        }
        
        // 점수 기준으로 정렬
        return participants.stream()
            .sorted((p1, p2) -> Double.compare(
                scores.get(p2.getUser().getId()),
                scores.get(p1.getUser().getId())
            ))
            .collect(Collectors.toList());
    }
    
    private List<Participation> calculateAchievementRankings(
        List<Participation> participants,
        Map<Long, FoodAnalysisResult> analysisResults
    ) {
        return participants.stream()
            .sorted((p1, p2) -> Double.compare(
                analysisResults.get(p2.getUser().getId()).achievementRate,
                analysisResults.get(p1.getUser().getId()).achievementRate
            ))
            .collect(Collectors.toList());
    }
    
    private List<Participation> calculateExpenseRankings(
        List<Participation> participants,
        Map<Long, FoodAnalysisResult> analysisResults
    ) {
        return participants.stream()
            .sorted((p1, p2) -> Double.compare(
                analysisResults.get(p1.getUser().getId()).additionalMetrics.get("totalExpense"),
                analysisResults.get(p2.getUser().getId()).additionalMetrics.get("totalExpense")
            ))
            .collect(Collectors.toList());
    }
    
    private List<Participation> calculateDiversityRankings(
        List<Participation> participants,
        Map<Long, FoodAnalysisResult> analysisResults
    ) {
        return participants.stream()
            .sorted((p1, p2) -> Double.compare(
                analysisResults.get(p2.getUser().getId()).additionalMetrics.get("categoryDiversity"),
                analysisResults.get(p1.getUser().getId()).additionalMetrics.get("categoryDiversity")
            ))
            .collect(Collectors.toList());
    }
    
    private List<Participation> calculateDeliveryRankings(
        List<Participation> participants,
        Map<Long, FoodAnalysisResult> analysisResults
    ) {
        return participants.stream()
            .sorted((p1, p2) -> Double.compare(
                analysisResults.get(p1.getUser().getId()).additionalMetrics.get("deliveryRatio"),
                analysisResults.get(p2.getUser().getId()).additionalMetrics.get("deliveryRatio")
            ))
            .collect(Collectors.toList());
    }
    
    private List<Participation> calculateDiningOutRankings(
        List<Participation> participants,
        Map<Long, FoodAnalysisResult> analysisResults
    ) {
        return participants.stream()
            .sorted((p1, p2) -> Double.compare(
                analysisResults.get(p1.getUser().getId()).additionalMetrics.get("diningOutRatio"),
                analysisResults.get(p2.getUser().getId()).additionalMetrics.get("diningOutRatio")
            ))
            .collect(Collectors.toList());
    }
    
    private List<Participation> calculateHomeCookingRankings(
        List<Participation> participants,
        Map<Long, FoodAnalysisResult> analysisResults
    ) {
        return participants.stream()
            .sorted((p1, p2) -> Double.compare(
                analysisResults.get(p2.getUser().getId()).additionalMetrics.get("homeCookingRatio"),
                analysisResults.get(p1.getUser().getId()).additionalMetrics.get("homeCookingRatio")
            ))
            .collect(Collectors.toList());
    }
    
    private enum RankingType {
        ACHIEVEMENT,    // 목표 달성률 순위
        EXPENSE,        // 지출 효율성 순위
        DIVERSITY,      // 다양성 순위
        DELIVERY,       // 배달 감소 순위
        DINING_OUT,     // 외식 감소 순위
        HOME_COOKING,   // 홈쿠킹 증가 순위
        OVERALL         // 전체 순위
    }
    
    private FoodChallengeGoal parseChallengeGoal(String description) {
        // 챌린지 설명에서 목표 유형과 파라미터 추출
        FoodChallengeGoal goal = new FoodChallengeGoal();
        
        if (description.contains("일일")) {
            goal.setType(GoalType.DAILY_LIMIT);
            // 일일 한도 금액 추출 (예: "일일 3만원 이내")
            goal.setDailyLimit(extractAmount(description));
        } else if (description.contains("주간")) {
            goal.setType(GoalType.WEEKLY_LIMIT);
            goal.setWeeklyLimit(extractAmount(description));
        } else if (description.contains("월간")) {
            goal.setType(GoalType.MONTHLY_LIMIT);
            goal.setMonthlyLimit(extractAmount(description));
        } else if (description.contains("배달")) {
            goal.setType(GoalType.DELIVERY_REDUCTION);
            goal.setTargetReduction(extractPercentage(description));
        } else if (description.contains("외식")) {
            goal.setType(GoalType.DINING_OUT_REDUCTION);
            goal.setTargetReduction(extractPercentage(description));
        } else if (description.contains("홈쿠킹")) {
            goal.setType(GoalType.HOME_COOKING_INCREASE);
            goal.setTargetIncrease(extractPercentage(description));
        } else {
            // 기본값: 일일 3만원 제한
            goal.setType(GoalType.DAILY_LIMIT);
            goal.setDailyLimit(30000L);
        }
        
        return goal;
    }
    
    private Long extractAmount(String description) {
        // 숫자 추출 로직
        return 30000L; // 기본값
    }
    
    private Double extractPercentage(String description) {
        // 퍼센트 추출 로직
        return 20.0; // 기본값
    }
    
    private double calculateAchievementRate(List<CardTransaction> transactions, FoodChallengeGoal goal) {
        switch (goal.getType()) {
            case DAILY_LIMIT:
                return calculateDailyLimitAchievement(transactions, goal.getDailyLimit());
            case WEEKLY_LIMIT:
                return calculateWeeklyLimitAchievement(transactions, goal.getWeeklyLimit());
            case MONTHLY_LIMIT:
                return calculateMonthlyLimitAchievement(transactions, goal.getMonthlyLimit());
            case DELIVERY_REDUCTION:
                return calculateDeliveryReductionAchievement(transactions, goal.getTargetReduction());
            case DINING_OUT_REDUCTION:
                return calculateDiningOutReductionAchievement(transactions, goal.getTargetReduction());
            case HOME_COOKING_INCREASE:
                return calculateHomeCookingIncreaseAchievement(transactions, goal.getTargetIncrease());
            default:
                return 0.0;
        }
    }
    
    private Map<String, Double> calculateAdditionalMetrics(List<CardTransaction> transactions, FoodChallengeGoal goal) {
        Map<String, Double> metrics = new HashMap<>();
        
        // 기본 지표
        metrics.put("totalExpense", calculateTotalExpense(transactions));
        metrics.put("averageDailyExpense", calculateAverageDailyExpense(transactions));
        metrics.put("categoryDiversity", calculateCategoryDiversity(transactions));
        
        // 목표 유형별 추가 지표
        switch (goal.getType()) {
            case DELIVERY_REDUCTION:
                metrics.put("deliveryRatio", calculateDeliveryRatio(transactions));
                break;
            case DINING_OUT_REDUCTION:
                metrics.put("diningOutRatio", calculateDiningOutRatio(transactions));
                break;
            case HOME_COOKING_INCREASE:
                metrics.put("homeCookingRatio", calculateHomeCookingRatio(transactions));
                break;
        }
        
        return metrics;
    }
    
    private boolean isFoodTransaction(CardTransaction transaction) {
        String category = transaction.getCategory().toLowerCase();
        return category.contains("식비") || 
               category.contains("음식") || 
               category.contains("배달") || 
               category.contains("외식") ||
               category.contains("카페") ||
               category.contains("간식");
    }
    
    private Map<String, Long> analyzeFoodCategories(List<CardTransaction> transactions) {
        Map<String, Long> categoryExpenses = new HashMap<>();
        
        for (CardTransaction transaction : transactions) {
            String category = determineFoodCategory(transaction);
            categoryExpenses.merge(category, transaction.getAmount(), Long::sum);
        }
        
        return categoryExpenses;
    }
    
    private String determineFoodCategory(CardTransaction transaction) {
        String storeName = transaction.getStoreName().toLowerCase();
        String category = transaction.getCategory().toLowerCase();
        
        if (storeName.contains("배달") || category.contains("배달")) {
            return "배달";
        } else if (storeName.contains("카페") || category.contains("카페")) {
            return "카페";
        } else if (storeName.contains("편의점") || category.contains("편의점")) {
            return "편의점";
        } else if (storeName.contains("마트") || category.contains("마트")) {
            return "마트";
        } else {
            return "외식";
        }
    }
    
    private double calculateDailyLimitAchievement(List<CardTransaction> transactions, Long dailyLimit) {
        long totalExpense = transactions.stream()
            .mapToLong(CardTransaction::getAmount)
            .sum();
        return totalExpense <= dailyLimit ? 1.0 : 0.0;
    }
    
    private double calculateWeeklyLimitAchievement(List<CardTransaction> transactions, Long weeklyLimit) {
        long totalExpense = transactions.stream()
            .mapToLong(CardTransaction::getAmount)
            .sum();
        return totalExpense <= weeklyLimit ? 1.0 : 0.0;
    }
    
    private double calculateMonthlyLimitAchievement(List<CardTransaction> transactions, Long monthlyLimit) {
        long totalExpense = transactions.stream()
            .mapToLong(CardTransaction::getAmount)
            .sum();
        return totalExpense <= monthlyLimit ? 1.0 : 0.0;
    }
    
    private double calculateDeliveryReductionAchievement(List<CardTransaction> transactions, Double targetReduction) {
        long totalExpense = transactions.stream()
            .mapToLong(CardTransaction::getAmount)
            .sum();
        double reductionRatio = (double) totalExpense / transactions.size();
        return reductionRatio <= targetReduction ? 1.0 : 0.0;
    }
    
    private double calculateDiningOutReductionAchievement(List<CardTransaction> transactions, Double targetReduction) {
        long diningOutExpense = transactions.stream()
            .filter(transaction -> isDiningOutTransaction(transaction))
            .mapToLong(CardTransaction::getAmount)
            .sum();
        double reductionRatio = (double) diningOutExpense / transactions.size();
        return reductionRatio <= targetReduction ? 1.0 : 0.0;
    }
    
    private double calculateHomeCookingIncreaseAchievement(List<CardTransaction> transactions, Double targetIncrease) {
        long homeCookingExpense = transactions.stream()
            .filter(transaction -> isHomeCookingTransaction(transaction))
            .mapToLong(CardTransaction::getAmount)
            .sum();
        double increaseRatio = (double) homeCookingExpense / transactions.size();
        return increaseRatio >= targetIncrease ? 1.0 : 0.0;
    }
    
    private double calculateTotalExpense(List<CardTransaction> transactions) {
        return transactions.stream()
            .mapToLong(CardTransaction::getAmount)
            .sum();
    }
    
    private double calculateAverageDailyExpense(List<CardTransaction> transactions) {
        long totalExpense = transactions.stream()
            .mapToLong(CardTransaction::getAmount)
            .sum();
        long totalDays = java.time.temporal.ChronoUnit.DAYS.between(
            transactions.get(0).getTransactionDate(), 
            transactions.get(transactions.size() - 1).getTransactionDate()
        ) + 1;
        return (double) totalExpense / totalDays;
    }
    
    private double calculateCategoryDiversity(List<CardTransaction> transactions) {
        return transactions.stream()
            .map(CardTransaction::getCategory)
            .distinct()
            .count();
    }
    
    private double calculateDeliveryRatio(List<CardTransaction> transactions) {
        long deliveryExpense = transactions.stream()
            .filter(transaction -> isDeliveryTransaction(transaction))
            .mapToLong(CardTransaction::getAmount)
            .sum();
        long totalExpense = transactions.stream()
            .mapToLong(CardTransaction::getAmount)
            .sum();
        return (double) deliveryExpense / totalExpense;
    }
    
    private double calculateDiningOutRatio(List<CardTransaction> transactions) {
        long diningOutExpense = transactions.stream()
            .filter(transaction -> isDiningOutTransaction(transaction))
            .mapToLong(CardTransaction::getAmount)
            .sum();
        long totalExpense = transactions.stream()
            .mapToLong(CardTransaction::getAmount)
            .sum();
        return (double) diningOutExpense / totalExpense;
    }
    
    private double calculateHomeCookingRatio(List<CardTransaction> transactions) {
        long homeCookingExpense = transactions.stream()
            .filter(transaction -> isHomeCookingTransaction(transaction))
            .mapToLong(CardTransaction::getAmount)
            .sum();
        long totalExpense = transactions.stream()
            .mapToLong(CardTransaction::getAmount)
            .sum();
        return (double) homeCookingExpense / totalExpense;
    }
    
    private boolean isDiningOutTransaction(CardTransaction transaction) {
        String category = transaction.getCategory().toLowerCase();
        return category.contains("외식") || category.contains("카페") || category.contains("간식");
    }
    
    private boolean isHomeCookingTransaction(CardTransaction transaction) {
        String category = transaction.getCategory().toLowerCase();
        return category.contains("홈쿠킹");
    }
    
    private boolean isDeliveryTransaction(CardTransaction transaction) {
        String storeName = transaction.getStoreName().toLowerCase();
        String category = transaction.getCategory().toLowerCase();
        return storeName.contains("배달") || 
               category.contains("배달") || 
               storeName.contains("배민") || 
               storeName.contains("요기요") || 
               storeName.contains("쿠팡이츠");
    }
    
    @Data
    private static class FoodAnalysisResult {
        private final Map<String, Long> categoryExpenses;
        private final double achievementRate;
        private final Map<String, Double> additionalMetrics;

        public FoodAnalysisResult(
            Map<String, Long> categoryExpenses,
            double achievementRate,
            Map<String, Double> additionalMetrics
        ) {
            this.categoryExpenses = categoryExpenses;
            this.achievementRate = achievementRate;
            this.additionalMetrics = additionalMetrics;
        }
    }
    
    @Data
    private static class FoodChallengeGoal {
        private GoalType type;
        private Long dailyLimit;
        private Long weeklyLimit;
        private Long monthlyLimit;
        private Double targetReduction;
        private Double targetIncrease;
    }
    
    private enum GoalType {
        DAILY_LIMIT,          // 일일 지출 제한
        WEEKLY_LIMIT,         // 주간 지출 제한
        MONTHLY_LIMIT,        // 월간 지출 제한
        DELIVERY_REDUCTION,   // 배달 주문 감소
        DINING_OUT_REDUCTION, // 외식 감소
        HOME_COOKING_INCREASE // 홈쿠킹 증가
    }

    private void analyzeCafeSnack(Challenge challenge) {
        log.info("[카페/간식 분석] 시작 - 챌린지 ID: {}", challenge.getChallengeID());
        // TODO: 카페/간식 분석 로직 구현
        // 1. 카페 방문 빈도 분석
        // 2. 선호 메뉴 패턴 분석
        // 3. 시간대별 소비 패턴 분석
    }

    private void analyzeTravel(Challenge challenge) {
        log.info("[여행 지출 분석] 시작 - 챌린지 ID: {}", challenge.getChallengeID());
        // TODO: 여행 지출 분석 로직 구현
        // 1. 여행 기간별 지출 분석
        // 2. 교통/숙박/식비/기타 비용 분류
        // 3. 지역별 지출 패턴 분석
    }

    private void analyzePet(Challenge challenge) {
        log.info("[반려동물 지출 분석] 시작 - 챌린지 ID: {}", challenge.getChallengeID());
        // TODO: 반려동물 지출 분석 로직 구현
        // 1. 사료/간식 지출 분석
        // 2. 의료비 지출 분석
        // 3. 용품 구매 패턴 분석
    }

    private void analyzeMartConvenience(Challenge challenge) {
        log.info("[마트/편의점 분석] 시작 - 챌린지 ID: {}", challenge.getChallengeID());
        // TODO: 마트/편의점 지출 분석 로직 구현
        // 1. 방문 빈도 분석
        // 2. 주요 구매 품목 분석
        // 3. 대형마트 vs 편의점 지출 비교
    }

    private void analyzeTransportation(Challenge challenge) {
        log.info("[교통 지출 분석] 시작 - 챌린지 ID: {}", challenge.getChallengeID());
        // TODO: 교통 지출 분석 로직 구현
        // 1. 대중교통 vs 택시 이용 비율
        // 2. 시간대별 이용 패턴
        // 3. 주요 이동 구간 분석
    }

    private void analyzeShopping(Challenge challenge) {
        log.info("[쇼핑 지출 분석] 시작 - 챌린지 ID: {}", challenge.getChallengeID());
        // TODO: 쇼핑 지출 분석 로직 구현
        // 1. 온라인 vs 오프라인 쇼핑 비율
        // 2. 품목별 지출 분석
        // 3. 계절별 구매 패턴 분석
    }

    private void analyzeBeauty(Challenge challenge) {
        log.info("[미용 지출 분석] 시작 - 챌린지 ID: {}", challenge.getChallengeID());
        // TODO: 미용 지출 분석 로직 구현
        // 1. 화장품 vs 시술 지출 비율
        // 2. 정기적 관리 항목 분석
        // 3. 브랜드별 선호도 분석
    }
} 