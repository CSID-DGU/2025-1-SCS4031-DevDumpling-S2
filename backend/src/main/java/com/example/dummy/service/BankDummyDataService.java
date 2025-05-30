package com.example.dummy.service;

import com.example.dummy.entity.BankBalance;
import com.example.dummy.entity.BankTransaction;
import com.example.dummy.repository.BankBalanceRepository;
import com.example.dummy.repository.BankTransactionRepository;
import com.example.dummy.util.DummyDataGenerator;
import com.example.service.FinanceProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class BankDummyDataService {

    private final BankBalanceRepository bankBalanceRepository;
    private final BankTransactionRepository bankTransactionRepository;
    private final FinanceProductService financeProductService;
    private final Random random = new Random();

    @Transactional
    public void generateDummyData(Long userId) {
        // 은행 계좌 생성
        BankBalance bankBalance = createBankBalance(userId);
        generateBankTransactions(userId, bankBalance.getAccountNumber());
    }

    @Transactional
    public BankBalance createBankBalance(Long userId) {
        // FSS API에서 가져온 은행 정보를 사용하여 계좌 생성
        String bankName = financeProductService.getRandomBankName();
        String productName = financeProductService.getRandomDepositProductName();
        
        BankBalance balance = new BankBalance();
        balance.setUserId(userId);
        balance.setBankName(bankName);
        balance.setAccountNumber(DummyDataGenerator.generateAccountNumber());
        balance.setAccountType(productName);
        balance.setBalance(1000000L + random.nextInt(9000000)); // 100만원 ~ 1000만원
        return bankBalanceRepository.save(balance);
    }

    @Transactional
    public void generateBankTransactions(Long userId, String accountNumber) {
        // 해당 계좌의 기존 거래내역 확인
        List<BankTransaction> existingTransactions = bankTransactionRepository.findByAccountNumber(accountNumber);
        
        // 이미 거래내역이 존재하는 경우 추가 생성하지 않음
        if (!existingTransactions.isEmpty()) {
            return;
        }

        // 최근 3개월간의 거래 내역 생성
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = endDate.minusMonths(3);

        // 90개의 거래 내역 생성
        for (int i = 0; i < 90; i++) {
            BankTransaction transaction = createTransaction(
                userId,
                accountNumber,
                DummyDataGenerator.getRandomDate(startDate, endDate)
            );
            bankTransactionRepository.save(transaction);
        }
    }

    private BankTransaction createTransaction(Long userId, String accountNumber, LocalDateTime transactionDate) {
        BankTransaction transaction = new BankTransaction();
        transaction.setUserId(userId);
        transaction.setAccountNumber(accountNumber);
        transaction.setTransactionDate(transactionDate);
        transaction.setTransactionType(DummyDataGenerator.randomChoice(DummyDataGenerator.TRANSACTION_TYPES));
        
        long amount = DummyDataGenerator.generateTransactionAmount(transaction.getTransactionType());
        transaction.setAmount(amount);
        
        // 잔액 계산 (실제로는 이전 거래의 잔액을 기반으로 계산해야 함)
        transaction.setBalanceAfter(1000000L + random.nextInt(9000000));
        
        transaction.setDescription(DummyDataGenerator.generateTransactionDescription(
            transaction.getTransactionType(),
            DummyDataGenerator.randomChoice(DummyDataGenerator.MERCHANT_NAMES)
        ));
        
        transaction.setCategory(DummyDataGenerator.randomChoice(DummyDataGenerator.CATEGORIES));
        
        return transaction;
    }

    @Transactional(readOnly = true)
    public List<BankBalance> getBankAccounts(Long userId) {
        return bankBalanceRepository.findByUserId(userId);
    }

    @Transactional
    public void revokeAccountConsent(Long userId, String accountNumber) {
        // 계좌 존재 여부 확인
        BankBalance bankBalance = bankBalanceRepository.findByUserIdAndAccountNumber(userId, accountNumber)
            .orElseThrow(() -> new IllegalArgumentException("해당 계좌를 찾을 수 없습니다."));

        // 계좌 비활성화
        bankBalance.setIsActive(false);
        bankBalanceRepository.save(bankBalance);

        // 해당 계좌의 거래내역 삭제
        bankTransactionRepository.deleteByAccountNumber(accountNumber);
    }
} 