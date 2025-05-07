package com.example.repository;

import com.example.entity.CheckCard;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CheckCardRepository extends JpaRepository<CheckCard, Long> {
} 