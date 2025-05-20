package com.example.controller;

import com.example.dummy.entity.*;
import com.example.dummy.repository.*;
import com.example.entity.User;
import com.example.service.UserService;
import com.example.service.ProductRecommendationService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final BankBalanceRepository bankBalanceRepository;
    private final BankTransactionRepository bankTransactionRepository;
    private final CardSpentRepository cardSpentRepository;
    private final CardTransactionRepository cardTransactionRepository;
    private final InvestmentRecordRepository investmentRecordRepository;
    private final InvestmentTransactionRepository investmentTransactionRepository;
    private final ProductRecommendationService productRecommendationService;

    @GetMapping("/mydata-consent")
    public ResponseEntity<?> checkMyDataConsent(Authentication authentication) {
        try {
            boolean hasConsent = userService.checkMyDataConsent(authentication.getName());
            return ResponseEntity.ok(Map.of("hasConsent", hasConsent));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/mydata-consent")
    public ResponseEntity<?> updateMyDataConsent(
            Authentication authentication,
            @RequestBody MyDataConsentRequest request) {
        try {
            User user = userService.findByKakaoId(authentication.getName());
            
            // 마이데이터 동의가 false에서 true로 변경되는 경우에만 더미 데이터 생성
            if (!user.isMyDataConsent() && request.isConsent()) {
                user.setMyDataConsent(true);
                userService.save(user);
                userService.generateDummyDataForConsentedUser(user.getId());
            } else {
                user.setMyDataConsent(request.isConsent());
                userService.save(user);
            }
            
            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Data
    public static class MyDataConsentRequest {
        private boolean consent;
    }

    @GetMapping("/{userId}/dummy-data")
    public ResponseEntity<?> checkDummyData(@PathVariable Long userId) {
        // 은행 데이터 확인
        List<BankBalance> bankBalances = bankBalanceRepository.findByUserId(userId);
        List<BankTransaction> bankTransactions = bankTransactionRepository.findByUserId(userId);
        
        // 카드 데이터 확인
        List<CardSpent> cardSpents = cardSpentRepository.findByUserId(userId);
        List<CardTransaction> cardTransactions = cardTransactionRepository.findByUserId(userId);
        
        // 투자 데이터 확인
        List<InvestmentRecord> investments = investmentRecordRepository.findByUserId(userId);
        List<InvestmentTransaction> investmentTransactions = investmentTransactionRepository.findByUserId(userId);
        
        return ResponseEntity.ok(Map.of(
            "bank", Map.of(
                "accounts", bankBalances.size(),
                "transactions", bankTransactions.size()
            ),
            "card", Map.of(
                "cards", cardSpents.size(),
                "transactions", cardTransactions.size()
            ),
            "investment", Map.of(
                "accounts", investments.size(),
                "transactions", investmentTransactions.size()
            )
        ));
    }

    @GetMapping("/user-type")
    public ResponseEntity<?> getUserType(Authentication authentication) {
        User user = userService.findByKakaoId(authentication.getName());
        User.UserType userType = user.getUserType();
        if (userType == null) {
            userType = userService.determineAndSaveUserType(user);
        }
        return ResponseEntity.ok(Map.of("userType", userType));
    }

    @GetMapping("/recommendations")
    public ResponseEntity<?> getRecommendedProducts(Authentication authentication) {
        try {
            User user = userService.findByKakaoId(authentication.getName());
            Map<String, List<?>> recommendations = productRecommendationService.getRecommendedProducts(user);
            return ResponseEntity.ok(recommendations);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
} 