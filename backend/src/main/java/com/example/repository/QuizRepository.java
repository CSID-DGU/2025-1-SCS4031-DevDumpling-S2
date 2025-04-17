package com.example.repository;

import com.example.entity.Quiz;
import com.example.entity.UserType;
import com.example.entity.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface QuizRepository extends JpaRepository<Quiz, Long> {
    List<Quiz> findByArticleId(Long articleId);
    List<Quiz> findByUserType(UserType userType);
    
    @Query("SELECT q FROM Quiz q WHERE q.article.id = :articleId AND q.userType = :userType ORDER BY q.id DESC")
    List<Quiz> findByArticleIdAndUserType(Long articleId, UserType userType);

    boolean existsByArticleAndUserType(Article article, UserType userType);
}