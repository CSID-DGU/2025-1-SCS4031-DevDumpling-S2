package com.example.service;

import com.example.api.GeminiClient;
import com.example.entity.Article;
import com.example.entity.Quiz;
import com.example.entity.UserType;
import com.example.repository.QuizRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;


import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class QuizService {

    private final QuizRepository quizRepository;
    private final GeminiClient geminiClient;

    public void generateQuizzesForArticle(Article article) {
        for (UserType type : UserType.values()) {
            String rawQuiz = geminiClient.generateQuiz(article.getSummary(), getUserTypeName(type));
            Quiz quiz = parseQuiz(rawQuiz, type, article);
            quizRepository.save(quiz);
        }
    }

    private String getUserTypeName(UserType type) {
        return switch (type) {
            case A -> "금융 초보형";
            case B -> "신용 도전형";
            case C -> "투자 지향형";
            case D -> "절약 성향형";
        };
    }

    private Quiz parseQuiz(String response, UserType type, Article article) {
        String question = extractLine(response, "Q:");
        String options = extractLine(response, "보기:");
        String answer = extractLine(response, "정답:");
        String explanation = extractLine(response, "해설:");

        String typeStr = (options == null || options.isBlank()) ? "OX" : "객관식";

        return Quiz.builder()
            .article(article)
            .userType(type)
            .question(question)
            .options(options)
            .answer(answer)
            .type(typeStr)
            .explanation(explanation)
            .build();
    }

    private String extractLine(String text, String prefix) {
        return Arrays.stream(text.split("\n"))
                     .filter(line -> line.startsWith(prefix))
                     .map(line -> line.replace(prefix, "").trim())
                     .findFirst()
                     .orElse("");
    }
}
