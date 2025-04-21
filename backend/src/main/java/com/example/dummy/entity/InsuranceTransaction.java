package com.example.dummy.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "insurance_transaction")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InsuranceTransaction {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "insurance_id", nullable = false) // 보험 계약 번호
    private String insuranceId;

    @Column(name = "payment_date", nullable = false) // 납기일
    private LocalDate paymentDate;

    @Column(name = "payment_cycle", nullable = false) // 납기 주기
    private Integer paymentCycle;

    @Column(name = "payment_amount", nullable = false) // 납기 금액
    private Long paymentAmount;

    @Column(name = "payment_method", nullable = false) // 납기 방법
    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    @Column(name = "created_at", nullable = false, updatable = false) // 생성일
    private LocalDateTime createdAt;

    public enum PaymentMethod {
        AUTO_TRANSFER,    // 자동이체
        CREDIT_CARD,     // 신용카드
        BANK_TRANSFER,   // 계좌이체
        CASH,           // 현금
        CHECK           // 수표
    }
} 