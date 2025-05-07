package com.example.dummy.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "card_transactions")
@EqualsAndHashCode(callSuper=false)
public class CardTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String cardId;

    @Column(nullable = false)
    private LocalDate transactionDate;

    @Column(nullable = false)
    private Long amount;

    @Column(nullable = false)
    private String storeName;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false)
    @Builder.Default
    private Long cashbackAmount = 0L;

    @Column(nullable = false)
    @Builder.Default
    private String cashbackType = "기본";

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private TransactionType transactionType = TransactionType.PURCHASE;

    @Column
    private Long originalTransactionId;

    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    public enum TransactionType {
        PURCHASE,    // 구매
        REFUND,      // 환불
        CANCELLATION, // 취소
        CASHBACK     // 캐시백
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
} 