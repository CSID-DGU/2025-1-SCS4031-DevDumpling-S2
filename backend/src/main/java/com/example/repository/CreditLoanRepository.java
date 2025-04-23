package com.example.repository;

import com.example.entity.CreditLoan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CreditLoanRepository extends JpaRepository<CreditLoan, Long> {
    Optional<CreditLoan> findByFinCoNoAndFinPrdtCd(String finCoNo, String finPrdtCd);
    
    boolean existsByFinCoNoAndFinPrdtCd(String finCoNo, String finPrdtCd);
    
    List<CreditLoan> findByDclsMonth(String dclsMonth);

    // 필요한 커스텀 쿼리 메서드를 여기에 추가할 수 있습니다.
} 