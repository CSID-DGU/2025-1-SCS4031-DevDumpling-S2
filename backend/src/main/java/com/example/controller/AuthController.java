package com.example.controller;

import com.example.entity.User;
import com.example.security.JwtTokenProvider;
import com.example.service.KakaoService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final KakaoService kakaoService;
    private final JwtTokenProvider jwtTokenProvider;

    @Value("${kakao.client.id}")
    private String clientId;

    @Value("${kakao.redirect-uri}")
    private String redirectUri;

    @GetMapping("/kakao/login")
    public void kakaoLogin(HttpServletResponse response) throws IOException {
        String kakaoAuthUrl = "https://kauth.kakao.com/oauth/authorize" +
                "?client_id=" + clientId +
                "&redirect_uri=" + redirectUri +
                "&response_type=code";
        response.sendRedirect(kakaoAuthUrl);
    }

    @GetMapping("/kakao/callback")
    public ResponseEntity<?> kakaoCallback(@RequestParam String code) {
        try {
            // 1. 카카오로부터 액세스 토큰 받기
            String accessToken = kakaoService.getKakaoAccessToken(code);
            
            // 2. 카카오 사용자 정보 받기
            Map<String, Object> userInfo = kakaoService.getKakaoUserInfo(accessToken);
            
            // 3. 사용자 정보 처리 및 저장
            User user = kakaoService.processKakaoLogin(userInfo);
            
            // 4. JWT 토큰 생성
            String jwtToken = jwtTokenProvider.createToken(user.getKakaoId(), Collections.singletonList("ROLE_USER"));
            
            // 5. 응답 데이터 구성
            Map<String, Object> userData = new HashMap<>();
            userData.put("id", user.getId());
            userData.put("kakaoId", user.getKakaoId());
            userData.put("nickname", user.getNickname());
            userData.put("profileImage", user.getProfileImage());
            userData.put("userType", user.getUserType());
            userData.put("createdAt", user.getCreatedAt());
            userData.put("updatedAt", user.getUpdatedAt());

            Map<String, Object> response = new HashMap<>();
            response.put("user", userData);
            response.put("token", jwtToken);
            
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("카카오 로그인 실패: " + e.getMessage());
        }
    }
} 