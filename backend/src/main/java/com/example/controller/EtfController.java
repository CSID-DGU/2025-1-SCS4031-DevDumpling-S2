package com.example.controller;

import com.example.entity.Etf;
import com.example.service.EtfService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/etfs")
@RequiredArgsConstructor
public class EtfController {

    private final EtfService etfService;

    /**
     * KRX API를 통해 특정 날짜의 ETF 정보를 수집하여 저장합니다.
     * @param basDd 기준일자 (YYYYMMDD 형식)
     * @return 처리 결과 메시지
     */
    @PostMapping("/fetch/{basDd}")
    public ResponseEntity<String> fetchAndSaveEtfData(@PathVariable String basDd) {
        log.info("Fetching and saving ETF data for date: {}", basDd);
        etfService.fetchAndSaveEtfData(basDd);
        return ResponseEntity.ok("ETF data fetched and saved successfully");
    }

    /**
     * 특정 날짜의 모든 ETF 정보를 조회합니다.
     * @param basDd 기준일자 (YYYYMMDD 형식)
     * @return ETF 정보 목록
     */
    @GetMapping("/daily/{basDd}")
    public ResponseEntity<List<Etf>> getDailyEtfs(@PathVariable String basDd) {
        log.info("Fetching ETF information for date: {}", basDd);
        List<Etf> etfs = etfService.getEtfsByDate(basDd);
        return ResponseEntity.ok(etfs);
    }

    /**
     * 특정 날짜의 특정 시장 ETF 정보를 조회합니다.
     * @param basDd 기준일자 (YYYYMMDD 형식)
     * @param mktNm 시장구분 (KOSPI/KOSDAQ)
     * @return ETF 정보 목록
     */
    @GetMapping("/daily/{basDd}/market/{mktNm}")
    public ResponseEntity<List<Etf>> getDailyEtfsByMarket(
            @PathVariable String basDd,
            @PathVariable String mktNm) {
        log.info("Fetching ETF information for date: {} and market: {}", basDd, mktNm);
        List<Etf> etfs = etfService.getEtfsByDateAndMarket(basDd, mktNm);
        return ResponseEntity.ok(etfs);
    }

    /**
     * 특정 날짜의 특정 종목 정보를 조회합니다.
     * @param basDd 기준일자 (YYYYMMDD 형식)
     * @param isuCd 종목코드           
     * @return ETF 정보
     */
    @GetMapping("/daily/{basDd}/code/{isuCd}")
    public ResponseEntity<Etf> getEtfInfo(
            @PathVariable String basDd,
            @PathVariable String isuCd) {
        log.info("Fetching ETF information for date: {} and code: {}", basDd, isuCd);
        return etfService.getEtfByDateAndCode(basDd, isuCd)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 특정 날짜의 특정 기초지수 ETF 정보를 조회합니다.
     * @param basDd 기준일자 (YYYYMMDD 형식)
     * @param idxIndNm 기초지수명
     * @return ETF 정보 목록
     */
    @GetMapping("/daily/{basDd}/index/{idxIndNm}")
    public ResponseEntity<List<Etf>> getEtfsByDateAndIndex(
            @PathVariable String basDd,
            @PathVariable String idxIndNm) {
        log.info("Fetching ETF information for date: {} and index: {}", basDd, idxIndNm);
        return ResponseEntity.ok(etfService.getEtfsByDateAndIndex(basDd, idxIndNm));
    }

    /**
     * 특정 날짜의 NAV 범위 ETF 정보를 조회합니다.
     * @param basDd 기준일자 (YYYYMMDD 형식)
     * @param minNav 최소 NAV
     * @param maxNav 최대 NAV
     * @return ETF 정보 목록
     */
    @GetMapping("/daily/{basDd}/nav")
    public ResponseEntity<List<Etf>> getEtfsByDateAndNavRange(
            @PathVariable String basDd,
            @RequestParam Float minNav,
            @RequestParam Float maxNav) {
        log.info("Fetching ETF information for date: {} with NAV between {} and {}", basDd, minNav, maxNav);
        return ResponseEntity.ok(etfService.getEtfsByDateAndNavRange(basDd, minNav, maxNav));
    }

    /**
     * 특정 날짜의 최소 시가총액 이상 ETF 정보를 조회합니다.
     * @param basDd 기준일자 (YYYYMMDD 형식)
     * @param minMktcap 최소 시가총액
     * @return ETF 정보 목록
     */
    @GetMapping("/daily/{basDd}/marketcap")
    public ResponseEntity<List<Etf>> getEtfsByDateAndMinMarketCap(
            @PathVariable String basDd,
            @RequestParam Long minMktcap) {
        log.info("Fetching ETF information for date: {} with market cap >= {}", basDd, minMktcap);
        return ResponseEntity.ok(etfService.getEtfsByDateAndMinMarketCap(basDd, minMktcap));
    }

    /**
     * 오늘 날짜의 ETF 정보를 업데이트합니다.
     * @return 처리 결과
     */
    @PostMapping("/update")
    public ResponseEntity<String> updateDailyEtfInfo() {
        log.info("Updating daily ETF information");
        etfService.updateDailyEtfInfo();
        return ResponseEntity.ok("Daily ETF information updated successfully");
    }
} 