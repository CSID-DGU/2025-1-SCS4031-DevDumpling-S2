package com.example.api;

import com.example.dto.dgk.InsuranceResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.time.Duration;
import java.util.Arrays;

@Slf4j
@Service
public class DgkApiClient {

    private final RestTemplate restTemplate;
    private final String serviceKey;
    private final ObjectMapper jsonMapper;
    private final XmlMapper xmlMapper;
    private static final String BASE_URL =
        "https://apis.data.go.kr/1160100/service/GetMedicalReimbursementInsuranceInfoService/getInsuranceInfo";

    public DgkApiClient(@Value("${dgk.api.key}") String serviceKey) {
        this.serviceKey = serviceKey;
        this.jsonMapper = new ObjectMapper();
        this.xmlMapper = new XmlMapper();
        
        // HttpComponentsClientHttpRequestFactory를 사용하여 RestTemplate 생성
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(HttpClients.createDefault());
        factory.setConnectTimeout(Duration.ofSeconds(5));
        factory.setConnectionRequestTimeout(Duration.ofSeconds(5));
        this.restTemplate = new RestTemplate(factory);
        
        // XML 메시지 컨버터 추가
        MappingJackson2XmlHttpMessageConverter xmlConverter = new MappingJackson2XmlHttpMessageConverter();
        xmlConverter.setSupportedMediaTypes(Arrays.asList(
            MediaType.APPLICATION_XML,
            MediaType.TEXT_XML,
            MediaType.APPLICATION_XHTML_XML
        ));
        this.restTemplate.getMessageConverters().add(xmlConverter);
    }

    /**
     * 실손보험정보 조회
     *
     * @param pageNo    조회할 페이지 번호 (기본 1)
     * @param numOfRows 페이지당 건수 (기본 10)
     */
    public InsuranceResponse fetchInsurance(int pageNo, int numOfRows) {
        try {
            // URI 객체 생성
            URI uri = new URI(String.format("%s?serviceKey=%s&resultType=xml&pageNo=%d&numOfRows=%d",
                BASE_URL, serviceKey, pageNo, numOfRows));

            log.info("API 요청 URL: {}", uri);
            
            // XML 응답을 String으로 받기
            String xmlResponse = restTemplate.getForObject(uri, String.class);
            log.debug("XML 응답: {}", xmlResponse);
            
            // XML을 직접 InsuranceResponse 객체로 변환
            InsuranceResponse response = xmlMapper.readValue(xmlResponse, InsuranceResponse.class);
            
            if (response == null || response.getBody() == null || 
                response.getBody().getItems() == null) {
                log.error("API 응답이 올바르지 않습니다.");
                throw new RuntimeException("API 응답이 올바르지 않습니다.");
            }
            
            return response;
        } catch (Exception e) {
            log.error("API 호출 중 오류 발생: {}", e.getMessage());
            throw new RuntimeException("API 호출 중 오류가 발생했습니다: " + e.getMessage(), e);
        }
    }
}
