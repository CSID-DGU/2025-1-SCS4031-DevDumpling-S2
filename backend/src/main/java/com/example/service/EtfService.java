package com.example.service;

import com.example.api.EtfApiClient;
import com.example.dto.krx.EtfResponse;
import com.example.entity.Etf;
import com.example.repository.EtfRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class EtfService {
    
    private final EtfApiClient etfApiClient;
    private final EtfRepository etfRepository;
    
    @Transactional
    public void fetchAndSaveEtfData(String basDd) {
        log.info("Starting to fetch ETF data for date: {}", basDd);
        
        try {
            List<EtfResponse> etfResponses = etfApiClient.getDailyEtfInfo(basDd);
            if (etfResponses.isEmpty()) {
                log.warn("No ETF data received for date: {}", basDd);
                return;
            }
            
            log.info("Processing {} ETF entries", etfResponses.size());
            for (EtfResponse response : etfResponses) {
                try {
                    if (response.getIsuCd() == null || response.getIsuCd().trim().isEmpty()) {
                        log.warn("Skipping ETF with empty code: {}", response);
                        continue;
                    }
                    
                    Etf etf = Etf.fromResponse(response);
                    etf.setBasDd(basDd);
                    
                    Optional<Etf> existingEtf = etfRepository.findByBasDdAndIsuCd(basDd, etf.getIsuCd());
                    
                    if (existingEtf.isPresent()) {
                        Etf updatedEtf = existingEtf.get();
                        updatedEtf.updateFromResponse(response);
                        etfRepository.save(updatedEtf);
                        log.debug("Updated existing ETF: {}", updatedEtf.getIsuCd());
                    } else {
                        etfRepository.save(etf);
                        log.debug("Saved new ETF: {}", etf.getIsuCd());
                    }
                } catch (Exception e) {
                    log.error("Error processing ETF entry: {}", response, e);
                }
            }
            
            log.info("Successfully saved ETF data for date: {}", basDd);
        } catch (Exception e) {
            log.error("Error while fetching and saving ETF data for date: {}", basDd, e);
            throw new RuntimeException("Failed to fetch and save ETF data: " + e.getMessage(), e);
        }
    }
    
    @Transactional(readOnly = true)
    public List<Etf> getEtfsByDate(String basDd) {
        log.info("Getting ETFs for date: {}", basDd);
        return etfRepository.findByBasDd(basDd);
    }
    
    @Transactional(readOnly = true)
    public List<Etf> getEtfsByDateAndMarket(String basDd, String mktNm) {
        log.info("Getting ETFs for date: {} and market: {}", basDd, mktNm);
        return etfRepository.findByBasDdAndMktNm(basDd, mktNm);
    }
    
    @Transactional(readOnly = true)
    public Optional<Etf> getEtfByDateAndCode(String basDd, String isuCd) {
        log.info("Getting ETF for date: {} and code: {}", basDd, isuCd);
        return etfRepository.findByBasDdAndIsuCd(basDd, isuCd);
    }
    
    @Transactional(readOnly = true)
    public List<Etf> getEtfsByDateAndIndex(String basDd, String idxIndNm) {
        log.info("Getting ETFs for date: {} and index: {}", basDd, idxIndNm);
        return etfRepository.findByBasDdAndIdxIndNm(basDd, idxIndNm);
    }
    
    @Transactional(readOnly = true)
    public List<Etf> getEtfsByDateAndNavRange(String basDd, Float minNav, Float maxNav) {
        log.info("Getting ETFs for date: {} with NAV between {} and {}", basDd, minNav, maxNav);
        return etfRepository.findByBasDdAndNavRange(basDd, minNav, maxNav);
    }
    
    @Transactional(readOnly = true)
    public List<Etf> getEtfsByDateAndMinMarketCap(String basDd, Long minMktcap) {
        log.info("Getting ETFs for date: {} with market cap >= {}", basDd, minMktcap);
        return etfRepository.findByBasDdAndMinMktcap(basDd, minMktcap);
    }

    @Transactional
    public void updateDailyEtfInfo() {
        String today = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
        List<EtfResponse> etfResponses = etfApiClient.getDailyEtfInfo(today);
        
        for (EtfResponse response : etfResponses) {
            etfRepository.findByBasDdAndIsuCd(today, response.getIsuCd())
                .ifPresentOrElse(
                    etf -> {
                        etf.updateFromResponse(response);
                        etfRepository.save(etf);
                    },
                    () -> etfRepository.save(Etf.fromResponse(response))
                );
        }
        log.info("Successfully updated daily ETF information");
    }
} 