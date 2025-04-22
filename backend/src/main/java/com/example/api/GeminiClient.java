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
        return "다음 금융 기사를 고등학생 수준에서 금융/경제에 대한 정보를 중심으로 분석하고 아래 JSON 형식으로 정확히 답변해줘:\n\n" +
                content + "\n\n" +
                "응답 형식 (JSON):\n" +
                "{\n" +
                "  \"summary\": \"200자 이내의 핵심 내용 요약\",\n" +
                "  \"explanation\": \"5-6문장으로 된 쉬운 설명\",\n" +
                "  \"terms\": [\n" +
                "    {\"term\": \"용어1\", \"explanation\": \"설명1\"},\n" +
                "    {\"term\": \"용어2\", \"explanation\": \"설명2\"}\n" +
                "  ]\n" +
                "}\n\n" +
                "주의사항:\n" +
                "1. 반드시 위 JSON 형식을 지켜서 응답해주세요.\n" +
                "2. 마크다운 문법(**, ## 등)을 사용하지 마세요.\n" +
                "3. terms 배열은 반드시 [] 형식을 유지해주세요.";
    }

    private ArticleAnalysis parseArticleAnalysis(String response) {
        if (response == null) {
            log.error("[Gemini] 응답이 null입니다.");
            return null;
        }

        try {
            log.info("[Gemini] 응답 파싱 시작:\n{}", response);
            
            // 주의사항 제거 (주의사항: 또는 Note: 로 시작하는 부분 제거)
            int cautionIndex = response.indexOf("\n주의사항:");
            if (cautionIndex == -1) {
                cautionIndex = response.indexOf("\nNote:");
            }
            if (cautionIndex != -1) {
                response = response.substring(0, cautionIndex);
            }
            
            // JSON 시작과 끝 위치 찾기
            int startIndex = response.indexOf("{");
            int endIndex = response.lastIndexOf("}") + 1;
            
            if (startIndex == -1 || endIndex == 0) {
                log.error("[Gemini] JSON 형식이 아닙니다.");
                return null;
            }

            String jsonStr = response.substring(startIndex, endIndex);
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(jsonStr);

            String summary = root.path("summary").asText();
            String explanation = root.path("explanation").asText();
            String terms = root.path("terms").toString();

            // 필드 검증
            if (isInvalidField(summary) || isInvalidField(explanation) || terms.equals("[]")) {
                log.warn("[Gemini] 유효하지 않은 필드 발견 - 요약: {}, 설명: {}, 용어: {}", 
                    summary, explanation, terms);
                return null;
            }

            log.info("[Gemini] 파싱 완료 - 요약 길이: {}, 설명 길이: {}, JSON 길이: {}", 
                summary.length(), explanation.length(), terms.length());

            return new ArticleAnalysis(summary, explanation, terms);
        } catch (Exception e) {
            log.error("[Gemini] 응답 파싱 실패: {}", e.getMessage(), e);
            return null;
        }
    }

    private boolean isInvalidField(String field) {
        if (field == null || field.isEmpty()) return true;
        field = field.trim();
        return field.equals("처리 중...") ||
               field.equals("기사 처리에 실패했습니다.") ||
               field.equals("설명없음") ||
               field.equals("요약없음") ||
               field.equals("[]");
    }

    public String generateQuiz(String content, String userType, int quizNumber) {
        String prompt = String.format("""
            다음 금융 기사를 바탕으로 각 사용자 유형에 맞는 4지선다 퀴즈를 생성해줘.
            기사의 전체적인 맥락과 주요 금융/경제 상황을 다루는 문제를 생성해줘.
            너무 세부적이거나 지엽적인 내용은 피하고, 일반적인 금융 지식과 관련된 문제를 만들어줘.
            
            내용:
            %s
            
            각 퀴즈는 다음 형식을 정확히 지켜서 답변해줘:
            
            [A 유형]
            문제: [기사의 맥락에서 신용 관리, 투자 전략, 리스크 관리 등 적극적인 금융 활동과 관련된 퀴즈]
            보기:
            1) [보기1]
            2) [보기2]
            3) [보기3]
            4) [보기4]
            정답: [정답]
            해설: [신용 관리와 투자 전략에 대한 전문적인 해설]
            
            [B 유형]
            문제: [기사의 경제 상황과 관련된 절약과 투자의 균형, 합리적인 자산 관리 전략을 묻는 퀴즈]
            보기:
            1) [보기1]
            2) [보기2]
            3) [보기3]
            4) [보기4]
            정답: [정답]
            해설: [절약과 투자의 균형을 맞추는 실용적인 해설]
            
            [C 유형]
            문제: [기사의 맥락에서 소비와 절약의 균형, 안정적인 자산 관리 방법을 묻는 퀴즈]
            보기:          
            1) [보기1]
            2) [보기2]
            3) [보기3]
            4) [보기4]
            정답: [정답]
            해설: [소비와 절약의 균형을 맞추는 실용적인 해설]
            
            [D 유형]
            문제: [기사의 경제 상황과 관련된 안정적인 자산 관리, 저위험 투자 전략을 묻는 퀴즈]
            보기:
            1) [보기1]
            2) [보기2]
            3) [보기3]
            4) [보기4]
            정답: [정답]
            해설: [안정적인 자산 관리와 저위험 투자에 대한 실용적인 해설]
            
            주의사항:
            1. 반드시 위 형식을 지켜서 응답해주세요.
            2. 마크다운 문법(**, ##, ``` 등)을 사용하지 마세요.
            3. 문제는 기사의 전체적인 맥락과 주요 금융/경제 상황을 다루어야 합니다.
            4. 너무 세부적이거나 지엽적인 내용은 피해주세요.
            5. 보기는 서로 명확히 구분되어야 하며, 정답이 명확해야 합니다.
            6. 각 유형의 특성을 고려하여 적절한 난이도와 내용의 문제를 생성해주세요.
            """, content);

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
            
            log.info("[Gemini] API 요청 시작");
            ResponseEntity<String> response = restTemplate.postForEntity(fullUrl, request, String.class);
            log.info("[Gemini] API 응답 상태: {}", response.getStatusCode());

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response.getBody());
            String responseText = root.path("candidates").get(0)
                    .path("content").path("parts").get(0)
                    .path("text").asText();
            
            // 마크다운 포맷 제거
            responseText = responseText.replaceAll("```json\\s*", "").replaceAll("```\\s*$", "").trim();
            
            log.info("[Gemini] API 응답 수신 완료 - 길이: {}", responseText.length());
            return responseText;
        } catch (Exception e) {
            log.error("[Gemini] API 호출 실패: {}", e.getMessage(), e);
            return null;
        }
    }
}