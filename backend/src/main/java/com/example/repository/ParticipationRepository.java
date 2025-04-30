package com.example.repository;

import com.example.entity.Challenge;
import com.example.entity.Participation;
import com.example.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ParticipationRepository extends JpaRepository<Participation, Long> {
    boolean existsByUserAndChallenge(User user, Challenge challenge);
    Long countByChallenge(Challenge challenge);
    List<Participation> findByChallenge(Challenge challenge);
    Optional<Participation> findByUserAndChallenge(User user, Challenge challenge);
} 