package com.example.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
public class GeminiClient {

    @Value("${gemini.api.key}")
    private String apiKey;

    private final String API_URL = "https://generativelanguage.googleapis.com/v1/models/gemini-pro:generateContent";

    public String summarize(String content) {
        String prompt = "아래는 금융 기사야. 핵심 내용을 5줄 이내로 요약해줘.\n\n기사:\n" + content;
        return callGemini(prompt);
    }

    public String explainSimply(String content) {
        String prompt = "아래는 금융 기사야. 고등학생도 이해할 수 있도록 쉽게 설명해줘.\n\n기사:\n" + content;
        return callGemini(prompt);
    }

    public String generateQuiz(String summary, String userTypeName) {
        String prompt = "아래는 금융 기사 요약이야.\n\n" +
                "기사 요약:\n" + summary + "\n\n" +
                "사용자 유형: " + userTypeName + "\n\n" +
                "위 내용을 바탕으로 해당 사용자에게 적합한 퀴즈를 하나 만들어줘. " +
                "OX 또는 객관식 4지선다 중 하나여야 하고 정답과 보기(있을 경우), 해설도 포함해줘.";
        return callGemini(prompt);
    }

    public String generateTitle(String content) {
        String prompt = "다음 금융 기사의 핵심 내용을 20자 이내의 제목으로 만들어줘.\n\n기사:\n" + content;
        return callGemini(prompt);
    }

    private String callGemini(String prompt) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            String requestBody = String.format("""
                {
                    "contents": [{
                        "parts": [{
                            "text": "%s"
                        }]
                    }]
                }""", prompt.replace("\"", "\\\""));

            String fullUrl = API_URL + "?key=" + apiKey;
            HttpEntity<String> request = new HttpEntity<>(requestBody, headers);
            
            log.info("Gemini API 호출: {}", fullUrl);
            ResponseEntity<String> response = restTemplate.postForEntity(fullUrl, request, String.class);
            log.info("Gemini API 응답: {}", response.getStatusCode());

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response.getBody());
            return root.path("candidates").get(0)
                    .path("content").path("parts").get(0)
                    .path("text").asText();
        } catch (Exception e) {
            log.error("Gemini API 호출 실패: {}", e.getMessage(), e);
            return "AI 처리 실패: " + e.getMessage();
        }
    }
}