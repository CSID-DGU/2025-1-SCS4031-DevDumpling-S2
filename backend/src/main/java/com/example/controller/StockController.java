package com.example.controller;

import com.example.entity.Stock;
import com.example.service.StockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/stocks")
@RequiredArgsConstructor
public class StockController {

    private final StockService stockService;

    /**
     * KRX API를 통해 특정 날짜의 주식 정보를 수집하여 저장합니다.
     * @param basDd 기준일자 (YYYYMMDD 형식)
     * @return 처리 결과 메시지
     */
    @PostMapping("/collect/{basDd}")
    public ResponseEntity<String> collectDailyStockInfo(@PathVariable String basDd) {
        log.info("Collecting stock information for date: {}", basDd);
        stockService.fetchAndSaveStockData(basDd);
        return ResponseEntity.ok("Stock data collection completed");
    }

    /**
     * 특정 날짜의 모든 주식 정보를 조회합니다.
     * @param basDd 기준일자 (YYYYMMDD 형식)
     * @return 주식 정보 목록
     */
    @GetMapping("/daily/{basDd}")
    public ResponseEntity<List<Stock>> getDailyStocks(@PathVariable String basDd) {
        log.info("Fetching stock information for date: {}", basDd);
        List<Stock> stocks = stockService.getStocksByDate(basDd);
        return ResponseEntity.ok(stocks);
    }

    /**
     * 특정 날짜의 특정 시장 주식 정보를 조회합니다.
     * @param basDd 기준일자 (YYYYMMDD 형식)
     * @param mrktCtg 시장구분 (KOSPI/KOSDAQ)
     * @return 주식 정보 목록
     */
    @GetMapping("/daily/{basDd}/market/{mrktCtg}")
    public ResponseEntity<List<Stock>> getDailyStocksByMarket(
            @PathVariable String basDd,
            @PathVariable String mrktCtg) {
        log.info("Fetching stock information for date: {} and market: {}", basDd, mrktCtg);
        List<Stock> stocks = stockService.getStocksByDateAndMarket(basDd, mrktCtg);
        return ResponseEntity.ok(stocks);
    }

    /**
     * 특정 날짜의 특정 종목 정보를 조회합니다.
     * @param basDd 기준일자 (YYYYMMDD 형식)
     * @param srtnCd 종목코드
     * @return 주식 정보
     */
    @GetMapping("/daily/{basDd}/stock/{srtnCd}")
    public ResponseEntity<Stock> getStockInfo(
            @PathVariable String basDd,
            @PathVariable String srtnCd) {
        log.info("Fetching stock information for date: {} and code: {}", basDd, srtnCd);
        return stockService.getStockByDateAndCode(basDd, srtnCd)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/fetch/{basDd}")
    public ResponseEntity<String> fetchAndSaveStockData(@PathVariable String basDd) {
        log.info("Fetching and saving stock data for date: {}", basDd);
        stockService.fetchAndSaveStockData(basDd);
        return ResponseEntity.ok("Stock data fetched and saved successfully");
    }

    @GetMapping("/date/{basDd}")
    public ResponseEntity<List<Stock>> getStocksByDate(@PathVariable String basDd) {
        log.info("Getting stocks for date: {}", basDd);
        List<Stock> stocks = stockService.getStocksByDate(basDd);
        return ResponseEntity.ok(stocks);
    }

    @GetMapping("/date/{basDd}/code/{srtnCd}")
    public ResponseEntity<Stock> getStockByDateAndCode(
            @PathVariable String basDd,
            @PathVariable String srtnCd) {
        log.info("Getting stock for date: {} and code: {}", basDd, srtnCd);
        return stockService.getStockByDateAndCode(basDd, srtnCd)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
} 