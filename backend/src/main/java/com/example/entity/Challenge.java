package com.example.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "Challenge")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Challenge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long challengeID;

    @ManyToOne
    @JoinColumn(name = "userID")
    private User user;

    private String title;

    @Lob
    private String description;

    private LocalDate startDate;
    private LocalDate endDate;

    private Boolean isOpen;
    private Integer maxParticipants;
    
    @Enumerated(EnumType.STRING)
    private ChallengeType type;
    
    @Enumerated(EnumType.STRING)
    private ChallengeCategory category;
    
    private String inviteCode;

    public enum ChallengeType {
        PUBLIC, PRIVATE
    }

    public enum ChallengeCategory {
        NEW_DISCOUNT, FOOD, CAFE_SNACK, SAVINGS, 
        ALCOHOL_ENTERTAINMENT, SHOPPING, BEAUTY,
        TRAVEL, PET, MART_CONVENIENCE, GAME_OTT,
        HOUSING_COMMUNICATION, TRANSPORTATION, HEALTH_EXERCISE
    }
}
