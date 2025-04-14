package com.example.controller;

import com.example.dummy.entity.*;
import com.example.dummy.repository.*;
import com.example.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final BankBalanceRepository bankBalanceRepository;
    private final BankTransactionRepository bankTransactionRepository;
    private final CardSpentRepository cardSpentRepository;
    private final CardTransactionRepository cardTransactionRepository;
    private final InvestmentRecordRepository investmentRecordRepository;
    private final InvestmentTransactionRepository investmentTransactionRepository;

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
} 