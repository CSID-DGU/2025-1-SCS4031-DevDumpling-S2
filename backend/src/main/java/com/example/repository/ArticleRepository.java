package com.example.repository;

import com.example.entity.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {
    boolean existsByTitle(String title);
    List<Article> findAll();
    List<Article> findByStatus(Article.ProcessingStatus status);
    long countByStatus(Article.ProcessingStatus status);
    
    // 상태별 기사 수 조회
    default long countPending() {
        return countByStatus(Article.ProcessingStatus.PENDING);
    }
    
    default long countCompleted() {
        return countByStatus(Article.ProcessingStatus.COMPLETED);
    }
    
    default long countFailed() {
        return countByStatus(Article.ProcessingStatus.FAILED);
    }
    
    // 상태별 기사 목록 조회
    default List<Article> findPending() {
        return findByStatus(Article.ProcessingStatus.PENDING);
    }
    
    default List<Article> findCompleted() {
        return findByStatus(Article.ProcessingStatus.COMPLETED);
    }
    
    default List<Article> findFailed() {
        return findByStatus(Article.ProcessingStatus.FAILED);
    }
}