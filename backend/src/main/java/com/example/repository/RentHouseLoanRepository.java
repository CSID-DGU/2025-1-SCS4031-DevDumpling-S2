package com.example.repository;

import com.example.entity.RentHouseLoan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RentHouseLoanRepository extends JpaRepository<RentHouseLoan, Long> {
    Optional<RentHouseLoan> findByFinCoNoAndFinPrdtCd(String finCoNo, String finPrdtCd);
    
    boolean existsByFinCoNoAndFinPrdtCd(String finCoNo, String finPrdtCd);
    
    List<RentHouseLoan> findByDclsMonth(String dclsMonth);
} 