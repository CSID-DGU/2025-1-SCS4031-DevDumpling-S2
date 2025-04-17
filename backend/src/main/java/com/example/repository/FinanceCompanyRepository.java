package com.example.repository;

import com.example.entity.FinanceCompany;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FinanceCompanyRepository extends JpaRepository<FinanceCompany, Long> {
    boolean existsByFinCoNo(String finCoNo);
} 