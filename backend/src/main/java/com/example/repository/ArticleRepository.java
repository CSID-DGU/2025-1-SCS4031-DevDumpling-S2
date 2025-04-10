package com.example.repository;

import com.example.entity.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ArticleRepository extends JpaRepository<Article, Long> {
    boolean existsByTitle(String title);
    List<Article> findAll();
}