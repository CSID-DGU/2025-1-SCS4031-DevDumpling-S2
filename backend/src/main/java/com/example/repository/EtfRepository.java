package com.example.repository;

import com.example.entity.Etf;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface EtfRepository extends JpaRepository<Etf, Long> {
    
    List<Etf> findByBasDd(@Param("basDd") String basDd);
    
    Optional<Etf> findByBasDdAndIsuCd(@Param("basDd") String basDd, @Param("isuCd") String isuCd);
    
    @Query("SELECT e FROM Etf e WHERE e.basDd = :basDd AND e.mktNm = :mktNm")
    List<Etf> findByBasDdAndMktNm(@Param("basDd") String basDd, @Param("mktNm") String mktNm);
    
    List<Etf> findByBasDdAndIdxIndNm(@Param("basDd") String basDd, @Param("idxIndNm") String idxIndNm);
    
    @Query("SELECT e FROM Etf e WHERE e.basDd = :basDd AND e.nav >= :minNav AND e.nav <= :maxNav")
    List<Etf> findByBasDdAndNavRange(
        @Param("basDd") String basDd,
        @Param("minNav") Float minNav,
        @Param("maxNav") Float maxNav
    );
    
    @Query("SELECT e FROM Etf e WHERE e.basDd = :basDd AND e.mktcap >= :minMktcap")
    List<Etf> findByBasDdAndMinMktcap(
        @Param("basDd") String basDd,
        @Param("minMktcap") Long minMktcap
    );
} 