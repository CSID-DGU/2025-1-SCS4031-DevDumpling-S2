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
            
            // Gemini 호출 (모든 사용자 타입의 퀴즈를 한 번에 생성)
            String response = geminiClient.generateQuiz(article.getContent(), "ALL", 4);
            
            if (response == null) {
                log.error("[퀴즈 서비스] Gemini 응답이 null입니다 - 기사 ID: {}", article.getId());
                return;
            }
            log.info("[퀴즈 서비스] Gemini 응답 수신 - 기사 ID: {}, 응답 길이: {}", article.getId(), response.length());

            // 각 사용자 타입별로 퀴즈 생성
            for (UserType userType : UserType.values()) {
                log.info("[퀴즈 서비스] {} 타입 퀴즈 생성 시작", userType);
                
                // 이미 해당 유형의 퀴즈가 있는지 확인
                if (quizRepository.existsByArticleAndUserType(article, userType)) {
                    log.info("[퀴즈 서비스] 이미 존재하는 유형의 퀴즈입니다 - 기사 ID: {}, userType: {}", article.getId(), userType);
                    continue;
                }

                // 해당 사용자 타입의 퀴즈 섹션 추출
                String typeSection = extractTypeSection(response, userType);
                if (typeSection == null) {
                    log.error("[퀴즈 서비스] {} 타입의 퀴즈 섹션을 찾을 수 없습니다 - 기사 ID: {}", userType, article.getId());
                    continue;
                }

                // 문제 추출
                String question = "";
                int questionStart = typeSection.indexOf("문제:");
                int questionEnd = typeSection.indexOf("보기:");
                if (questionStart != -1) {
                    question = typeSection.substring(questionStart + 3, 
                                              questionEnd != -1 ? questionEnd : typeSection.indexOf("정답:")).trim();
                    log.info("[퀴즈 서비스] 문제 추출 완료 - 기사 ID: {}, userType: {}, 문제 길이: {}", 
                        article.getId(), userType, question.length());
                }

                // 보기 추출 (4지선다)
                Map<String, String> optionsMap = new LinkedHashMap<>();
                if (typeSection.contains("1)")) {
                    int optionsStart = typeSection.indexOf("1)");
                    int optionsEnd = typeSection.indexOf("정답:");
                    if (optionsStart != -1 && optionsEnd != -1) {
                        String optionsText = typeSection.substring(optionsStart, optionsEnd).trim();
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
                int answerStart = typeSection.indexOf("정답:");
                int answerEnd = typeSection.indexOf("해설:");
                if (answerStart != -1 && answerEnd != -1) {
                    String answerText = typeSection.substring(answerStart + 3, answerEnd).trim();
                    
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
                        continue;
                    }
                }

                // 해설 추출
                String explanation = "";
                int explanationStart = typeSection.indexOf("해설:");
                if (explanationStart != -1) {
                    explanation = typeSection.substring(explanationStart + 3).trim();
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

    private String extractTypeSection(String response, UserType userType) {
        String typeMarker = "[" + userType + " 유형]";
        log.info("[퀴즈 서비스] {} 타입 섹션 검색 시작 - 마커: {}", userType, typeMarker);
        
        int startIndex = response.indexOf(typeMarker);
        if (startIndex == -1) {
            log.error("[퀴즈 서비스] {} 타입 섹션을 찾을 수 없습니다", userType);
            return null;
        }
        
        startIndex += typeMarker.length();
        int endIndex = response.indexOf("[", startIndex);
        if (endIndex == -1) {
            endIndex = response.length();
        }
        
        String section = response.substring(startIndex, endIndex).trim();
        log.info("[퀴즈 서비스] {} 타입 섹션 추출 완료 - 길이: {}", userType, section.length());
        return section;
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
