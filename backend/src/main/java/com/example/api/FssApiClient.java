package com.example.api;

import com.example.dto.fss.CompanyResponse;
import com.example.dto.fss.DepositProductResponse;
import com.example.dto.fss.SavingProductResponse;
import com.example.dto.fss.RentHouseLoanResponse;
import com.example.dto.fss.CreditLoanResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.DeserializationFeature;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import jakarta.annotation.PostConstruct;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import javax.net.ssl.*;
import java.security.cert.X509Certificate;

@Component
public class FssApiClient {

    private final RestTemplate restTemplate;
    private final String apiKey;
    private final Environment environment;
    private final ObjectMapper objectMapper;

    public FssApiClient(RestTemplate restTemplate,
                       @Value("${fss.api.key}") String apiKey,
                       Environment environment) {
        this.restTemplate = restTemplate;
        this.apiKey = apiKey;
        this.environment = environment;
        this.objectMapper = new ObjectMapper();
        // 알 수 없는 필드 무시
        this.objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        
        // SSL 인증서 검증 비활성화
        try {
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, new TrustManager[]{new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() { return null; }
                public void checkClientTrusted(X509Certificate[] certs, String authType) {}
                public void checkServerTrusted(X509Certificate[] certs, String authType) {}
            }}, new java.security.SecureRandom());
            
            HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) -> true);
            
            SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
            requestFactory.setConnectTimeout(5000);
            requestFactory.setReadTimeout(5000);
            requestFactory.setBufferRequestBody(false);
            this.restTemplate.setRequestFactory(requestFactory);
        } catch (Exception e) {
            System.err.println("SSL 설정 중 오류 발생: " + e.getMessage());
        }
        
        System.out.println("🔑 Constructor - FSS API Key: " + apiKey);
        System.out.println("🔍 Constructor - Environment FSS API Key: " + environment.getProperty("fss.api.key"));
    }

    @PostConstruct
    public void init() {
        System.out.println("🔧 PostConstruct - FssApiClient initialized");
        System.out.println("🔑 PostConstruct - API Key from field: " + apiKey);
        System.out.println("🔍 PostConstruct - API Key from Environment: " + environment.getProperty("fss.api.key"));
        validateConfiguration();
    }

    private void validateConfiguration() {
        if (apiKey == null || apiKey.trim().isEmpty()) {
            throw new IllegalStateException("FSS API Key가 설정되지 않았습니다. application.yml의 fss.api.key를 확인해주세요.");
        }
        System.out.println("✅ Configuration validated - API Key is properly set");
    }

    private String callApi(String url) {
        try {
            System.out.println("📡 호출 URL: " + url);
            
            HttpHeaders headers = new HttpHeaders();
            headers.set("Accept", "application/json, text/plain, */*");
            headers.set("Accept-Language", "ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7");
            headers.set("Connection", "keep-alive");
            headers.set("Content-Type", "application/json;charset=UTF-8");
            headers.set("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/123.0.0.0 Safari/537.36");
            headers.set("Origin", "https://finlife.fss.or.kr");
            headers.set("Referer", "https://finlife.fss.or.kr/");
            
            HttpEntity<String> entity = new HttpEntity<>(headers);
            
            ResponseEntity<String> response = restTemplate.exchange(url, org.springframework.http.HttpMethod.GET, entity, String.class);
            
            if (response.getBody() == null) {
                System.err.println("❌ API 응답이 null입니다.");
                throw new RuntimeException("API 응답이 null입니다. API 서버 상태를 확인해주세요.");
            }
            
            System.out.println("📥 응답 상태: " + response.getStatusCode());
            System.out.println("📥 응답 결과: " + response.getBody());
            return response.getBody();
            
        } catch (HttpClientErrorException e) {
            System.err.println("❌ HTTP 클라이언트 오류: " + e.getMessage());
            System.err.println("❌ 응답 본문: " + e.getResponseBodyAsString());
            throw new RuntimeException("API 호출 중 오류가 발생했습니다: " + e.getMessage(), e);
        } catch (RestClientException e) {
            System.err.println("❌ REST 클라이언트 오류: " + e.getMessage());
            throw new RuntimeException("API 호출 중 오류가 발생했습니다: " + e.getMessage(), e);
        }
    }

    private <T> T parseResponse(String response, Class<T> responseType) {
        try {
            // 응답이 JSON 형식인지 확인
            if (!response.trim().startsWith("{") || !response.trim().endsWith("}")) {
                System.err.println("❌ 응답이 JSON 형식이 아닙니다: " + response);
                throw new RuntimeException("API 응답이 JSON 형식이 아닙니다.");
            }

            // JSON 파싱 시도
            T result = objectMapper.readValue(response, responseType);
            System.out.println("✅ JSON 파싱 성공");
            return result;
        } catch (Exception e) {
            System.err.println("❌ JSON 파싱 오류: " + e.getMessage());
            System.err.println("❌ 원본 응답: " + response);
            throw new RuntimeException("API 응답 파싱 중 오류가 발생했습니다: " + e.getMessage(), e);
        }
    }

    public CompanyResponse getCompanyList(String topFinGrpNo, int pageNo) {
        System.out.println("\n=== FSS API 호출 정보 (회사 목록) ===");
        System.out.println("🔑 Current API Key: " + apiKey);

        String url = UriComponentsBuilder.fromHttpUrl("https://finlife.fss.or.kr/finlifeapi/companySearch.json")
                .queryParam("auth", apiKey)
                .queryParam("topFinGrpNo", topFinGrpNo)
                .queryParam("pageNo", pageNo)
                .toUriString();

        return parseResponse(callApi(url), CompanyResponse.class);
    }

    public DepositProductResponse getDepositProducts(String topFinGrpNo, int pageNo) {
        System.out.println("\n=== FSS API 호출 정보 (예금 상품) ===");
        System.out.println("🔑 Current API Key: " + apiKey);

        String url = UriComponentsBuilder.fromHttpUrl("https://finlife.fss.or.kr/finlifeapi/depositProductsSearch.json")
                .queryParam("auth", apiKey)
                .queryParam("topFinGrpNo", topFinGrpNo)
                .queryParam("pageNo", pageNo)
                .toUriString();

        return parseResponse(callApi(url), DepositProductResponse.class);
    }

    public SavingProductResponse getSavingProducts(String topFinGrpNo, int pageNo) {
        String url = UriComponentsBuilder.fromHttpUrl("https://finlife.fss.or.kr/finlifeapi/savingProductsSearch.json")
                .queryParam("auth", apiKey)
                .queryParam("topFinGrpNo", topFinGrpNo)
                .queryParam("pageNo", pageNo)
                .toUriString();

        return parseResponse(callApi(url), SavingProductResponse.class);
    }

    public CreditLoanResponse getCreditLoanProducts(String topFinGrpNo, int pageNo) {
        System.out.println("\n=== FSS API 호출 정보 (신용대출 상품) ===");
        System.out.println("🔑 Current API Key: " + apiKey);

        String url = UriComponentsBuilder.fromHttpUrl("https://finlife.fss.or.kr/finlifeapi/creditLoanProductsSearch.json")
                .queryParam("auth", apiKey)
                .queryParam("topFinGrpNo", topFinGrpNo)
                .queryParam("pageNo", pageNo)
                .toUriString();

        return parseResponse(callApi(url), CreditLoanResponse.class);
    }

    public RentHouseLoanResponse getRentLoanProducts(String topFinGrpNo, int pageNo) {
        System.out.println("\n=== FSS API 호출 정보 (전세대출 상품) ===");
        System.out.println("🔑 Current API Key: " + apiKey);

        String url = UriComponentsBuilder.fromHttpUrl("https://finlife.fss.or.kr/finlifeapi/rentHouseLoanProductsSearch.json")
                .queryParam("auth", apiKey)
                .queryParam("topFinGrpNo", topFinGrpNo)
                .queryParam("pageNo", pageNo)
                .toUriString();

        return parseResponse(callApi(url), RentHouseLoanResponse.class);
    }
}