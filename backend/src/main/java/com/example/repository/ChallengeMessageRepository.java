package com.example.repository;

import com.example.entity.Challenge;
import com.example.entity.ChallengeMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChallengeMessageRepository extends JpaRepository<ChallengeMessage, Long> {
    Page<ChallengeMessage> findByChallengeAndIsDeletedFalseOrderByCreatedAtDesc(
        Challenge challenge, Pageable pageable);
} 