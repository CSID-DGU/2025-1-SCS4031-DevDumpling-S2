package com.example.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.time.LocalDate;

@Entity
@Table(name = "Contents")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Contents {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long contentID;

    @ManyToOne
    @JoinColumn(name = "userID")
    private User user;

    private String title;

    @Lob
    private String content;

    private LocalDateTime createdAt;
}
