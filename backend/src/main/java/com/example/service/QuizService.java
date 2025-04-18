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
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class QuizService {

    private final QuizRepository quizRepository;
    private final GeminiClient geminiClient;

    @Transactional
    public void generateQuizzesForArticle(Article article) {
        try {
            log.info("[퀴즈 서비스] 퀴즈 생성 시작 - 기사: {}", article.getTitle());
            
            // 한 번의 Gemini 호출로 모든 유형의 퀴즈 생성
            String response = geminiClient.generateQuiz(article.getContent(), "ALL", 4);
            
            if (response == null) {
                log.error("[퀴즈 서비스] Gemini 응답이 null입니다.");
                return;
            }

            // 각 유형별 퀴즈 파싱
            String[] quizSections = response.split("\\[");
            for (int i = 1; i < quizSections.length; i++) {
                String section = quizSections[i].trim();
                if (section.isEmpty()) continue;

                // 유형 추출
                String type = section.substring(0, section.indexOf("]")).trim();
                section = section.substring(section.indexOf("]") + 1).trim();

                // UserType 설정
                UserType userType;
                switch (type) {
                    case "금융 초보형":
                        userType = UserType.A;
                        break;
                    case "신용 도전형":
                        userType = UserType.B;
                        break;
                    case "투자 지향형":
                        userType = UserType.C;
                        break;
                    case "절약 성향형":
                        userType = UserType.D;
                        break;
                    default:
                        log.warn("[퀴즈 서비스] 알 수 없는 유형: {}", type);
                        continue;
                }

                // 이미 해당 유형의 퀴즈가 있는지 확인
                if (quizRepository.existsByArticleAndUserType(article, userType)) {
                    log.info("[퀴즈 서비스] 이미 존재하는 유형의 퀴즈입니다 - userType: {}", userType);
                    continue;
                }

                // 문제 추출
                String question = "";
                int questionStart = section.indexOf("문제:");
                int questionEnd = section.indexOf("#");
                if (questionStart != -1) {
                    question = section.substring(questionStart + 3, 
                                              questionEnd != -1 ? questionEnd : section.indexOf("정답:")).trim();
                }

                // 보기 추출 (4지선다인 경우)
                String[] options = new String[0];
                if (section.contains("1)")) {
                    int optionsStart = section.indexOf("1)");
                    int optionsEnd = section.indexOf("정답:");
                    if (optionsStart != -1 && optionsEnd != -1) {
                        String optionsText = section.substring(optionsStart, optionsEnd).trim();
                        options = optionsText.split("\\n");
                        for (int j = 0; j < options.length; j++) {
                            options[j] = options[j].substring(options[j].indexOf(")") + 1).trim();
                        }
                    }
                } else {
                    // O/X 문제인 경우
                    options = new String[]{"O", "X"};
                }

                // 정답 추출
                String answer = "";
                int answerStart = section.indexOf("정답:");
                int answerEnd = section.indexOf("해설:");
                if (answerStart != -1 && answerEnd != -1) {
                    answer = section.substring(answerStart + 3, answerEnd).trim();
                }

                // 해설 추출
                String explanation = "";
                int explanationStart = section.indexOf("해설:");
                if (explanationStart != -1) {
                    explanation = section.substring(explanationStart + 3).trim();
                }

                Quiz quiz = new Quiz();
                quiz.setArticle(article);
                quiz.setUserType(userType);
                quiz.setQuestion(question);
                quiz.setQuizType(options.length > 2 ? "MULTIPLE_CHOICE" : "OX");
                quiz.setOptions(Arrays.toString(options));
                quiz.setAnswer(answer);
                quiz.setExplanation(explanation);

                quizRepository.save(quiz);
                log.info("[퀴즈 서비스] 퀴즈 저장 완료 - userType: {}, quizType: {}", userType, quiz.getQuizType());
            }
            
            log.info("[퀴즈 서비스] 퀴즈 생성 완료 - 기사: {}", article.getTitle());

        } catch (Exception e) {
            log.error("[퀴즈 서비스] 퀴즈 생성 중 오류 발생: {}", e.getMessage(), e);
        }
    }

    private boolean isInvalidField(String field) {
        return field == null || 
               field.trim().isEmpty() || 
               field.equals("처리 중...") || 
               field.equals("기사 처리에 실패했습니다.");
    }

    public List<Quiz> findByArticleId(Long articleId) {
        return quizRepository.findByArticleId(articleId);
    }

    public List<Quiz> findByUserType(UserType userType) {
        return quizRepository.findByUserType(userType);
    }

    public Quiz findByArticleIdAndUserType(Long articleId, UserType userType) {
        List<Quiz> quizzes = quizRepository.findByArticleIdAndUserType(articleId, userType);
        return quizzes.isEmpty() ? null : quizzes.get(0);  // 가장 최근 퀴즈(ID가 가장 큰 것) 반환
    }

    public Quiz findById(Long id) {
        return quizRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Quiz not found with id: " + id));
    }
}
