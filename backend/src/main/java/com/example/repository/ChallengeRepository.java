package com.example.repository;

import com.example.entity.Challenge;
import com.example.entity.Challenge.ChallengeCategory;
import com.example.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ChallengeRepository extends JpaRepository<Challenge, Long> {
    List<Challenge> findByUser(User user);
    Optional<Challenge> findByInviteCode(String inviteCode);
    
    // 카테고리별 챌린지 조회 (페이징)
    Page<Challenge> findByCategory(ChallengeCategory category, Pageable pageable);
    
    // 카테고리별 진행 중인 챌린지 조회 (시작일 <= 현재 <= 종료일)
    @Query("SELECT c FROM Challenge c WHERE c.category = :category " +
           "AND c.startDate <= :currentDate AND c.endDate >= :currentDate")
    Page<Challenge> findActiveChallengesByCategory(
        ChallengeCategory category, 
        LocalDate currentDate, 
        Pageable pageable
    );
    
    // 카테고리별 예정된 챌린지 조회 (시작일 > 현재)
    @Query("SELECT c FROM Challenge c WHERE c.category = :category " +
           "AND c.startDate > :currentDate")
    Page<Challenge> findUpcomingChallengesByCategory(
        ChallengeCategory category, 
        LocalDate currentDate, 
        Pageable pageable
    );
    
    // 카테고리별 완료된 챌린지 조회 (종료일 < 현재)
    @Query("SELECT c FROM Challenge c WHERE c.category = :category " +
           "AND c.endDate < :currentDate")
    Page<Challenge> findCompletedChallengesByCategory(
        ChallengeCategory category, 
        LocalDate currentDate, 
        Pageable pageable
    );

    @Query("SELECT c FROM Challenge c WHERE c.endDate < :today AND c.isCompleted = false")
    List<Challenge> findByEndDateBeforeAndIsCompletedFalse(@Param("today") LocalDate today);

    @Query("SELECT c FROM Challenge c " +
           "JOIN Participation p ON c = p.challenge " +
           "WHERE p.user = :user " +
           "AND c.startDate <= :currentDate " +
           "AND c.endDate >= :currentDate " +
           "AND c.isDeleted = false " +
           "AND c.isCompleted = false")
    Page<Challenge> findUserParticipatingChallenges(
        @Param("user") User user,
        @Param("currentDate") LocalDate currentDate,
        Pageable pageable
    );

    @Query("SELECT c FROM Challenge c WHERE c.isCompleted = false AND c.isDeleted = false")
    List<Challenge> findByIsCompletedFalseAndDeletedFalse();
} 