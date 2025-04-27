package com.example.dummy.repository;

import com.example.dummy.entity.InvestmentRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InvestmentRecordRepository extends JpaRepository<InvestmentRecord, Long> {
    List<InvestmentRecord> findByUserId(Long userId);
    Optional<InvestmentRecord> findByUserIdAndAccountNumber(Long userId, String accountNumber);
} 