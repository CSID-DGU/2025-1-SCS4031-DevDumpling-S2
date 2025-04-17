package com.example.repository;

import com.example.entity.FinanceProductOption;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FinanceProductOptionRepository extends JpaRepository<FinanceProductOption, Long> {
    void deleteByProductId(Long productId);
} 