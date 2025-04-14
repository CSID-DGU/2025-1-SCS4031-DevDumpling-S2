package com.example.dummy.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "investment_transactions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvestmentTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String accountNumber;

    @Column(nullable = false)
    private LocalDate transactionDate;

    @Column(nullable = false)
    private String transactionType;

    @Column(nullable = false)
    private String stockName;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false)
    private double price;

    @Column(nullable = false)
    private long amount;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
} 