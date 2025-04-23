package com.example.repository;

import com.example.entity.CreditLoanOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CreditLoanOptionRepository extends JpaRepository<CreditLoanOption, Long> {
    List<CreditLoanOption> findByCreditLoan_Id(Long loanId);
    
    void deleteByCreditLoan_Id(Long loanId);

    // 필요한 커스텀 쿼리 메서드를 여기에 추가할 수 있습니다.
} 