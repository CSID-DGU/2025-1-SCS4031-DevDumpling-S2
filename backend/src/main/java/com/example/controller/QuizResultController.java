package com.example.controller;

import com.example.entity.Quiz;
import com.example.entity.QuizResult;
import com.example.entity.User;
import com.example.service.QuizResultService;
import com.example.service.QuizService;
import com.example.service.UserService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/quiz-results")
@RequiredArgsConstructor
public class QuizResultController {

    private final QuizResultService quizResultService;
    private final QuizService quizService;
    private final UserService userService;

    @Data
    public static class QuizAnswerRequest {
        private String selectedAnswer;
    }

    @PostMapping("/{quizId}/submit")
    public ResponseEntity<?> submitQuizAnswer(
            @PathVariable Long quizId,
            @RequestBody QuizAnswerRequest request,
            Authentication authentication) {
        try {
            User user = userService.findByKakaoId(authentication.getName());
            Quiz quiz = quizService.findById(quizId);
            
            if (quiz == null) {
                return ResponseEntity.notFound().build();
            }
            
            QuizResult result = quizResultService.saveQuizResult(user, quiz, request.getSelectedAnswer());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("[퀴즈 결과] 제출 중 오류 발생: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/results")
    public ResponseEntity<List<QuizResult>> getMyQuizResults(Authentication authentication) {
        try {
            User user = userService.findByKakaoId(authentication.getName());
            List<QuizResult> results = quizResultService.getUserQuizResults(user);
            return ResponseEntity.ok(results);
        } catch (Exception e) {
            log.error("[퀴즈 결과] 조회 중 오류 발생: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getMyQuizStats(Authentication authentication) {
        try {
            User user = userService.findByKakaoId(authentication.getName());
            
            long totalAnswers = quizResultService.getUserTotalAnswerCount(user);
            long correctAnswers = quizResultService.getUserCorrectAnswerCount(user);
            double correctRate = quizResultService.getUserCorrectAnswerRate(user);
            
            return ResponseEntity.ok(Map.of(
                "totalAnswers", totalAnswers,
                "correctAnswers", correctAnswers,
                "correctRate", correctRate
            ));
        } catch (Exception e) {
            log.error("[퀴즈 통계] 조회 중 오류 발생: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
} 