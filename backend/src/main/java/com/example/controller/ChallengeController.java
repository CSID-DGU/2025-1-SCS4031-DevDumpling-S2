package com.example.controller;

import com.example.dto.challenge.CreateChallengeRequest;
import com.example.dto.challenge.ChallengeResponse;
import com.example.dto.challenge.JoinChallengeRequest;
import com.example.dto.challenge.ParticipationResponse;
import com.example.dto.challenge.ChallengeSummaryResponse;
import com.example.dto.challenge.ChallengeDetailResponse;
import com.example.entity.User;
import com.example.service.ChallengeService;
import com.example.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/challenges")
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

    @PostMapping("/{challengeId}/join")
    public ResponseEntity<ParticipationResponse> joinChallenge(
            @PathVariable Long challengeId,
            @RequestBody(required = false) JoinChallengeRequest request,
            Authentication authentication) {
        try {
            User user = userService.findByKakaoId(authentication.getName());
            
            // request가 null인 경우 새로 생성 (공개 챌린지)
            if (request == null) {
                request = new JoinChallengeRequest();
            }
            request.setChallengeId(challengeId);
            
            ParticipationResponse response = challengeService.joinChallenge(request, user);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.error("[챌린지 참여] 유효성 오류: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (IllegalStateException e) {
            log.error("[챌린지 참여] 상태 오류: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("[챌린지 참여] 오류 발생: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<Page<ChallengeSummaryResponse>> getChallengesByCategory(
            @PathVariable com.example.entity.Challenge.ChallengeCategory category,
            @RequestParam(required = false) String status,
            Pageable pageable) {
        return ResponseEntity.ok(challengeService.getChallengesByCategory(category, status, pageable));
    }

    @GetMapping("/{challengeId}")
    public ResponseEntity<ChallengeDetailResponse> getChallengeDetail(
            @PathVariable Long challengeId) {
        try {
            ChallengeDetailResponse response = challengeService.getChallengeDetail(challengeId);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.error("[챌린지 상세 조회] 챌린지를 찾을 수 없음: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("[챌린지 상세 조회] 오류 발생: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
} 