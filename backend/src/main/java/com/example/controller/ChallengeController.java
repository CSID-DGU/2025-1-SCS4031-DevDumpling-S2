package com.example.controller;

import com.example.dto.challenge.CreateChallengeRequest;
import com.example.dto.challenge.ChallengeResponse;
import com.example.entity.User;
import com.example.service.ChallengeService;
import com.example.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/challenges")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ChallengeController {

    private final ChallengeService challengeService;
    private final UserService userService;

    @PostMapping
    public ResponseEntity<ChallengeResponse> createChallenge(
            @RequestBody CreateChallengeRequest request,
            Authentication authentication) {
        try {
            User user = userService.findByKakaoId(authentication.getName());
            ChallengeResponse response = challengeService.createChallenge(request, user);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("[챌린지 생성] 오류 발생: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
} 