package com.example.repository;

import com.example.entity.CreditCard;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CreditCardRepository extends JpaRepository<CreditCard, Long> {
    Optional<CreditCard> findByCardName(String cardName);
} 