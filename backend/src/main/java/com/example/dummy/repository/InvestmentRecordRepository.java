package com.example.dummy.repository;

import com.example.dummy.entity.InvestmentRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface InvestmentRecordRepository extends JpaRepository<InvestmentRecord, Long> {
    List<InvestmentRecord> findByUserId(Long userId);
    Optional<InvestmentRecord> findByAccountNumber(String accountNumber);
} 