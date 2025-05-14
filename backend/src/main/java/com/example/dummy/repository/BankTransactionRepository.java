package com.example.dummy.repository;

import com.example.dummy.entity.BankTransaction;
import com.example.dummy.entity.BankBalance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BankTransactionRepository extends JpaRepository<BankTransaction, Long> {
    List<BankTransaction> findByUserId(Long userId);
    List<BankTransaction> findByAccountNumber(String accountNumber);
    List<BankTransaction> findByUserIdAndTransactionDateBetween(
        Long userId, LocalDateTime startDate, LocalDateTime endDate);
    
    List<BankTransaction> findByUserIdAndAccountNumberAndTransactionDateBetween(
        Long userId, String accountNumber, LocalDateTime startDate, LocalDateTime endDate);
    
    @Modifying
    @Query("DELETE FROM BankTransaction bt WHERE bt.accountNumber = :accountNumber")
    void deleteByAccountNumber(@Param("accountNumber") String accountNumber);
    
    @Query("SELECT b FROM BankBalance b WHERE b.userId = :userId AND b.accountNumber = :accountNumber")
    Optional<BankBalance> findByUserIdAndAccountNumber(@Param("userId") Long userId, @Param("accountNumber") String accountNumber);
} 