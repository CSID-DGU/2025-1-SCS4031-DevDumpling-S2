package com.example.dummy.repository;

import com.example.dummy.entity.InsuranceAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InsuranceAccountRepository extends JpaRepository<InsuranceAccount, Long> {
    List<InsuranceAccount> findByUserId(Long userId);
    InsuranceAccount findByInsuranceId(String insuranceId);
} 