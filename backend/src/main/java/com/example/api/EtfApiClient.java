package com.example.api;

import com.example.dto.krx.EtfResponse;
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
public class EtfApiClient {
    
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private static final String BASE_URL = "http://data-dbg.krx.co.kr/svc/apis/etp/etf_bydd_trd";
    private static final int DEFAULT_LIMIT = 50;

    @Value("${krx.api.key}")
    private String apiKey;
    
    public EtfApiClient(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }
    
    public List<EtfResponse> getDailyEtfInfo(String basDd) {
        return getDailyEtfInfo(basDd, DEFAULT_LIMIT);
    }
    
    public List<EtfResponse> getDailyEtfInfo(String basDd, int limit) {
        try {
            log.info("Requesting ETF daily info for date: {} with limit: {}", basDd, limit);
            
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
                
                JsonNode etfDataList = rootNode.path("OutBlock_1");
                
                if (etfDataList.isArray()) {
                    List<EtfResponse> etfs = new ArrayList<>();
                    int count = 0;
                    
                    for (JsonNode etfNode : etfDataList) {
                        if (count >= limit) {
                            log.info("Reached limit of {} etfs", limit);
                            break;
                        }
                        
                        log.debug("Processing etf node: {}", etfNode);
                        try {
                            EtfResponse etfResponse = parseEtfResponse(etfNode);
                            if (etfResponse.getIsuCd() == null || etfResponse.getIsuCd().trim().isEmpty()) {
                                log.warn("Skipping etf with empty code: {}", etfNode);
                                continue;
                            }
                            etfs.add(etfResponse);
                            count++;
                        } catch (Exception e) {
                            log.error("Error parsing etf node: {}", etfNode, e);
                        }
                    }
                    log.info("Successfully fetched {} etf entries (limited to {})", etfs.size(), limit);
                    return etfs;
                } else {
                    log.error("Invalid response format: OutBlock_1 is not an array");
                    throw new RuntimeException("Invalid response format from KRX API");
                }
            } else {
                log.error("Failed to get etf info. Status: {}, Body: {}", 
                    response.getStatusCode(), response.getBody());
                throw new RuntimeException("Failed to get etf info from KRX API");
            }
            
        } catch (Exception e) {
            log.error("Error while fetching etf info for date: {}", basDd, e);
            throw new RuntimeException("Failed to fetch etf info: " + e.getMessage(), e);
        }
    }
    
    private EtfResponse parseEtfResponse(JsonNode node) {
        return EtfResponse.builder()
                .basDd(getStringValue(node, "BAS_DD"))
                .isuCd(getStringValue(node, "ISU_CD"))
                .isuNm(getStringValue(node, "ISU_NM"))
                .mktNm(getStringValue(node, "MKT_NM"))
                .sectTpNm(getStringValue(node, "SECT_TP_NM"))
                .tddClsprc(getStringValue(node, "TDD_CLSPRC"))
                .cmpprevddPrc(getStringValue(node, "CMPPREVDD_PRC"))
                .flucRt(getStringValue(node, "FLUC_RT"))
                .nav(getStringValue(node, "NAV"))
                .tddOpnprc(getStringValue(node, "TDD_OPNPRC"))
                .tddHgprc(getStringValue(node, "TDD_HGPRC"))
                .tddLwprc(getStringValue(node, "TDD_LWPRC"))
                .accTrdvol(getStringValue(node, "ACC_TRDVOL"))
                .accTrdval(getStringValue(node, "ACC_TRDVAL"))
                .mktcap(getStringValue(node, "MKTCAP"))
                .invstasstNetasstTotamt(getStringValue(node, "INVSTASST_NETASST_TOTAMT"))
                .listShrs(getStringValue(node, "LIST_SHRS"))
                .idxIndNm(getStringValue(node, "IDX_IND_NM"))
                .objStkprcIdx(getStringValue(node, "OBJ_STKPRC_IDX"))
                .cmpprevddIdx(getStringValue(node, "CMPPREVDD_IDX"))
                .flucRtIdx(getStringValue(node, "FLUC_RT_IDX"))
                .build();
    }
    
    private String getStringValue(JsonNode node, String fieldName) {
        JsonNode fieldNode = node.get(fieldName);
        return fieldNode != null ? fieldNode.asText() : "";
    }
}