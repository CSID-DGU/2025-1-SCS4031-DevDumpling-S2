package com.example.dummy.service;

import com.example.dummy.entity.InvestmentRecord;
import com.example.dummy.entity.InvestmentTransaction;
import com.example.dummy.repository.InvestmentRecordRepository;
import com.example.dummy.repository.InvestmentTransactionRepository;
import com.example.dummy.util.DummyDataGenerator;
import com.example.entity.Etf;
import com.example.entity.Stock;
import com.example.service.EtfService;
import com.example.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class InvestmentDummyDataService {

    private final InvestmentRecordRepository investmentRecordRepository;
    private final InvestmentTransactionRepository investmentTransactionRepository;
    private final EtfService etfService;
    private final StockService stockService;
    private final Random random = new Random();

    private static final String[] DUMMY_ETF_NAMES = {
        "KODEX 200", "TIGER 차이나전기차", "KODEX 레버리지", "TIGER 미국S&P500"
    };
    private static final String[] DUMMY_STOCK_NAMES = {
        "삼성전자", "NAVER", "카카오", "현대차", "LG화학"
    };

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
        boolean isEtf = random.nextBoolean(); // ETF 여부 랜덤 결정
        String today = LocalDateTime.now().format(DateTimeFormatter.BASIC_ISO_DATE);
        
        String productName;
        if (isEtf) {
            List<Etf> etfs = etfService.getEtfsByDate(today);
            if (!etfs.isEmpty()) {
                productName = etfs.get(random.nextInt(etfs.size())).getIsuNm();
            } else {
                productName = DUMMY_ETF_NAMES[random.nextInt(DUMMY_ETF_NAMES.length)];
            }
        } else {
            List<Stock> stocks = stockService.getStocksByDate(today);
            if (!stocks.isEmpty()) {
                productName = stocks.get(random.nextInt(stocks.size())).getItmsNm();
            } else {
                productName = DUMMY_STOCK_NAMES[random.nextInt(DUMMY_STOCK_NAMES.length)];
            }
        }

        LocalDateTime openDate = LocalDateTime.now().minusMonths(random.nextInt(12));

        InvestmentRecord record = InvestmentRecord.builder()
                .userId(userId)
                .accountNumber(accountNumber)
                .accountName(productName)
                .investmentType(isEtf ? "ETF" : "주식")
                .openDate(openDate.toLocalDate())
                .balance(0L)
                .recentTransactionDate(LocalDateTime.now().minusDays(random.nextInt(30)).toLocalDate())
                .recentAmount(100000L + random.nextInt(900000))
                .recentStock(productName)
                .createdAt(LocalDateTime.now())
                .build();

        return investmentRecordRepository.save(record);
    }

    @Transactional
    public void generateInvestmentTransactions(Long userId, String accountNumber) {
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = endDate.minusMonths(3);
        String today = LocalDateTime.now().format(DateTimeFormatter.BASIC_ISO_DATE);

        for (int j = 0; j < 60; j++) {
            InvestmentTransaction transaction = createInvestmentTransaction(
                userId,
                accountNumber,
                DummyDataGenerator.getRandomDate(startDate, endDate),
                today
            );
            investmentTransactionRepository.save(transaction);
        }
    }

    private InvestmentTransaction createInvestmentTransaction(Long userId, String accountNumber, LocalDateTime transactionDate, String today) {
        InvestmentRecord record = investmentRecordRepository.findByAccountNumber(accountNumber)
            .orElseThrow(() -> new RuntimeException("계좌를 찾을 수 없습니다."));
        
        boolean isEtf = "ETF".equals(record.getInvestmentType());
        String productName;
        double price;
        
        if (isEtf) {
            List<Etf> etfs = etfService.getEtfsByDate(today);
            if (!etfs.isEmpty()) {
                Etf etf = etfs.get(random.nextInt(etfs.size()));
                productName = etf.getIsuNm();
                price = etf.getNav();
            } else {
                productName = DUMMY_ETF_NAMES[random.nextInt(DUMMY_ETF_NAMES.length)];
                price = 10000.0 + random.nextDouble() * 90000.0;
            }
        } else {
            List<Stock> stocks = stockService.getStocksByDate(today);
            if (!stocks.isEmpty()) {
                Stock stock = stocks.get(random.nextInt(stocks.size()));
                productName = stock.getItmsNm();
                price = stock.getClpr();
            } else {
                productName = DUMMY_STOCK_NAMES[random.nextInt(DUMMY_STOCK_NAMES.length)];
                price = 50000.0 + random.nextDouble() * 450000.0;
            }
        }

        String transactionType = random.nextBoolean() ? "매수" : "매도";
        int quantity = 1 + random.nextInt(10);
        long amount = (long) (quantity * price);

        return InvestmentTransaction.builder()
                .userId(userId)
                .accountNumber(accountNumber)
                .transactionDate(transactionDate.toLocalDate())
                .transactionType(transactionType)
                .stockName(productName)
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