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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    @Data
    public static class QuizResultDTO {
        private Long id;
        private Long quizId;
        private String selectedAnswer;
        private Boolean isCorrect;
        private LocalDateTime createdAt;
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
    public ResponseEntity<List<QuizResultDTO>> getMyQuizResults(Authentication authentication) {
        try {
            User user = userService.findByKakaoId(authentication.getName());
            List<QuizResult> results = quizResultService.getUserQuizResults(user);
            
            List<QuizResultDTO> dtoList = results.stream()
                .map(result -> {
                    QuizResultDTO dto = new QuizResultDTO();
                    dto.setId(result.getId());
                    dto.setQuizId(result.getQuiz().getId());
                    dto.setSelectedAnswer(result.getSelectedAnswer());
                    dto.setIsCorrect(result.getIsCorrect());
                    dto.setCreatedAt(result.getCreatedAt());
                    return dto;
                })
                .collect(Collectors.toList());
                
            return ResponseEntity.ok(dtoList);
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

    @GetMapping("/{resultId}")
    public ResponseEntity<QuizResultDTO> getQuizResult(
            @PathVariable Long resultId,
            Authentication authentication) {
        try {
            User user = userService.findByKakaoId(authentication.getName());
            QuizResult result = quizResultService.findById(resultId);
            
            if (result == null) {
                return ResponseEntity.notFound().build();
            }
            
            // 사용자의 퀴즈 결과인지 확인
            if (!result.getUser().getId().equals(user.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            
            QuizResultDTO dto = new QuizResultDTO();
            dto.setId(result.getId());
            dto.setQuizId(result.getQuiz().getId());
            dto.setSelectedAnswer(result.getSelectedAnswer());
            dto.setIsCorrect(result.getIsCorrect());
            dto.setCreatedAt(result.getCreatedAt());
            
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            log.error("[퀴즈 결과] 조회 중 오류 발생: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/quiz/{quizId}")
    public ResponseEntity<QuizResultDTO> getQuizResultByQuizId(
            @PathVariable Long quizId,
            Authentication authentication) {
        try {
            User user = userService.findByKakaoId(authentication.getName());
            QuizResult result = quizResultService.findByQuizIdAndUserId(quizId, user.getId());
            
            if (result == null) {
                return ResponseEntity.notFound().build();
            }
            
            QuizResultDTO dto = new QuizResultDTO();
            dto.setId(result.getId());
            dto.setQuizId(result.getQuiz().getId());
            dto.setSelectedAnswer(result.getSelectedAnswer());
            dto.setIsCorrect(result.getIsCorrect());
            dto.setCreatedAt(result.getCreatedAt());
            
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            log.error("[퀴즈 결과] 조회 중 오류 발생: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }
} 