package com.example.controller;

import com.example.dto.challenge.CreateChallengeRequest;
import com.example.dto.challenge.ChallengeResponse;
import com.example.dto.challenge.JoinChallengeRequest;
import com.example.dto.challenge.ParticipationResponse;
import com.example.dto.challenge.ChallengeSummaryResponse;
import com.example.dto.challenge.ChallengeDetailResponse;
import com.example.dto.challenge.UpdateChallengeRequest;
import com.example.dto.challenge.CompleteChallengeRequest;
import com.example.dto.challenge.ChallengeRankingResponse;
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
import com.example.entity.challenge.ChallengeCategory;
import com.example.dto.challenge.CategoryInfoResponse;
import java.util.List;
import java.util.stream.Collectors;

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
        try {
            // status 파라미터 유효성 검사
            if (status != null && !status.isEmpty()) {
                String upperStatus = status.toUpperCase();
                if (!List.of("ACTIVE", "UPCOMING", "COMPLETED", "ALL").contains(upperStatus)) {
                    log.error("[챌린지 목록 조회] 잘못된 상태값: {}", status);
                    return ResponseEntity.badRequest().build();
                }
            }

            // 페이지네이션 파라미터 유효성 검사
            if (pageable.getPageNumber() < 0) {
                log.error("[챌린지 목록 조회] 잘못된 페이지 번호: {}", pageable.getPageNumber());
                return ResponseEntity.badRequest().build();
            }
            if (pageable.getPageSize() <= 0 || pageable.getPageSize() > 100) {
                log.error("[챌린지 목록 조회] 잘못된 페이지 크기: {}", pageable.getPageSize());
                return ResponseEntity.badRequest().build();
            }

            Page<ChallengeSummaryResponse> response = challengeService.getChallengesByCategory(category, status, pageable);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.error("[챌린지 목록 조회] 잘못된 파라미터: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (NullPointerException e) {
            log.error("[챌린지 목록 조회] 필수 파라미터 누락: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("[챌린지 목록 조회] 서버 오류 발생: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{challengeId}")
    public ResponseEntity<ChallengeDetailResponse> getChallengeDetail(
            @PathVariable Long challengeId,
            Authentication authentication) {
        try {
            User user = authentication != null ? userService.findByKakaoId(authentication.getName()) : null;
            ChallengeDetailResponse response = challengeService.getChallengeDetail(challengeId, user);

            if (response == null) {
                log.error("[챌린지 상세 조회] 응답 생성 실패: {}", challengeId);
                return ResponseEntity.internalServerError().build();
            }

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.error("[챌린지 상세 조회] 챌린지를 찾을 수 없음: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            log.error("[챌린지 상세 조회] 접근 권한 없음: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("[챌린지 상세 조회] 오류 발생: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{challengeId}/personalrank")
    public ResponseEntity<ChallengeRankingResponse> getMyRanking(
            @PathVariable Long challengeId,
            Authentication authentication) {
        try {
            User user = userService.findByKakaoId(authentication.getName());
            ChallengeRankingResponse response = challengeService.getUserRanking(challengeId, user);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.error("[랭킹 조회] 유효성 오류: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("[랭킹 조회] 오류 발생: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/{challengeId}")
    public ResponseEntity<ChallengeResponse> updateChallenge(
            @PathVariable Long challengeId,
            @RequestBody UpdateChallengeRequest request,
            Authentication authentication) {
        try {
            User user = userService.findByKakaoId(authentication.getName());
            ChallengeResponse response = challengeService.updateChallenge(challengeId, request, user);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.error("[챌린지 수정] 유효성 오류: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (IllegalStateException e) {
            log.error("[챌린지 수정] 상태 오류: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("[챌린지 수정] 오류 발생: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/{challengeId}")
    public ResponseEntity<Void> deleteChallenge(
            @PathVariable Long challengeId,
            Authentication authentication) {
        try {
            User user = userService.findByKakaoId(authentication.getName());
            challengeService.deleteChallenge(challengeId, user);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            log.error("[챌린지 삭제] 유효성 오류: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (IllegalStateException e) {
            log.error("[챌린지 삭제] 상태 오류: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("[챌린지 삭제] 오류 발생: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/{challengeId}/complete")
    public ResponseEntity<Void> completeChallenge(
            @PathVariable Long challengeId,
            @RequestBody CompleteChallengeRequest request,
            Authentication authentication) {
        try {
            User user = userService.findByKakaoId(authentication.getName());
            challengeService.completeChallenge(challengeId, request, user);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            log.error("[챌린지 완료] 유효성 오류: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (IllegalStateException e) {
            log.error("[챌린지 완료] 상태 오류: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("[챌린지 완료] 오류 발생: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/{challengeId}/abandon")
    public ResponseEntity<Void> abandonChallenge(
            @PathVariable Long challengeId,
            Authentication authentication) {
        try {
            User user = userService.findByKakaoId(authentication.getName());
            challengeService.abandonChallenge(challengeId, user);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            log.error("[챌린지 포기] 유효성 오류: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (IllegalStateException e) {
            log.error("[챌린지 포기] 상태 오류: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("[챌린지 포기] 오류 발생: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/categories")
    public ResponseEntity<List<CategoryInfoResponse>> getCategories() {
        String s3BaseUrl = "https://myapp-logos.s3.amazonaws.com/ChallengeIcons/";
        List<CategoryInfoResponse> categories = List.of(ChallengeCategory.values()).stream()
                .map(cat -> new CategoryInfoResponse(
                        cat.getDisplayName(),
                        s3BaseUrl + cat.getDisplayName() + ".png"))
                .collect(Collectors.toList());
        return ResponseEntity.ok(categories);
    }
}