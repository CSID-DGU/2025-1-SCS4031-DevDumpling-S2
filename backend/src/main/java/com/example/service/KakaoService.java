package com.example.service;

import com.example.entity.User;
import com.example.repository.UserRepository;
import com.example.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@RequiredArgsConstructor
public class KakaoService {

    private final UserRepository userRepository;
    private final UserService userService;
    private final RestTemplate restTemplate = new RestTemplate();
    private static final Logger log = LoggerFactory.getLogger(KakaoService.class);

    @Value("${kakao.client.id}")
    private String clientId;

    @Value("${kakao.client.secret}")
    private String clientSecret;

    @Value("${kakao.redirect-uri}")
    private String redirectUri;

    public String getKakaoAccessToken(String code) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", clientId);
        params.add("redirect_uri", redirectUri);
        params.add("code", code);
        params.add("client_secret", clientSecret);

        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest = new HttpEntity<>(params, headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                "https://kauth.kakao.com/oauth/token",
                HttpMethod.POST,
                kakaoTokenRequest,
                Map.class
        );

        return (String) response.getBody().get("access_token");
    }

    public Map<String, Object> getKakaoUserInfo(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        HttpEntity<MultiValueMap<String, String>> kakaoUserInfoRequest = new HttpEntity<>(headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                "https://kapi.kakao.com/v2/user/me?property_keys=[\"properties.nickname\",\"properties.profile_image\"]",
                HttpMethod.GET,
                kakaoUserInfoRequest,
                Map.class
        );

        log.info("카카오 API 응답: {}", response.getBody());
        return response.getBody();
    }

    public User processKakaoLogin(Map<String, Object> userInfo) {
        String kakaoId = String.valueOf(userInfo.get("id"));
        Map<String, Object> properties = (Map<String, Object>) userInfo.get("properties");
        
        log.info("카카오 유저 정보: {}", userInfo);
        log.info("카카오 properties: {}", properties);
        
        String nickname = (String) properties.get("nickname");
        String profileImage = (String) properties.get("profile_image");

        log.info("카카오 로그인 처리 - ID: {}, 닉네임: {}, 프로필: {}", kakaoId, nickname, profileImage);

        // 이미 존재하는 사용자인지 확인
        if (!userRepository.existsByKakaoId(kakaoId)) {
            log.info("새로운 사용자 생성");
            return userService.createUser(kakaoId, nickname, profileImage, false);
        }

        log.info("기존 사용자 정보 업데이트");
        // 기존 사용자 정보 업데이트
        User user = userRepository.findByKakaoId(kakaoId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        user.setNickname(nickname);
        user.setProfileImage(profileImage);
        User savedUser = userRepository.save(user);
        log.info("저장된 사용자 정보: {}", savedUser);
        return savedUser;
    }
} 