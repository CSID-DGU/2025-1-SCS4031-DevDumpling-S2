package com.example.dummy.repository;

import com.example.dummy.entity.CardSpent;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface CardSpentRepository extends JpaRepository<CardSpent, Long> {
    List<CardSpent> findByUserId(Long userId);
    Optional<CardSpent> findByCardId(String cardId);
    List<CardSpent> findByUserIdAndBillingYearMonth(Long userId, String billingYearMonth);
} 