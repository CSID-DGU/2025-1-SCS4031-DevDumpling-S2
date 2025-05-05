package com.example.repository;

import com.example.entity.CheckCard;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CheckCardRepository extends JpaRepository<CheckCard, Long> {
    Optional<CheckCard> findByCardName(String cardName);
} 