package com.example.service;

import com.example.dummy.service.BankDummyDataService;
import com.example.dummy.service.CardDummyDataService;
import com.example.dummy.service.InvestmentDummyDataService;
import com.example.dummy.service.InsuranceDummyDataService;
import com.example.dummy.service.LoanDummyDataService;
import com.example.entity.User;
import com.example.entity.CheckCard;
import com.example.entity.CreditCard;
import com.example.repository.UserRepository;
import com.example.repository.CheckCardRepository;
import com.example.repository.CreditCardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import com.example.dummy.repository.BankTransactionRepository;
import com.example.dummy.repository.InvestmentRecordRepository;
import com.example.dummy.repository.CardTransactionRepository;
import com.example.dummy.repository.BankBalanceRepository;
import com.example.dummy.repository.InvestmentTransactionRepository;
import com.example.dummy.repository.InsuranceAccountRepository;

import java.util.Collections;
import java.util.Random;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final BankDummyDataService bankDummyDataService;
    private final CardDummyDataService cardDummyDataService;
    private final InvestmentDummyDataService investmentDummyDataService;
    private final InsuranceDummyDataService insuranceDummyDataService;
    private final LoanDummyDataService loanDummyDataService;
    private final FinanceProductService financeProductService;
    private final CheckCardRepository checkCardRepository;
    private final CreditCardRepository creditCardRepository;
    private final Random random = new Random();
    private final EtfService etfService;
    private final StockService stockService;
    private final RestTemplate restTemplate = new RestTemplate();
    private final BankTransactionRepository bankTransactionRepository;
    private final InvestmentRecordRepository investmentRecordRepository;
    private final CardTransactionRepository cardTransactionRepository;
    private final BankBalanceRepository bankBalanceRepository;
    private final InsuranceAccountRepository insuranceAccountRepository;
    private final InvestmentTransactionRepository investmentTransactionRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByKakaoId(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with kakao id: " + username));

        return new org.springframework.security.core.userdetails.User(
                user.getKakaoId(),
                user.getKakaoId(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }

    @Transactional(readOnly = true)
    public User findByKakaoId(String kakaoId) {
        User user = userRepository.findByKakaoId(kakaoId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        // Lazy Loading 컬렉션을 명시적으로 초기화
        user.getScrappedArticles().size();
        return user;
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    @Transactional
    public User createUser(String kakaoId, String nickname, String profileImage, boolean myDataConsent) {
        // 이미 존재하는 사용자인지 확인
        if (userRepository.existsByKakaoId(kakaoId)) {
            throw new RuntimeException("이미 존재하는 사용자입니다.");
        }

        // 새로운 사용자 생성
        User user = User.builder()
                .kakaoId(kakaoId)
                .nickname(nickname)
                .profileImage(profileImage)
                .myDataConsent(myDataConsent)
                .userType(null)
                .build();
        user = userRepository.save(user);

        // FSS API에서 은행 정보 동기화
        syncFinanceData();

        // 마이데이터 동의가 true인 경우에만 더미 데이터 생성
        if (myDataConsent) {
            generateDummyData(user.getId());
        }

        return user;
    }

    @Transactional
    public void syncFinanceData() {
        // 은행/예금/적금/대출/주식/ETF 상품 동기화는 빌드(서버 시작) 시에만 수행
        // 회원가입 시에는 별도 동기화 없이 DB 싱크만 맞춤 (필요시 DB에 상품 없을 때만 예외 처리)
    }

    @Transactional
    public void generateDummyData(Long userId) {
        // 은행 계좌 1~3개 생성
        int bankCount = 1 + random.nextInt(3);
        for (int i = 0; i < bankCount; i++) {
            bankDummyDataService.createBankBalance(userId);
        }

        // 체크카드 1~5개 생성
        List<CheckCard> checkCards = checkCardRepository.findAll();
        int checkCardCount = Math.min(1 + random.nextInt(5), checkCards.size());
        for (int i = 0; i < checkCardCount; i++) {
            CheckCard selectedCard = checkCards.get(random.nextInt(checkCards.size()));
            cardDummyDataService.createCardSpent(userId, selectedCard);
        }

        // 신용카드 0~2개 생성
        List<CreditCard> creditCards = creditCardRepository.findAll();
        int creditCardCount = Math.min(random.nextInt(3), creditCards.size());
        for (int i = 0; i < creditCardCount; i++) {
            CreditCard selectedCard = creditCards.get(random.nextInt(creditCards.size()));
            cardDummyDataService.createCardSpent(userId, selectedCard);
        }

        // 투자 계좌 1~2개 생성
        int investmentCount = 1 + random.nextInt(2);
        for (int i = 0; i < investmentCount; i++) {
            investmentDummyDataService.createInvestmentRecord(userId);
        }

        // 보험 계좌 1~3개 생성
        int insuranceCount = 1 + random.nextInt(3);
        for (int i = 0; i < insuranceCount; i++) {
            insuranceDummyDataService.createInsuranceAccount(userId);
        }

        // 대출 계좌 1~2개 생성
        int loanCount = 1 + random.nextInt(2);
        for (int i = 0; i < loanCount; i++) {
            boolean isShortTerm = random.nextBoolean();
            loanDummyDataService.createLoanAccount(userId, isShortTerm);
        }
    }

    @Transactional
    public void generateDummyDataForConsentedUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.isMyDataConsent()) {
            generateDummyData(userId);
        } else {
            throw new RuntimeException("마이데이터 동의가 필요합니다.");
        }
    }

    public boolean checkMyDataConsent(String kakaoId) {
        User user = findByKakaoId(kakaoId);
        return user.isMyDataConsent();
    }

    /**
     * 개선된 스코어링 기반 금융성향 4분류 로직
     * - 각 항목을 Z-score로 정규화하여 상대적 비교
     * - 투자 성향에 더 높은 가중치 부여
     * - 소비와 저축 간 균형 고려
     */
    @Transactional
    public User.UserType determineAndSaveUserType(User user) {
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        java.time.LocalDateTime oneMonthAgo = now.minusMonths(1);
        Long userId = user.getId();

        // 1. 기본 데이터 수집
        // 1.1 신용카드 사용액(최근 1개월)
        long cardSpent = cardTransactionRepository.findByUserIdAndTransactionDateBetween(userId, oneMonthAgo.toLocalDate(), now.toLocalDate())
            .stream().filter(t -> t.getAmount() < 0).mapToLong(t -> Math.abs(t.getAmount())).sum();

        // 1.2 투자 계좌 및 거래 데이터
        var investmentRecords = investmentRecordRepository.findByUserId(userId);
        int investmentCount = investmentRecords.size();
        long investmentTxCount = investmentRecords.stream()
            .mapToLong(record -> investmentTransactionRepository.findByAccountNumberAndTransactionDateBetween(
                record.getAccountNumber(), oneMonthAgo.toLocalDate(), now.toLocalDate()).size()).sum();
        long totalInvestmentAmount = investmentRecords.stream()
            .mapToLong(record -> record.getBalance()).sum();

        // 1.3 은행 거래 데이터
        long bankSpent = bankTransactionRepository.findByUserIdAndTransactionDateBetween(userId, oneMonthAgo, now)
            .stream().filter(t -> t.getAmount() < 0).mapToLong(t -> Math.abs(t.getAmount())).sum();
        long totalSpent = bankSpent + cardSpent;

        // 1.4 예적금 잔액
        long savingsBalance = bankBalanceRepository.findByUserId(userId).stream()
            .filter(b -> b.getAccountType() != null && (b.getAccountType().contains("예금") || b.getAccountType().contains("적금")))
            .mapToLong(b -> b.getBalance() != null ? b.getBalance() : 0L).sum();

        // 1.5 보험 계좌
        int insuranceCount = insuranceAccountRepository.findByUserId(userId).size();

        // 2. 전체 사용자 데이터 수집 (정규화를 위해)
        List<Long> allCardSpent = cardTransactionRepository.findAll().stream()
            .filter(t -> t.getAmount() < 0)
            .map(t -> Math.abs(t.getAmount()))
            .collect(Collectors.toList());

        List<Long> allInvestmentAmounts = investmentRecordRepository.findAll().stream()
            .map(record -> record.getBalance())
            .collect(Collectors.toList());

        List<Long> allSavingsBalances = bankBalanceRepository.findAll().stream()
            .filter(b -> b.getAccountType() != null && (b.getAccountType().contains("예금") || b.getAccountType().contains("적금")))
            .map(b -> b.getBalance() != null ? b.getBalance() : 0L)
            .collect(Collectors.toList());

        // 3. Z-score 계산
        double cardSpentZScore = calculateZScore(cardSpent, allCardSpent);
        double investmentAmountZScore = calculateZScore(totalInvestmentAmount, allInvestmentAmounts);
        double savingsBalanceZScore = calculateZScore(savingsBalance, allSavingsBalances);

        // 4. 투자 성향 점수 (가중치 0.4)
        double investmentScore = (
            normalize(investmentCount, 0, 10) * 0.3 +  // 투자 계좌 수
            normalize(investmentTxCount, 0, 100) * 0.3 +  // 투자 거래 빈도
            normalize(investmentAmountZScore, -3, 3) * 0.4  // 투자 금액
        ) * 0.4;

        // 5. 소비 성향 점수 (가중치 0.3)
        double consumptionScore = (
            normalize(cardSpentZScore, -3, 3) * 0.6 +  // 신용카드 사용
            normalize(bankSpent, 0, 10000000) * 0.4  // 은행 거래
        ) * 0.3;

        // 6. 저축 성향 점수 (가중치 0.3)
        double savingsScore = (
            normalize(savingsBalanceZScore, -3, 3) * 0.7 +  // 예적금 잔액
            normalize(insuranceCount, 0, 5) * 0.3  // 보험 계약 수
        ) * 0.3;

        // 7. 최종 점수 계산
        double finalScore = investmentScore + consumptionScore + savingsScore;

        // 8. 유형 결정
        User.UserType type;
        if (finalScore >= 0.7)      type = User.UserType.A; // 도전러
        else if (finalScore >= 0.4) type = User.UserType.B; // 계획러
        else if (finalScore >= 0.2) type = User.UserType.C; // 편안러
        else                        type = User.UserType.D; // 안심러

        user.setUserType(type);
        userRepository.save(user);
        return type;
    }

    private double calculateZScore(double value, List<Long> allValues) {
        if (allValues.isEmpty()) return 0;
        
        double mean = allValues.stream()
            .mapToDouble(Long::doubleValue)
            .average()
            .orElse(0.0);
            
        double stdDev = Math.sqrt(allValues.stream()
            .mapToDouble(v -> Math.pow(v - mean, 2))
            .average()
            .orElse(0.0));
            
        return stdDev == 0 ? 0 : (value - mean) / stdDev;
    }

    private double normalize(double value, double min, double max) {
        return (value - min) / (max - min);
    }
} 