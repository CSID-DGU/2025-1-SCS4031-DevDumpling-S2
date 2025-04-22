package com.example.repository;

import com.example.entity.FinanceProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FinanceProductRepository extends JpaRepository<FinanceProduct, Long> {
    boolean existsByFinCoNoAndFinPrdtCd(String finCoNo, String finPrdtCd);
    List<FinanceProduct> findByProductType(String productType);
} 