package com.example.service;

import com.example.dummy.service.BankDummyDataService;
import com.example.dummy.service.CardDummyDataService;
import com.example.dummy.service.InvestmentDummyDataService;
import com.example.dummy.service.InsuranceDummyDataService;
import com.example.dummy.service.LoanDummyDataService;
import com.example.entity.User;
import com.example.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Random;

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
    private final Random random = new Random();

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

        // 더미 데이터 생성
        generateDummyData(user.getId());

        return user;
    }

    @Transactional
    public void syncFinanceData() {
        // 은행 정보 동기화
        financeProductService.syncAllCompanies("020000");
        financeProductService.syncAllDepositProducts("020000");
        financeProductService.syncAllSavingProducts("020000");
    }

    @Transactional
    public void generateDummyData(Long userId) {
        // 은행 계좌 1~3개 생성
        int bankCount = 1 + random.nextInt(3);
        for (int i = 0; i < bankCount; i++) {
            bankDummyDataService.createBankBalance(userId);
        }

        // 카드 2~3개 생성
        int cardCount = 2 + random.nextInt(2);
        for (int i = 0; i < cardCount; i++) {
            cardDummyDataService.createCardSpent(userId);
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

    public boolean checkMyDataConsent(String kakaoId) {
        User user = findByKakaoId(kakaoId);
        return user.isMyDataConsent();
    }
} 