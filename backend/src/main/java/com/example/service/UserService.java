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

import java.util.Collections;
import java.util.Random;
import java.util.List;

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

    public User findByKakaoId(String kakaoId) {
        return userRepository.findByKakaoId(kakaoId)
                .orElseThrow(() -> new RuntimeException("User not found"));
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
                .userType(User.UserType.A)
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
} 