package com.example.controller;

import com.example.entity.Comment;
import com.example.entity.User;
import com.example.service.CommentService;
import com.example.service.UserService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CommentController {
    private final CommentService commentService;
    private final UserService userService;

    @Data
    public static class CommentResponse {
        private Long id;
        private String content;
        private String authorNickname;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public CommentResponse(Comment comment) {
            this.id = comment.getId();
            this.content = comment.getContent();
            this.authorNickname = comment.getUser().getNickname();
            this.createdAt = comment.getCreatedAt();
            this.updatedAt = comment.getUpdatedAt();
        }
    }

    @Data
    public static class CreateCommentRequest {
        private String content;
    }

    // 댓글 목록 조회
    @GetMapping("/board/{boardId}")
    public ResponseEntity<?> getComments(@PathVariable Long boardId) {
        try {
            log.info("[댓글 컨트롤러] 댓글 목록 조회 요청: {}", boardId);
            List<Comment> comments = commentService.getCommentsByBoardId(boardId);
            List<CommentResponse> responses = comments.stream()
                    .map(CommentResponse::new)
                    .toList();
            return ResponseEntity.ok(responses);
        } catch (Exception e) {
            log.error("[댓글 컨트롤러] 댓글 목록 조회 중 오류 발생: {}", e.getMessage());
            return ResponseEntity.internalServerError().body("댓글 목록 조회 중 오류가 발생했습니다.");
        }
    }

    // 댓글 작성
    @PostMapping("/board/{boardId}")
    public ResponseEntity<?> createComment(
            @PathVariable Long boardId,
            @RequestBody CreateCommentRequest request,
            Authentication authentication) {
        try {
            log.info("[댓글 컨트롤러] 댓글 작성 요청: {}", boardId);
            User user = userService.findByKakaoId(authentication.getName());
            Comment comment = commentService.createComment(boardId, request.getContent(), user);
            return ResponseEntity.ok(new CommentResponse(comment));
        } catch (Exception e) {
            log.error("[댓글 컨트롤러] 댓글 작성 중 오류 발생: {}", e.getMessage());
            return ResponseEntity.internalServerError().body("댓글 작성 중 오류가 발생했습니다.");
        }
    }

    // 댓글 수정
    @PutMapping("/{commentId}")
    public ResponseEntity<?> updateComment(
            @PathVariable Long commentId,
            @RequestBody CreateCommentRequest request,
            Authentication authentication) {
        try {
            log.info("[댓글 컨트롤러] 댓글 수정 요청: {}", commentId);
            User user = userService.findByKakaoId(authentication.getName());
            Comment comment = commentService.updateComment(commentId, request.getContent(), user);
            return ResponseEntity.ok(new CommentResponse(comment));
        } catch (Exception e) {
            log.error("[댓글 컨트롤러] 댓글 수정 중 오류 발생: {}", e.getMessage());
            return ResponseEntity.internalServerError().body("댓글 수정 중 오류가 발생했습니다.");
        }
    }

    // 댓글 삭제
    @DeleteMapping("/{commentId}")
    public ResponseEntity<?> deleteComment(
            @PathVariable Long commentId,
            Authentication authentication) {
        try {
            log.info("[댓글 컨트롤러] 댓글 삭제 요청: {}", commentId);
            User user = userService.findByKakaoId(authentication.getName());
            commentService.deleteComment(commentId, user);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("[댓글 컨트롤러] 댓글 삭제 중 오류 발생: {}", e.getMessage());
            return ResponseEntity.internalServerError().body("댓글 삭제 중 오류가 발생했습니다.");
        }
    }
} 