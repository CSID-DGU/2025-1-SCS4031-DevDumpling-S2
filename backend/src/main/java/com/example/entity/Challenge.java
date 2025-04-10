package com.example.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.time.LocalDate;

@Entity
@Table(name = "Challenge")
@Data
@NoArgsConstructor
@AllArgsConstructor 
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
}
