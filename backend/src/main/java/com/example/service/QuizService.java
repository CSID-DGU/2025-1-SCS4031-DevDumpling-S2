package com.example.service;

import com.example.api.GeminiClient;
import com.example.entity.Article;
import com.example.entity.Quiz;
import com.example.entity.UserType;
import com.example.repository.QuizRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

@Slf4j
@Service
@RequiredArgsConstructor
public class QuizService {

    private final QuizRepository quizRepository;
    private final GeminiClient geminiClient;

    @Transactional
    public void generateQuizzesForArticle(Article article) {
        try {
            String content = article.getContent();
            
            // 한 번의 API 호출로 모든 유형의 퀴즈 생성
            String response = geminiClient.generateQuiz(content, null, 0);
            if (response == null) {
                log.error("[퀴즈 서비스] 퀴즈 생성 실패: {}", article.getTitle());
                return;
            }
            
            // 각 유형별 퀴즈 파싱
            String[] quizzes = response.split("\\[(?=금융 초보형|신용 도전형|투자 지향형|절약 성향형)");
            for (String quizText : quizzes) {
                if (quizText.trim().isEmpty()) continue;
                
                UserType userType = null;
                if (quizText.startsWith("금융 초보형")) userType = UserType.A;
                else if (quizText.startsWith("신용 도전형")) userType = UserType.B;
                else if (quizText.startsWith("투자 지향형")) userType = UserType.C;
                else if (quizText.startsWith("절약 성향형")) userType = UserType.D;
                
                if (userType != null) {
                    Quiz quiz = parseQuizResponse(quizText.trim());
                    if (quiz != null) {
                        quiz.setArticle(article);
                        quiz.setUserType(userType);
                        quiz.setQuizType(quizText.contains("1)") ? "MULTIPLE_CHOICE" : "OX");
                        quizRepository.save(quiz);
                        log.info("[퀴즈 서비스] 퀴즈 생성 완료 - 유형: {}, 사용자 타입: {}", quiz.getQuizType(), userType);
                    }
                }
            }
        } catch (Exception e) {
            log.error("[퀴즈 서비스] 퀴즈 생성 중 에러 발생: {}", article.getTitle(), e);
        }
    }

    private Quiz parseQuizResponse(String response) {
        try {
            Quiz quiz = new Quiz();
            String[] lines = response.split("\n");
            
            StringBuilder optionsBuilder = new StringBuilder();
            boolean isCollectingOptions = false;
            
            for (String line : lines) {
                line = line.trim();
                if (line.isEmpty()) continue;
                
                if (line.startsWith("문제:")) {
                    quiz.setQuestion(line.substring("문제:".length()).trim());
                } else if (line.matches("^[1-4]\\).*")) {
                    isCollectingOptions = true;
                    optionsBuilder.append(line).append("\n");
                } else if (line.startsWith("정답:")) {
                    isCollectingOptions = false;
                    quiz.setAnswer(line.substring("정답:".length()).trim());
                } else if (line.startsWith("해설:")) {
                    quiz.setExplanation(line.substring("해설:".length()).trim());
                }
            }
            
            if (optionsBuilder.length() > 0) {
                quiz.setOptions(optionsBuilder.toString().trim());
                quiz.setQuizType("MULTIPLE_CHOICE");
            } else {
                quiz.setQuizType("OX");
            }
            
            return quiz;
        } catch (Exception e) {
            log.error("[퀴즈 서비스] 퀴즈 파싱 실패: {}", e.getMessage());
            return null;
        }
    }
}
