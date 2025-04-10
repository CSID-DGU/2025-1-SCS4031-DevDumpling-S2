package com.example.controller;

import com.example.entity.Quiz;
import com.example.entity.UserType;
import com.example.service.QuizService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/quizzes")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class QuizController {

    private final QuizService quizService;

    // 특정 기사의 퀴즈 목록 조회
    @GetMapping("/article/{articleId}")
    public ResponseEntity<List<Quiz>> getQuizzesByArticle(@PathVariable Long articleId) {
        return ResponseEntity.ok(quizService.findByArticleId(articleId));
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
        return ResponseEntity.ok(quizService.findByArticleIdAndUserType(articleId, userType));
    }
} 