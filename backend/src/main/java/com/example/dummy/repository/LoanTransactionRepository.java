package com.example.dummy.repository;

import com.example.dummy.entity.LoanTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface LoanTransactionRepository extends JpaRepository<LoanTransaction, Long> {
    List<LoanTransaction> findByLoanIdAndTransactionDateBetween(String loanId, LocalDate startDate, LocalDate endDate);
    List<LoanTransaction> findByLoanId(String loanId);
    void deleteByLoanId(String loanId);
} 