package com.example.repository;

import com.example.entity.FinanceCompany;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FinanceCompanyRepository extends JpaRepository<FinanceCompany, Long> {
    boolean existsByFinCoNo(String finCoNo);
    Optional<FinanceCompany> findByKorCoNm(String korCoNm);
} 