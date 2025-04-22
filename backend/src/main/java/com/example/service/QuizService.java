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
import java.util.Map;
import java.util.HashMap;
import java.util.LinkedHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class QuizService {

    private final QuizRepository quizRepository;
    private final GeminiClient geminiClient;
    private final ObjectMapper objectMapper;

    @Transactional
    public void generateQuizzesForArticle(Article article) {
        try {
            log.info("[퀴즈 서비스] 퀴즈 생성 시작 - 기사 ID: {}, 제목: {}", article.getId(), article.getTitle());
            
            // 각 사용자 타입별로 퀴즈 생성
            for (UserType userType : UserType.values()) {
                log.info("[퀴즈 서비스] {} 타입 퀴즈 생성 시작", userType);
                
                // 이미 해당 유형의 퀴즈가 있는지 확인
                if (quizRepository.existsByArticleAndUserType(article, userType)) {
                    log.info("[퀴즈 서비스] 이미 존재하는 유형의 퀴즈입니다 - 기사 ID: {}, userType: {}", article.getId(), userType);
                    continue;
                }

                // Gemini 호출 시 사용자 타입을 전달
                log.info("[퀴즈 서비스] Gemini API 호출 - 기사 ID: {}, userType: {}", article.getId(), userType);
                String response = geminiClient.generateQuiz(article.getContent(), userType.name(), 4);
                
                if (response == null) {
                    log.error("[퀴즈 서비스] Gemini 응답이 null입니다 - 기사 ID: {}, userType: {}", article.getId(), userType);
                    continue;
                }
                log.info("[퀴즈 서비스] Gemini 응답 수신 - 기사 ID: {}, userType: {}, 응답 길이: {}", 
                    article.getId(), userType, response.length());

                // 문제 추출
                String question = "";
                int questionStart = response.indexOf("문제:");
                int questionEnd = response.indexOf("보기:");
                if (questionStart != -1) {
                    question = response.substring(questionStart + 3, 
                                              questionEnd != -1 ? questionEnd : response.indexOf("정답:")).trim();
                    log.info("[퀴즈 서비스] 문제 추출 완료 - 기사 ID: {}, userType: {}, 문제 길이: {}", 
                        article.getId(), userType, question.length());
                }

                // 보기 추출 (4지선다)
                Map<String, String> optionsMap = new LinkedHashMap<>(); // 순서 보장을 위해 LinkedHashMap 사용
                if (response.contains("1)")) {
                    int optionsStart = response.indexOf("1)");
                    int optionsEnd = response.indexOf("정답:");
                    if (optionsStart != -1 && optionsEnd != -1) {
                        String optionsText = response.substring(optionsStart, optionsEnd).trim();
                        String[] options = optionsText.split("\\n");
                        for (int j = 0; j < options.length; j++) {
                            String option = options[j].substring(options[j].indexOf(")") + 1).trim();
                            optionsMap.put("option" + (j + 1), option);
                        }
                        log.info("[퀴즈 서비스] 보기 추출 완료 - 기사 ID: {}, userType: {}, 보기 개수: {}", 
                            article.getId(), userType, optionsMap.size());
                    }
                }

                // 정답 추출 (보기 내용 그대로)
                String answer = "";
                int answerStart = response.indexOf("정답:");
                int answerEnd = response.indexOf("해설:");
                if (answerStart != -1 && answerEnd != -1) {
                    String answerText = response.substring(answerStart + 3, answerEnd).trim();
                    
                    // 정답 번호 추출
                    int answerNumber = -1;
                    if (answerText.matches("^\\s*[1-4]\\s*[).]?\\s*.*")) {
                        // "1)", "1.", "1" 등 다양한 형식 처리
                        answerNumber = Integer.parseInt(answerText.replaceAll("[^1-4]", ""));
                    }
                    
                    if (answerNumber >= 1 && answerNumber <= 4) {
                        // 해당 번호의 보기 내용을 정답으로 저장 (번호.보기내용 형식)
                        String optionContent = optionsMap.get("option" + answerNumber);
                        answer = answerNumber + "." + optionContent;
                        log.info("[퀴즈 서비스] 정답 추출 완료 - 기사 ID: {}, userType: {}, 정답: {}", 
                            article.getId(), userType, answer);
                    } else {
                        log.error("[퀴즈 서비스] 정답 번호가 올바르지 않아 퀴즈 생성을 건너뜁니다 - 기사 ID: {}, userType: {}, 원본 정답: {}", 
                            article.getId(), userType, answerText);
                        continue; // 정답 번호가 올바르지 않으면 해당 퀴즈 건너뛰기
                    }
                }

                // 해설 추출
                String explanation = "";
                int explanationStart = response.indexOf("해설:");
                if (explanationStart != -1) {
                    explanation = response.substring(explanationStart + 3).trim();
                    log.info("[퀴즈 서비스] 해설 추출 완료 - 기사 ID: {}, userType: {}, 해설 길이: {}", 
                        article.getId(), userType, explanation.length());
                }

                Quiz quiz = new Quiz();
                quiz.setArticle(article);
                quiz.setUserType(userType);
                quiz.setQuestion(question);
                quiz.setOptions(objectMapper.writeValueAsString(optionsMap));
                quiz.setAnswer(answer);
                quiz.setExplanation(explanation);

                quizRepository.save(quiz);
                log.info("[퀴즈 서비스] 퀴즈 저장 완료 - 기사 ID: {}, userType: {}, 퀴즈 ID: {}", 
                    article.getId(), userType, quiz.getId());
            }
            
            log.info("[퀴즈 서비스] 퀴즈 생성 완료 - 기사 ID: {}, 제목: {}", article.getId(), article.getTitle());

        } catch (Exception e) {
            log.error("[퀴즈 서비스] 퀴즈 생성 중 오류 발생 - 기사 ID: {}, 제목: {}, 오류: {}", 
                article.getId(), article.getTitle(), e.getMessage(), e);
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
        return quizzes.isEmpty() ? null : quizzes.get(0);
    }

    public Quiz findById(Long id) {
        return quizRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Quiz not found with id: " + id));
    }

    private UserType mapUserType(String type) {
        return switch (type) {
            case "A" -> UserType.A;
            case "B" -> UserType.B;
            case "C" -> UserType.C;
            case "D" -> UserType.D;
            default -> {
                log.warn("알 수 없는 유형: {}", type);
                yield UserType.A;
            }
        };
    }
}
