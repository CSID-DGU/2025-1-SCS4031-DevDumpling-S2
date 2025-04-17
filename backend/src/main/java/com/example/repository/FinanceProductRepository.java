package com.example.repository;

import com.example.entity.FinanceProduct;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FinanceProductRepository extends JpaRepository<FinanceProduct, Long> {
    boolean existsByFinCoNoAndFinPrdtCd(String finCoNo, String finPrdtCd);
} 