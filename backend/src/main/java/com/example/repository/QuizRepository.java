package com.example.repository;

import com.example.entity.Quiz;
import com.example.entity.UserType;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface QuizRepository extends JpaRepository<Quiz, Long> {
    List<Quiz> findByArticleId(Long articleId);
    List<Quiz> findByUserType(UserType userType);
    Optional<Quiz> findByArticleIdAndUserType(Long articleId, UserType userType);
}