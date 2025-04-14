package com.example.dummy.repository;

import com.example.dummy.entity.BankTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface BankTransactionRepository extends JpaRepository<BankTransaction, Long> {
    List<BankTransaction> findByUserId(Long userId);
    List<BankTransaction> findByAccountNumber(String accountNumber);
    List<BankTransaction> findByUserIdAndTransactionDateBetween(
        Long userId, LocalDateTime startDate, LocalDateTime endDate);
} 