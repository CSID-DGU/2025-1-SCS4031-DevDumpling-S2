package com.example.dummy.repository;

import com.example.dummy.entity.InsuranceTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface InsuranceTransactionRepository extends JpaRepository<InsuranceTransaction, Long> {
    List<InsuranceTransaction> findByUserId(Long userId);
    List<InsuranceTransaction> findByInsuranceId(String insuranceId);
    List<InsuranceTransaction> findByInsuranceIdAndPaymentDateBetween(String insuranceId, LocalDate startDate, LocalDate endDate);
} 