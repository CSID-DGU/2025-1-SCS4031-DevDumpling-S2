package com.example.service;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.DayOfWeek;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ProductDataInitializer implements ApplicationRunner {

    private final FinanceProductService financeProductService;
    private final EtfService etfService;
    private final StockService stockService;

    public ProductDataInitializer(
        FinanceProductService financeProductService,
        EtfService etfService,
        StockService stockService
    ) {
        this.financeProductService = financeProductService;
        this.etfService = etfService;
        this.stockService = stockService;
    }

    @Override
    public void run(ApplicationArguments args) {
        // 은행/예금/적금/대출 동기화
        financeProductService.syncAllCompanies("020000");
        financeProductService.syncAllDepositProducts("020000");
        financeProductService.syncAllSavingProducts("020000");

        // 대출 상품 정보 동기화
        financeProductService.syncAllCreditLoanProducts("020000");
        financeProductService.syncAllRentLoanProducts("020000");

        // 주식/ETF 동기화 (어제 날짜로 변경)
        String yesterday = adjustToLastBusinessDay(LocalDate.now().minusDays(1));
        log.info("주식/ETF 동기화 시작 - 날짜: {}", yesterday);
        try {
            stockService.fetchAndSaveStockData(yesterday);
            etfService.fetchAndSaveEtfData(yesterday);
            log.info("주식/ETF 동기화 완료 - 날짜: {}", yesterday);
        } catch (Exception e) {
            log.error("주식/ETF 동기화 실패 - 날짜: {}, 에러: {}", yesterday, e.getMessage(), e);
        }
    }

    private String adjustToLastBusinessDay(LocalDate date) {
        while (date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY) {
            date = date.minusDays(1);
        }
        return date.format(DateTimeFormatter.BASIC_ISO_DATE);
    }
} 