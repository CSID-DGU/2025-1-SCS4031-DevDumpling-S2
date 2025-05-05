package com.example.dummy.service;

import com.example.dummy.dto.AccountSummaryResponse.*;
import com.example.dummy.repository.*;
import com.example.dummy.entity.BankTransaction;
import com.example.dummy.entity.BankBalance;
import com.example.dummy.entity.LoanAccount;
import com.example.dummy.entity.InvestmentRecord;
import com.example.dummy.util.DummyDataGenerator;
import com.example.service.FinanceProductService;
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
    private final LoanDummyDataService loanDummyDataService;
    private final InvestmentDummyDataService investmentDummyDataService;
    private final FinanceProductService financeProductService;
    private final Random random = new Random();

    @Transactional(readOnly = true)
    public List<BankAccountDto> getBankAccounts(Long userId) {
        return bankBalanceRepository.findByUserId(userId).stream()
                .map(balance -> BankAccountDto.builder()
                        .accountNumber(balance.getAccountNumber())
                        .bankName(balance.getBankName())
                        .accountName(balance.getAccountType())
                        .balance(balance.getBalance())
                        .bankImage(financeProductService.getBankImage(balance.getBankName()))
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
                        .cardImage("https://myapp-logos.s3.ap-northeast-2.amazonaws.com/bank-logos/default.png")
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<InvestmentAccountDto> getInvestmentAccounts(Long userId) {
        List<InvestmentRecord> records = investmentRecordRepository.findByUserId(userId);
        return records.stream()
                .map(record -> InvestmentAccountDto.builder()
                        .accountNumber(record.getAccountNumber())
                        .accountName(record.getAccountName())
                        .investmentType(record.getInvestmentType())
                        .balance(record.getBalance())
                        .bankImage(financeProductService.getBankImage(record.getInvestmentType().equals("ETF") ? 
                                "ETF" : "주식"))
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
                        .bankName(loan.getKorCoNm())
                        .bankImage(financeProductService.getBankImage(loan.getKorCoNm()))
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

    @Transactional
    public void revokeAccountConsent(Long userId, String accountNumber) {
        // 계좌 존재 여부 확인
        BankBalance bankBalance = bankBalanceRepository.findByUserIdAndAccountNumber(userId, accountNumber)
                .orElseThrow(() -> new IllegalArgumentException("해당 계좌를 찾을 수 없습니다."));

        // 이미 동의하지 않은 계좌인 경우
        if (!bankBalance.getIsActive()) {
            throw new IllegalArgumentException("이미 동의가 철회된 계좌입니다.");
        }

        // 계좌 비활성화
        bankBalance.setIsActive(false);
        bankBalanceRepository.save(bankBalance);

        // 해당 계좌의 거래내역 삭제
        bankTransactionRepository.deleteByAccountNumber(accountNumber);
    }

    @Transactional
    public void processSelectedLoans(Long userId, List<String> selectedLoanIds) {
        for (String loanId : selectedLoanIds) {
            loanDummyDataService.generateLoanTransactions(userId, loanId);
        }
    }

    @Transactional
    public void processSelectedInvestments(Long userId, List<String> selectedAccountNumbers) {
        for (String accountNumber : selectedAccountNumbers) {
            investmentDummyDataService.generateInvestmentTransactions(userId, accountNumber);
        }
    }

    @Transactional
    public void revokeInvestmentConsent(Long userId, String accountNumber) {
        investmentDummyDataService.revokeInvestmentConsent(userId, accountNumber);
    }

    @Transactional
    public void revokeLoanConsent(Long userId, String loanId) {
        loanDummyDataService.revokeLoanConsent(userId, loanId);
    }
} 