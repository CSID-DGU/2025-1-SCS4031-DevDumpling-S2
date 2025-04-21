package com.example.dummy.repository;

import com.example.dummy.entity.LoanAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoanAccountRepository extends JpaRepository<LoanAccount, Long> {
    List<LoanAccount> findByUserId(Long userId);
    LoanAccount findByLoanId(String loanId);
} 