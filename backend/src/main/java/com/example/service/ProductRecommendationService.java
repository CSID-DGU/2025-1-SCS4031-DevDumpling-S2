package com.example.service;

import com.example.dummy.entity.*;
import com.example.dummy.repository.*;
import com.example.entity.User;
import com.example.entity.User.UserType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductRecommendationService {

    private final CardSpentRepository cardSpentRepository;
    private final BankBalanceRepository bankBalanceRepository;
    private final InsuranceAccountRepository insuranceAccountRepository;
    private final LoanAccountRepository loanAccountRepository;

    @Transactional(readOnly = true)
    public Map<String, List<?>> getRecommendedProducts(User user) {
        // 사용자 타입이 null이면 B로 설정
        UserType userType = user.getUserType() != null ? user.getUserType() : UserType.B;
        
        List<CardSpent> cards = recommendCards(user.getId(), userType);
        List<BankBalance> banks = recommendBanks(user.getId(), userType);
        List<InsuranceAccount> insurances = recommendInsurances(user.getId(), userType);
        List<LoanAccount> loans = recommendLoans(user.getId(), userType);

        return Map.of(
            "cards", cards,
            "banks", banks,
            "insurances", insurances,
            "loans", loans
        );
    }

    private List<CardSpent> recommendCards(Long userId, UserType userType) {
        List<CardSpent> userCards = cardSpentRepository.findByUserId(userId);
        
        return userCards.stream()
            .filter(card -> {
                switch (userType) {
                    case A: // 도전형
                        return card.getCardType().equals("신용카드") && 
                               card.getMonthlyBillAmount() > 1000000;
                    case B: // 안정형
                        return card.getCardType().equals("체크카드") && 
                               card.getMonthlyBillAmount() < 500000;
                    case C: // 성장형
                        return card.getCardType().equals("신용카드") && 
                               card.getMonthlyBillAmount() > 500000;
                    case D: // 보수형
                        return card.getCardType().equals("체크카드") && 
                               card.getMonthlyBillAmount() < 300000;
                    default:
                        return true;
                }
            })
            .collect(Collectors.toList());
    }

    private List<BankBalance> recommendBanks(Long userId, UserType userType) {
        List<BankBalance> userBanks = bankBalanceRepository.findByUserId(userId);
        
        return userBanks.stream()
            .filter(bank -> {
                switch (userType) {
                    case A: // 도전형
                        return bank.getBalance() > 5000000;
                    case B: // 안정형
                        return bank.getBalance() > 1000000 && bank.getBalance() < 5000000;
                    case C: // 성장형
                        return bank.getBalance() > 3000000;
                    case D: // 보수형
                        return bank.getBalance() < 2000000;
                    default:
                        return true;
                }
            })
            .collect(Collectors.toList());
    }

    private List<InsuranceAccount> recommendInsurances(Long userId, UserType userType) {
        List<InsuranceAccount> userInsurances = insuranceAccountRepository.findByUserId(userId);
        
        return userInsurances.stream()
            .filter(insurance -> {
                switch (userType) {
                    case A: // 도전형
                        return insurance.getInsuranceType().equals("INVESTMENT") && 
                               insurance.getInsuredAmount() > 50000000;
                    case B: // 안정형
                        return insurance.getInsuranceType().equals("MEDICAL") && 
                               insurance.getInsuredAmount() > 30000000;
                    case C: // 성장형
                        return insurance.getInsuranceType().equals("LIFE") && 
                               insurance.getInsuredAmount() > 40000000;
                    case D: // 보수형
                        return insurance.getInsuranceType().equals("MEDICAL") && 
                               insurance.getInsuredAmount() < 30000000;
                    default:
                        return true;
                }
            })
            .collect(Collectors.toList());
    }

    private List<LoanAccount> recommendLoans(Long userId, UserType userType) {
        List<LoanAccount> userLoans = loanAccountRepository.findByUserId(userId);
        
        return userLoans.stream()
            .filter(loan -> {
                switch (userType) {
                    case A: // 도전형
                        return loan.getLoanType().equals("CREDIT") && 
                               loan.getInterestRate() < 8.0;
                    case B: // 안정형
                        return loan.getLoanType().equals("MORTGAGE") && 
                               loan.getInterestRate() < 6.0;
                    case C: // 성장형
                        return loan.getLoanType().equals("CREDIT") && 
                               loan.getInterestRate() < 7.0;
                    case D: // 보수형
                        return loan.getLoanType().equals("RENT") && 
                               loan.getInterestRate() < 5.0;
                    default:
                        return true;
                }
            })
            .collect(Collectors.toList());
    }
} 