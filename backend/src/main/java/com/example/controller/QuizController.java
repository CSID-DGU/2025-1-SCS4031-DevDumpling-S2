package com.example.controller;

import com.example.entity.Quiz;
import com.example.entity.User;
import com.example.entity.UserType;
import com.example.service.QuizService;
import com.example.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/quizzes")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class QuizController {

    private final QuizService quizService;
    private final UserService userService;

    // 특정 기사의 퀴즈 목록 조회
    @GetMapping("/article/{articleId}")
    public ResponseEntity<List<Quiz>> getQuizzesByArticle(@PathVariable Long articleId) {
        return ResponseEntity.ok(quizService.findByArticleId(articleId));
    }

    // 특정 사용자 유형의 퀴즈 목록 조회 (아직 풀지 않은 퀴즈만)
    @GetMapping("/user-type/{userType}/unanswered")
    public ResponseEntity<List<Quiz>> getUnansweredQuizzesByUserType(
            @PathVariable UserType userType,
            Authentication authentication) {
        try {
            User user = userService.findByKakaoId(authentication.getName());
            List<Quiz> quizzes = quizService.findUnansweredQuizzesByUserType(userType, user);
            return ResponseEntity.ok(quizzes);
        } catch (Exception e) {
            log.error("[퀴즈] 아직 풀지 않은 퀴즈 조회 중 오류 발생: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    // 특정 사용자 유형의 퀴즈 목록 조회
    @GetMapping("/user-type/{userType}")
    public ResponseEntity<List<Quiz>> getQuizzesByUserType(@PathVariable UserType userType) {
        return ResponseEntity.ok(quizService.findByUserType(userType));
    }

    // 특정 기사의 특정 사용자 유형 퀴즈 조회
    @GetMapping("/article/{articleId}/user-type/{userType}")
    public ResponseEntity<Quiz> getQuizByArticleAndUserType(
            @PathVariable Long articleId,
            @PathVariable UserType userType) {
        Quiz quiz = quizService.findByArticleIdAndUserType(articleId, userType);
        if (quiz == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(quiz);
    }
} 