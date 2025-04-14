package com.example.dummy.repository;

import com.example.dummy.entity.BankBalance;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface BankBalanceRepository extends JpaRepository<BankBalance, Long> {
    List<BankBalance> findByUserId(Long userId);
    Optional<BankBalance> findByAccountNumber(String accountNumber);
} 