package com.example.repository;

import com.example.entity.CheckCard;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CheckCardRepository extends JpaRepository<CheckCard, Long> {
    List<CheckCard> findByCompany(String company);
} 