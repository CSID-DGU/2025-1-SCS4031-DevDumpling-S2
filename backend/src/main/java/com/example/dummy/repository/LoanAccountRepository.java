package com.example.dummy.repository;

import com.example.dummy.entity.LoanAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LoanAccountRepository extends JpaRepository<LoanAccount, Long> {
    List<LoanAccount> findByUserId(Long userId);
    LoanAccount findByLoanId(String loanId);
    Optional<LoanAccount> findByUserIdAndLoanId(Long userId, String loanId);
} 