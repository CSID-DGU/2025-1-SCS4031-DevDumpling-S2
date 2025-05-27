package com.example.ml;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClientException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import java.util.List;
import java.util.Map;
import java.time.Duration;

@Component
public class ModelRunner {
    private static final Logger logger = LoggerFactory.getLogger(ModelRunner.class);
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final RestTemplate restTemplate;
    
    @Value("${ml.service.url:http://localhost:8000}")
    private String mlServiceUrl;

    public ModelRunner() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(5000); // 5초
        factory.setReadTimeout(10000);   // 10초
        this.restTemplate = new RestTemplate(factory);
    }

    public List<Map<String, Object>> getRecommendations(Map<String, Object> userData) {
        try {
            logger.info("Sending recommendation request to ML service with data: {}", userData);
            logger.info("ML Service URL: {}", mlServiceUrl);
            
            // 데이터를 JSON 문자열로 변환
            String jsonData = objectMapper.writeValueAsString(userData);
            logger.info("Converted JSON data: {}", jsonData);
            
            // HTTP 헤더 설정
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            // HTTP 요청 엔티티 생성
            HttpEntity<String> requestEntity = new HttpEntity<>(jsonData, headers);
            
            // FastAPI 서비스로 요청 전송
            List<Map<String, Object>> response = restTemplate.postForObject(
                mlServiceUrl + "/recommend",
                requestEntity,
                List.class
            );
            
            logger.info("Received recommendations from ML service: {}", response);
            return response;
            
        } catch (RestClientException e) {
            logger.error("Error getting recommendations from ML service: {}", e.getMessage());
            logger.error("Error details: ", e);
            throw new RuntimeException("ML 서비스와 통신 중 오류가 발생했습니다: " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Unexpected error in ML service communication: {}", e.getMessage());
            logger.error("Error details: ", e);
            throw new RuntimeException("ML 서비스 처리 중 예상치 못한 오류가 발생했습니다: " + e.getMessage(), e);
        }
    }
} 