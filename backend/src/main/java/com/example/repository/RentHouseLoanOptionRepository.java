package com.example.repository;

import com.example.entity.RentHouseLoanOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RentHouseLoanOptionRepository extends JpaRepository<RentHouseLoanOption, Long> {
    List<RentHouseLoanOption> findByRentHouseLoan_Id(Long loanId);
    
    void deleteByRentHouseLoan_Id(Long loanId);
} 