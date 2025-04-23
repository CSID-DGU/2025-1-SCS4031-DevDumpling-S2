package com.example.dummy.service;

import com.example.dummy.dto.AccountSummaryResponse.*;
import com.example.dummy.repository.*;
import com.example.dummy.entity.BankTransaction;
import com.example.dummy.entity.BankBalance;
import com.example.dummy.util.DummyDataGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DummyAccountService {

    private final BankBalanceRepository bankBalanceRepository;
    private final BankTransactionRepository bankTransactionRepository;
    private final CardSpentRepository cardSpentRepository;
    private final InvestmentRecordRepository investmentRecordRepository;
    private final InsuranceAccountRepository insuranceAccountRepository;
    private final LoanAccountRepository loanAccountRepository;
    private final BankDummyDataService bankDummyDataService;
    private final Random random = new Random();

    @Transactional(readOnly = true)
    public List<BankAccountDto> getBankAccounts(Long userId) {
        return bankBalanceRepository.findByUserId(userId).stream()
                .map(bank -> BankAccountDto.builder()
                        .accountNumber(bank.getAccountNumber())
                        .bankName(bank.getBankName())
                        .accountName(bank.getBankName())
                        .balance(bank.getBalance())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CardAccountDto> getCardAccounts(Long userId) {
        return cardSpentRepository.findByUserId(userId).stream()
                .map(card -> CardAccountDto.builder()
                        .cardId(card.getCardId())
                        .cardName(card.getCardName())
                        .cardType(card.getCardType())
                        .monthlyBillAmount(card.getMonthlyBillAmount())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<InvestmentAccountDto> getInvestmentAccounts(Long userId) {
        return investmentRecordRepository.findByUserId(userId).stream()
                .map(investment -> InvestmentAccountDto.builder()
                        .accountNumber(investment.getAccountNumber())
                        .accountName(investment.getAccountName())
                        .investmentType(investment.getInvestmentType())
                        .balance(investment.getBalance())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<InsuranceAccountDto> getInsuranceAccounts(Long userId) {
        return insuranceAccountRepository.findByUserId(userId).stream()
                .map(insurance -> InsuranceAccountDto.builder()
                        .insuranceId(insurance.getInsuranceId())
                        .productName(insurance.getProductName())
                        .insuranceType(insurance.getInsuranceType().name())
                        .insuredAmount(insurance.getInsuredAmount())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<LoanAccountDto> getLoanAccounts(Long userId) {
        return loanAccountRepository.findByUserId(userId).stream()
                .map(loan -> LoanAccountDto.builder()
                        .loanNumber(loan.getLoanId())
                        .productName(loan.getProductName())
                        .loanType(loan.getLoanType().name())
                        .loanAmount(loan.getPrincipalAmount())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional
    public void processSelectedBankAccounts(Long userId, List<String> selectedAccountNumbers) {
        // 선택된 각 계좌에 대해 isActive 상태 업데이트 및 거래내역 생성
        for (String accountNumber : selectedAccountNumbers) {
            // 계좌 존재 여부 확인
            BankBalance bankBalance = bankBalanceRepository.findByUserIdAndAccountNumber(userId, accountNumber)
                    .orElseThrow(() -> new RuntimeException("존재하지 않는 계좌입니다: " + accountNumber));

            // 계좌 활성화
            bankBalance.setIsActive(true);
            bankBalanceRepository.save(bankBalance);

            // 활성화된 계좌에 대해 거래내역 생성
            bankDummyDataService.generateBankTransactions(userId, accountNumber);
        }
    }
} 