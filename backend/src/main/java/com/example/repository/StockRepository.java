package com.example.repository;

import com.example.entity.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface StockRepository extends JpaRepository<Stock, Long> {
    
    @Query("SELECT s FROM Stock s WHERE s.srtnCd = :srtnCd")
    Optional<Stock> findBySrtnCd(@Param("srtnCd") String srtnCd);
    
    @Query("SELECT s FROM Stock s WHERE s.basDd = :basDd")
    List<Stock> findByBasDd(@Param("basDd") String basDd);
    
    @Query("SELECT s FROM Stock s WHERE s.basDd = :basDd AND s.srtnCd = :srtnCd")
    Optional<Stock> findByBasDdAndSrtnCd(@Param("basDd") String basDd, @Param("srtnCd") String srtnCd);
    
    @Query("SELECT s FROM Stock s WHERE s.basDd = :basDd AND s.mrktCtg = :mrktCtg")
    List<Stock> findByBasDdAndMrktCtg(@Param("basDd") String basDd, @Param("mrktCtg") String mrktCtg);
} 