package com.example.ml;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import java.util.List;
import java.util.Map;

@Component
public class ModelRunner {
    private static final Logger logger = LoggerFactory.getLogger(ModelRunner.class);
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final RestTemplate restTemplate;
    
    @Value("${ml.service.url:http://localhost:8000}")
    private String mlServiceUrl;

    public ModelRunner() {
        this.restTemplate = new RestTemplate();
    }

    public List<Map<String, Object>> getRecommendations(Map<String, Object> userData) {
        try {
            logger.info("Sending recommendation request to ML service with data: {}", userData);
            
            // FastAPI 서비스로 요청 전송
            List<Map<String, Object>> response = restTemplate.postForObject(
                mlServiceUrl + "/recommend",
                userData,
                List.class
            );
            
            logger.info("Received recommendations from ML service: {}", response);
            return response;
            
        } catch (Exception e) {
            logger.error("Error getting recommendations from ML service: {}", e.getMessage(), e);
            return List.of();
        }
    }
} 