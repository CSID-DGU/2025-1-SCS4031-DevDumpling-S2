package com.example.service;

import com.example.api.KrxApiClient;
import com.example.dto.krx.StockResponse;
import com.example.entity.Stock;
import com.example.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class StockService {
    
    private final KrxApiClient krxApiClient;
    private final StockRepository stockRepository;
    
    @Transactional
    public void fetchAndSaveStockData(String basDd) {
        log.info("Starting to fetch stock data for date: {}", basDd);
        
        try {
            List<StockResponse> stockResponses = krxApiClient.getDailyStockInfo(basDd);
            if (stockResponses.isEmpty()) {
                log.warn("No stock data received for date: {}", basDd);
                return;
            }
            
            log.info("Processing {} stock entries", stockResponses.size());
            for (StockResponse response : stockResponses) {
                try {
                    if (response.getIsuCd() == null || response.getIsuCd().trim().isEmpty()) {
                        log.warn("Skipping stock with empty code: {}", response);
                        continue;
                    }
                    
                    Stock stock = Stock.fromResponse(response);
                    stock.setBasDd(basDd);
                    
                    // 기존 데이터가 있는지 확인
                    Optional<Stock> existingStock = stockRepository.findByBasDdAndSrtnCd(basDd, stock.getSrtnCd());
                    
                    if (existingStock.isPresent()) {
                        // 기존 데이터 업데이트
                        Stock updatedStock = existingStock.get();
                        updatedStock.updateFromStock(stock);
                        Stock saved = stockRepository.save(updatedStock);
                        log.debug("Updated existing stock: {}", saved);
                    } else {
                        // 새로운 데이터 저장
                        Stock saved = stockRepository.save(stock);
                        log.debug("Saved new stock: {}", saved);
                    }
                } catch (Exception e) {
                    log.error("Error processing stock entry: {}", response, e);
                }
            }
            
            log.info("Successfully saved stock data for date: {}", basDd);
        } catch (Exception e) {
            log.error("Error while fetching and saving stock data for date: {}", basDd, e);
            throw new RuntimeException("Failed to fetch and save stock data: " + e.getMessage(), e);
        }
    }
    
    @Transactional(readOnly = true)
    public List<Stock> getStocksByDate(String basDd) {
        log.info("Getting stocks for date: {}", basDd);
        return stockRepository.findByBasDd(basDd);
    }
    
    @Transactional(readOnly = true)
    public List<Stock> getStocksByDateAndMarket(String basDd, String mrktCtg) {
        return stockRepository.findByBasDdAndMrktCtg(basDd, mrktCtg);
    }
    
    @Transactional(readOnly = true)
    public Optional<Stock> getStockByDateAndCode(String basDd, String srtnCd) {
        log.info("Getting stock for date: {} and code: {}", basDd, srtnCd);
        return stockRepository.findByBasDdAndSrtnCd(basDd, srtnCd);
    }
} 