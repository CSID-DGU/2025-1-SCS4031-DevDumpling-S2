package com.example.controller;

import com.example.dto.challenge.ChallengeMessageRequest;
import com.example.dto.challenge.ChallengeMessageResponse;
import com.example.entity.User;
import com.example.service.ChallengeMessageService;
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
@RequestMapping("/api/challenges/{challengeId}/messages")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ChallengeMessageController {

    private final ChallengeMessageService messageService;
    private final UserService userService;

    @GetMapping
    public ResponseEntity<Page<ChallengeMessageResponse>> getMessages(
            @PathVariable Long challengeId,
            Pageable pageable,
            Authentication authentication) {
        try {
            User user = authentication != null ? 
                userService.findByKakaoId(authentication.getName()) : null;
            Page<ChallengeMessageResponse> messages = messageService.getMessages(challengeId, pageable, user);
            return ResponseEntity.ok(messages);
        } catch (IllegalArgumentException e) {
            log.error("[메시지 조회] 유효성 오류: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (IllegalStateException e) {
            log.error("[메시지 조회] 상태 오류: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("[메시지 조회] 오류 발생: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping
    public ResponseEntity<ChallengeMessageResponse> createMessage(
            @PathVariable Long challengeId,
            @RequestBody ChallengeMessageRequest request,
            Authentication authentication) {
        try {
            User user = userService.findByKakaoId(authentication.getName());
            ChallengeMessageResponse response = messageService.createMessage(challengeId, request, user);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.error("[메시지 작성] 유효성 오류: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (IllegalStateException e) {
            log.error("[메시지 작성] 상태 오류: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("[메시지 작성] 오류 발생: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/{messageId}")
    public ResponseEntity<ChallengeMessageResponse> updateMessage(
            @PathVariable Long challengeId,
            @PathVariable Long messageId,
            @RequestBody ChallengeMessageRequest request,
            Authentication authentication) {
        try {
            User user = userService.findByKakaoId(authentication.getName());
            ChallengeMessageResponse response = messageService.updateMessage(messageId, request, user);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.error("[메시지 수정] 유효성 오류: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (IllegalStateException e) {
            log.error("[메시지 수정] 상태 오류: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("[메시지 수정] 오류 발생: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/{messageId}")
    public ResponseEntity<Void> deleteMessage(
            @PathVariable Long challengeId,
            @PathVariable Long messageId,
            Authentication authentication) {
        try {
            User user = userService.findByKakaoId(authentication.getName());
            messageService.deleteMessage(messageId, user);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            log.error("[메시지 삭제] 유효성 오류: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (IllegalStateException e) {
            log.error("[메시지 삭제] 상태 오류: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("[메시지 삭제] 오류 발생: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
} 