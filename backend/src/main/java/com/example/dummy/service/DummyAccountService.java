package com.example.dummy.service;

import com.example.dummy.dto.AccountSummaryResponse.*;
import com.example.dummy.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DummyAccountService {

    private final BankBalanceRepository bankBalanceRepository;
    private final CardSpentRepository cardSpentRepository;
    private final InvestmentRecordRepository investmentRecordRepository;
    private final InsuranceAccountRepository insuranceAccountRepository;
    private final LoanAccountRepository loanAccountRepository;

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
} 