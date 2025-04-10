package com.example.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;

@Slf4j
@Component
public class GeminiClient {

    @Value("${gemini.api.key}")
    private String apiKey;

    private final String API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent";
    private final RestTemplate restTemplate;
    private static final int MAX_RETRIES = 3;
    private static final long RETRY_DELAY_MS = 40000; // 40초 대기

    public GeminiClient() {
        this.restTemplate = new RestTemplate();
    }

    @Getter
    @AllArgsConstructor
    public static class ArticleAnalysis {
        private String title;
        private String summary;
        private String explanation;
        private String termExplanationsJson;
    }

    public ArticleAnalysis analyzeArticle(String content) {
        String prompt = buildPrompt(content);
        String response = callGemini(prompt);
        return parseArticleAnalysis(response);
    }

    private String buildPrompt(String content) {
        return "다음 금융 기사를 금융/경제에 대한 정보를 중심으로 분석해서 아래 형식에 맞게 정확히 답변해줘. 각 섹션의 형식을 엄격히 지켜줘:\n\n" +
                content + "\n\n" +
                "형식:\n" +
                "제목: [20자 이내의 핵심적인 제목]\n" +
                "요약: [200자 이내의 핵심 내용 요약]\n" +
                "핵심 내용: [한 문장으로 된 핵심 내용]\n" +
                "상세 설명: [5-6문장으로 된 쉬운 설명]\n" +
                "용어_JSON: [\n" +
                "  {\"term\": \"용어1\", \"explanation\": \"설명1\"},\n" +
                "  {\"term\": \"용어2\", \"explanation\": \"설명2\"},\n" +
                "  {\"term\": \"용어3\", \"explanation\": \"설명3\"}\n" +
                "]";
    }

    private ArticleAnalysis parseArticleAnalysis(String response) {
        if (response == null) {
            log.error("[Gemini] 응답이 null입니다.");
            return null;
        }

        try {
            String[] lines = response.split("\n");
            String title = null;
            String summary = null;
            StringBuilder explanation = new StringBuilder();
            StringBuilder jsonBuilder = new StringBuilder();
            boolean isJson = false;

            for (String line : lines) {
                // 제목 파싱 (## 제목: 또는 제목: 형식 모두 처리)
                if (line.matches(".*제목:.*")) {
                    title = line.replaceAll("^[#\\s]*제목:\\s*", "").trim();
                    continue;
                }
                
                // 요약 파싱 (## 요약: 또는 요약: 형식 모두 처리)
                if (line.matches(".*요약:.*")) {
                    summary = line.replaceAll("^[#\\s]*요약:\\s*", "").trim();
                    continue;
                }

                // 마크다운 볼드(**) 및 헤더(##) 형식 처리
                line = line.replaceAll("\\*\\*", "").trim();
                
                if (line.startsWith("용어_JSON:") || line.startsWith("[") || line.startsWith("```json")) {
                    isJson = true;
                    if (!line.startsWith("[")) {
                        continue;
                    }
                    jsonBuilder.append(line).append("\n");
                } else if (isJson) {
                    if (line.startsWith("```")) continue;
                    jsonBuilder.append(line).append("\n");
                } else if (line.startsWith("핵심 내용:") || line.startsWith("상세 설명:")) {
                    explanation.append(line).append("\n");
                }
            }

            if (title == null || summary == null) {
                log.error("[Gemini] 필수 필드 누락 - 제목: {}, 요약: {}", title, summary);
                return null;
            }

            return new ArticleAnalysis(
                title,
                summary,
                explanation.toString().trim(),
                jsonBuilder.toString().trim()
            );
        } catch (Exception e) {
            log.error("[Gemini] 응답 파싱 실패: {}", e.getMessage(), e);
            return null;
        }
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
            
            log.info("[Gemini] API 요청: \n{}", prompt);
            ResponseEntity<String> response = restTemplate.postForEntity(fullUrl, request, String.class);
            log.info("[Gemini] API 응답 상태: {}", response.getStatusCode());

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response.getBody());
            String responseText = root.path("candidates").get(0)
                    .path("content").path("parts").get(0)
                    .path("text").asText();
            
            log.info("[Gemini] API 응답 내용: \n{}", responseText);
            return responseText;
        } catch (Exception e) {
            log.error("[Gemini] API 호출 실패: {}", e.getMessage(), e);
            return null;
        }
    }

    public String generateQuiz(String content, String userType, int quizNumber) {
        String prompt = String.format("""
            다음 금융 기사를 바탕으로 각 사용자 유형에 맞는 퀴즈를 생성해줘.
            각 퀴즈는 랜덤하게 4지선다 또는 OX 문제로 생성해줘.
            
            내용:
            %s
            
            각 퀴즈는 다음 형식을 정확히 지켜서 답변해줘:
            
            [금융 초보형]
            문제: [금융 초보자에게 적합한 기본 개념 위주의 쉬운 퀴즈]
            # 4지선다인 경우에만 아래 보기 포함
            1) [보기1]
            2) [보기2]
            3) [보기3]
            4) [보기4]
            정답: [정답]
            해설: [초보자가 이해하기 쉬운 상세한 해설]
            
            [신용 도전형]
            문제: [신용, 대출, 이자율에 관한 실용적인 퀴즈]
            # 4지선다인 경우에만 아래 보기 포함
            1) [보기1]
            2) [보기2]
            3) [보기3]
            4) [보기4]
            정답: [정답]
            해설: [신용 관리에 도움이 되는 실용적인 해설]
            
            [투자 지향형]
            문제: [투자, 주식, 펀드 관련 심화 퀴즈]
            # 4지선다인 경우에만 아래 보기 포함
            1) [보기1]
            2) [보기2]
            3) [보기3]
            4) [보기4]
            정답: [정답]
            해설: [투자 관련 전문적인 해설]
            
            [절약 성향형]
            문제: [저축, 지출 관리, 재테크 관련 실용적인 퀴즈]
            # 4지선다인 경우에만 아래 보기 포함
            1) [보기1]
            2) [보기2]
            3) [보기3]
            4) [보기4]
            정답: [정답]
            해설: [실질적인 절약과 재테크 팁이 담긴 해설]
            """, content);

        return callGemini(prompt);
    }
}