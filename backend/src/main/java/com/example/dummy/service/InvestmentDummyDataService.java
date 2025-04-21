package com.example.dummy.service;

import com.example.dummy.entity.InvestmentRecord;
import com.example.dummy.entity.InvestmentTransaction;
import com.example.dummy.repository.InvestmentRecordRepository;
import com.example.dummy.repository.InvestmentTransactionRepository;
import com.example.dummy.util.DummyDataGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class InvestmentDummyDataService {

    private final InvestmentRecordRepository investmentRecordRepository;
    private final InvestmentTransactionRepository investmentTransactionRepository;
    private final Random random = new Random();

    @Transactional
    public void generateDummyData(Long userId) {
        // 투자 계좌 1~2개 생성
        int accountCount = 1 + random.nextInt(2);
        for (int i = 0; i < accountCount; i++) {
            InvestmentRecord record = createInvestmentRecord(userId);
            generateInvestmentTransactions(userId, record.getAccountNumber());
            updateInvestmentBalance(record);
        }
    }

    @Transactional
    public InvestmentRecord createInvestmentRecord(Long userId) {
        String accountNumber = DummyDataGenerator.generateAccountNumber();
        String investmentType = DummyDataGenerator.randomChoice(DummyDataGenerator.INVESTMENT_TYPES);
        LocalDateTime openDate = LocalDateTime.now().minusMonths(random.nextInt(12));

        InvestmentRecord record = InvestmentRecord.builder()
                .userId(userId)
                .accountNumber(accountNumber)
                .accountName(DummyDataGenerator.randomChoice(DummyDataGenerator.BANK_NAMES) + " " + investmentType)
                .investmentType(investmentType)
                .openDate(openDate.toLocalDate())
                .balance(0L) // 초기값 0, 거래 생성 후 업데이트
                .recentTransactionDate(LocalDateTime.now().minusDays(random.nextInt(30)).toLocalDate())
                .recentAmount(100000L + random.nextInt(900000))
                .recentStock(DummyDataGenerator.randomChoice(DummyDataGenerator.STOCK_NAMES))
                .createdAt(LocalDateTime.now())
                .build();

        return investmentRecordRepository.save(record);
    }

    @Transactional
    public void generateInvestmentTransactions(Long userId, String accountNumber) {
        // 최근 3개월 거래 내역 생성
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = endDate.minusMonths(3);

        // 월별 평균 20건의 거래 생성
        for (int j = 0; j < 60; j++) {
            InvestmentTransaction transaction = createInvestmentTransaction(
                userId,
                accountNumber,
                DummyDataGenerator.getRandomDate(startDate, endDate)
            );
            investmentTransactionRepository.save(transaction);
        }
    }

    private InvestmentTransaction createInvestmentTransaction(Long userId, String accountNumber, LocalDateTime transactionDate) {
        String stockName = DummyDataGenerator.randomChoice(DummyDataGenerator.STOCK_NAMES);
        String transactionType = random.nextBoolean() ? "매수" : "매도";
        int quantity = 1 + random.nextInt(10);
        double price = 10000.0 + random.nextDouble() * 90000.0;
        long amount = (long) (quantity * price);

        return InvestmentTransaction.builder()
                .userId(userId)
                .accountNumber(accountNumber)
                .transactionDate(transactionDate.toLocalDate())
                .transactionType(transactionType)
                .stockName(stockName)
                .quantity(quantity)
                .price(price)
                .amount(amount)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Transactional
    public void updateInvestmentBalance(InvestmentRecord record) {
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = endDate.minusMonths(3);

        long totalAmount = investmentTransactionRepository.findByAccountNumberAndTransactionDateBetween(
                record.getAccountNumber(), startDate.toLocalDate(), endDate.toLocalDate())
            .stream()
            .mapToLong(transaction -> 
                transaction.getTransactionType().equals("매수") ? 
                transaction.getAmount() : -transaction.getAmount())
            .sum();

        record.setBalance(totalAmount);
        record.setRecentTransactionDate(endDate.toLocalDate());
        investmentRecordRepository.save(record);
    }
} 