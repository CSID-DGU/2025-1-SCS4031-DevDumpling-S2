package com.example.repository;

import com.example.entity.QuizResult;
import com.example.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

//사용자의 퀴즈 결과 조회
//최신순으로 정렬된 결과 조회
//정답 수와 전체 답변 수 카운트
public interface QuizResultRepository extends JpaRepository<QuizResult, Long> {
    List<QuizResult> findByUser(User user);
    
    @Query("SELECT qr FROM QuizResult qr WHERE qr.user = :user ORDER BY qr.createdAt DESC")
    List<QuizResult> findByUserOrderByCreatedAtDesc(@Param("user") User user);
    
    @Query("SELECT COUNT(qr) FROM QuizResult qr WHERE qr.user = :user AND qr.isCorrect = true")
    long countCorrectAnswersByUser(@Param("user") User user);
    
    @Query("SELECT COUNT(qr) FROM QuizResult qr WHERE qr.user = :user")
    long countTotalAnswersByUser(@Param("user") User user);

    @Query("SELECT qr FROM QuizResult qr WHERE qr.quiz.id = :quizId AND qr.user.id = :userId")
    Optional<QuizResult> findByQuizIdAndUserId(@Param("quizId") Long quizId, @Param("userId") Long userId);
} 