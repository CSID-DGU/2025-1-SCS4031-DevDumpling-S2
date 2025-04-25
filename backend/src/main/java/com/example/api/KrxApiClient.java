package com.example.api;

import com.example.dto.krx.StockResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class KrxApiClient {
    
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private static final String BASE_URL = "https://data-dbg.krx.co.kr/svc/apis/sto/stk_bydd_trd";
    private static final int DEFAULT_LIMIT = 50;

    @Value("${krx.api.key}")
    private String apiKey;
    
    public KrxApiClient(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }
    
    public List<StockResponse> getDailyStockInfo(String basDd) {
        return getDailyStockInfo(basDd, DEFAULT_LIMIT);
    }
    
    public List<StockResponse> getDailyStockInfo(String basDd, int limit) {
        try {
            log.info("Requesting KRX daily stock info for date: {} with limit: {}", basDd, limit);
            
            // HTTP Headers 설정
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + apiKey);
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            // 요청 본문 설정
            String requestBody = String.format("{\"basDd\":\"%s\"}", basDd);
            
            // HTTP 엔티티 설정
            HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);
            
            log.debug("Request headers: {}", headers);
            log.debug("Request body: {}", requestBody);
            
            // POST 요청 보내기
            ResponseEntity<String> response = restTemplate.exchange(
                BASE_URL,
                HttpMethod.POST,
                entity,
                String.class
            );
            
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                log.debug("Received response: {}", response.getBody());
                JsonNode rootNode = objectMapper.readTree(response.getBody());
                
                // 응답 구조 확인을 위한 로깅
                log.info("Response structure: {}", rootNode.toPrettyString());
                
                JsonNode stockDataList = rootNode.path("OutBlock_1");
                
                if (stockDataList.isArray()) {
                    List<StockResponse> stocks = new ArrayList<>();
                    int count = 0;
                    
                    for (JsonNode stockNode : stockDataList) {
                        if (count >= limit) {
                            log.info("Reached limit of {} stocks", limit);
                            break;
                        }
                        
                        log.debug("Processing stock node: {}", stockNode);
                        try {
                            StockResponse stockResponse = parseStockResponse(stockNode);
                            if (stockResponse.getIsuCd() == null || stockResponse.getIsuCd().trim().isEmpty()) {
                                log.warn("Skipping stock with empty code: {}", stockNode);
                                continue;
                            }
                            stocks.add(stockResponse);
                            count++;
                        } catch (Exception e) {
                            log.error("Error parsing stock node: {}", stockNode, e);
                        }
                    }
                    log.info("Successfully fetched {} stock entries (limited to {})", stocks.size(), limit);
                    return stocks;
                } else {
                    log.error("Invalid response format: OutBlock_1 is not an array");
                    throw new RuntimeException("Invalid response format from KRX API");
                }
            } else {
                log.error("Failed to get stock info. Status: {}, Body: {}", 
                    response.getStatusCode(), response.getBody());
                throw new RuntimeException("Failed to get stock info from KRX API");
            }
            
        } catch (Exception e) {
            log.error("Error while fetching stock info for date: {}", basDd, e);
            throw new RuntimeException("Failed to fetch stock info: " + e.getMessage(), e);
        }
    }
    
    private StockResponse parseStockResponse(JsonNode node) {
        return StockResponse.builder()
                .basDd(getStringValue(node, "BAS_DD"))
                .isuCd(getStringValue(node, "ISU_CD"))
                .isuNm(getStringValue(node, "ISU_NM"))
                .mktNm(getStringValue(node, "MKT_NM"))
                .sectTpNm(getStringValue(node, "SECT_TP_NM"))
                .tddClsprc(getStringValue(node, "TDD_CLSPRC"))
                .cmpprevddPrc(getStringValue(node, "CMPPREVDD_PRC"))
                .flucRt(getStringValue(node, "FLUC_RT"))
                .tddOpnprc(getStringValue(node, "TDD_OPNPRC"))
                .tddHgprc(getStringValue(node, "TDD_HGPRC"))
                .tddLwprc(getStringValue(node, "TDD_LWPRC"))
                .accTrdvol(getStringValue(node, "ACC_TRDVOL"))
                .accTrdval(getStringValue(node, "ACC_TRDVAL"))
                .mktcap(getStringValue(node, "MKTCAP"))
                .listShrs(getStringValue(node, "LIST_SHRS"))
                .build();
    }
    
    private String getStringValue(JsonNode node, String fieldName) {
        JsonNode fieldNode = node.get(fieldName);
        return fieldNode != null ? fieldNode.asText() : "";
    }
}