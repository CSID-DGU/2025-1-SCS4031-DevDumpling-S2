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
}