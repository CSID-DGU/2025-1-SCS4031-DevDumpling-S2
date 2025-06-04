package com.example.service;

import com.example.entity.Quiz;
import com.example.entity.QuizResult;
import com.example.entity.User;
import com.example.repository.QuizResultRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class QuizResultService {

    private final QuizResultRepository quizResultRepository;

    @Transactional
    public QuizResult saveQuizResult(User user, Quiz quiz, String selectedAnswer) {
        log.info("[퀴즈 결과] 저장 시작 - 사용자: {}, 퀴즈: {}", user.getId(), quiz.getId());
        
        boolean isCorrect = checkAnswer(quiz, selectedAnswer);
        
        QuizResult quizResult = QuizResult.builder()
                .user(user)
                .quiz(quiz)
                .selectedAnswer(selectedAnswer)
                .isCorrect(isCorrect)
                .build();
        
        QuizResult savedResult = quizResultRepository.save(quizResult);
        log.info("[퀴즈 결과] 저장 완료 - ID: {}, 정답 여부: {}", savedResult.getId(), isCorrect);
        
        return savedResult;
    }

    private boolean checkAnswer(Quiz quiz, String selectedAnswer) {
        try {
            int selected = Integer.parseInt(selectedAnswer);
            // 정답에서 첫 번쨰 문자(번호)만 추출
            String correctAnswer = quiz.getAnswer();
            int correct = Integer.parseInt(correctAnswer.substring(0, 1));
            return selected == correct;
        } catch (Exception e) {
            log.error("[퀴즈 결과] 정답 비교 중 오류 발생: {}", e.getMessage());
            return false;
        }
    }

    public List<QuizResult> getUserQuizResults(User user) {
        return quizResultRepository.findByUserOrderByCreatedAtDesc(user);
    }

    public long getUserCorrectAnswerCount(User user) {
        return quizResultRepository.countCorrectAnswersByUser(user);
    }

    public long getUserTotalAnswerCount(User user) {
        return quizResultRepository.countTotalAnswersByUser(user);
    }

    public double getUserCorrectAnswerRate(User user) {
        long total = getUserTotalAnswerCount(user);
        if (total == 0) return 0.0;
        
        long correct = getUserCorrectAnswerCount(user);
        return (double) correct / total * 100;
    }

    public QuizResult findById(Long id) {
        return quizResultRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Quiz result not found with id: " + id));
    }

    public QuizResult findByQuizIdAndUserId(Long quizId, Long userId) {
        return quizResultRepository.findByQuizIdAndUserId(quizId, userId)
                .orElse(null);
    }
} 