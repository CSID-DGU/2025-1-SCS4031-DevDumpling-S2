package com.example.dummy.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "investment_records")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvestmentRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false, unique = true)
    private String accountNumber;

    @Column(nullable = false)
    private String accountName;

    @Column(nullable = false)
    private String investmentType;

    @Column(nullable = false)
    private LocalDate openDate;

    @Column(nullable = false)
    private Long balance;

    @Column(nullable = false)
    private LocalDate recentTransactionDate;

    @Column(nullable = false)
    private Long recentAmount;

    @Column(nullable = false)
    private String recentStock;

    @Column(nullable = false)
    private LocalDateTime createdAt;
} 