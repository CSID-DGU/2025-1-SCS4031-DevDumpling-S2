package com.example.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CreditCard {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String cardName;
    private String company;
    private String detailUrl;
    private String imageUrl;

    @Column(length = 1000)
    private String benefits;
} 