package com.example.dummy.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "loan_transaction")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoanTransaction {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "loan_id", nullable = false)
    private String loanId;

    @Column(name = "user_id", nullable = false) 
    private Long userId;

    @Column(name = "transaction_date", nullable = false) // 거래 일자
    private LocalDate transactionDate;

    @Column(name = "transaction_type", nullable = false) // 거래 타입
    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;

    @Column(name = "amount", nullable = false) // 거래 금액
    private Long amount;

    @Column(name = "principal_paid") // 원금 납입
    private Long principalPaid;

    @Column(name = "interest_paid") // 이자 납입
    private Long interestPaid;

    @Column(name = "interest_rate") // 이자율
    private Double interestRate;

    @Column(name = "is_auto_payment") // 자동 납입 여부
    private Boolean isAutoPayment;

    @Column(name = "created_at", nullable = false, updatable = false) // 생성일
    private LocalDateTime createdAt;

    public enum TransactionType {
        PRINCIPAL_REPAYMENT,    // 원금상환
        INTEREST_PAYMENT,       // 이자납입
        LATE_INTEREST,         // 연체이자
        LATE_FEE,             // 연체료
        FEE,                  // 수수료
        ADJUSTMENT            // 조정
    }
} 