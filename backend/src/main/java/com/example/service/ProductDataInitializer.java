package com.example.service;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.DayOfWeek;
import lombok.extern.slf4j.Slf4j;
import java.util.List;
import java.util.Arrays;
import java.util.stream.Collectors;
import com.example.api.DgkApiClient;
import com.example.repository.InsuranceRepository;
import com.example.entity.Insurance;
import com.example.dto.dgk.InsuranceResponse;

@Slf4j
@Component
public class ProductDataInitializer implements ApplicationRunner {

    private final FinanceProductService financeProductService;
    private final EtfService etfService;
    private final StockService stockService;
    private final DgkApiClient dgkApiClient;
    private final InsuranceRepository insuranceRepository;

    public ProductDataInitializer(
        FinanceProductService financeProductService,
        EtfService etfService,
        StockService stockService,
        DgkApiClient dgkApiClient,
        InsuranceRepository insuranceRepository
    ) {
        this.financeProductService = financeProductService;
        this.etfService = etfService;
        this.stockService = stockService;
        this.dgkApiClient = dgkApiClient;
        this.insuranceRepository = insuranceRepository;
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

        // 보험 상품 정보 동기화
        syncInsuranceProducts();

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

    private void syncInsuranceProducts() {
        log.info("보험 상품 정보 동기화 시작");
        try {
            // 기존 데이터 삭제
            insuranceRepository.deleteAll();
            log.info("기존 보험 상품 데이터 삭제 완료");

            // API 호출하여 데이터 저장
            InsuranceResponse response = dgkApiClient.fetchInsurance(1, 100);
            if (response != null && response.getBody() != null && response.getBody().getItems() != null) {
                List<Insurance> insurances = Arrays.stream(response.getBody().getItems().getItem())
                    .map(item -> Insurance.builder()
                        .cmpyCd(item.getCmpyCd())
                        .cmpyNm(item.getCmpyNm())
                        .ptrn(item.getPtrn())
                        .mog(item.getMog())
                        .prdNm(item.getPrdNm())
                        .age(item.getAge())
                        .mlInsRt(item.getMlInsRt())
                        .fmlInsRt(item.getFmlInsRt())
                        .basDt(item.getBasDt())
                        .ofrInstNm(item.getOfrInstNm())
                        .build())
                    .collect(Collectors.toList());
                
                insuranceRepository.saveAll(insurances);
                log.info("보험 상품 정보 동기화 완료 - {}건", insurances.size());
            }
        } catch (Exception e) {
            log.error("보험 상품 정보 동기화 실패: {}", e.getMessage(), e);
        }
    }

    private String adjustToLastBusinessDay(LocalDate date) {
        while (date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY) {
            date = date.minusDays(1);
        }
        return date.format(DateTimeFormatter.BASIC_ISO_DATE);
    }
} 