package com.example.dummy.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "loan_account")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoanAccount {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "loan_id", nullable = false, unique = true)
    private String loanId;

    @Column(name = "loan_type", nullable = false) // 대출 타입
    @Enumerated(EnumType.STRING)
    private LoanType loanType;

    @Column(name = "institution_name", nullable = false) // 기관 이름
    private String institutionName;

    @Column(name = "product_name", nullable = false) // 상품 이름
    private String productName;

    @Column(name = "contract_date", nullable = false) // 계약 일자
    private LocalDate contractDate;

    @Column(name = "maturity_date", nullable = false) // 만기 일자
    private LocalDate maturityDate;

    @Column(name = "principal_amount", nullable = false) // 원금
    private Long principalAmount;

    @Column(name = "remaining_principal", nullable = false) // 남은 원금
    private Long remainingPrincipal;

    @Column(name = "interest_rate", nullable = false) // 이자율
    private Double interestRate;

    @Column(name = "repayment_type", nullable = false) // 상환 타입
    @Enumerated(EnumType.STRING)
    private RepaymentType repaymentType;

    @Column(name = "monthly_due_day") // 월 납기일
    private Integer monthlyDueDay;

    @Column(name = "next_interest_due") // 다음 이자 납기일
    private LocalDate nextInterestDue;

    @Column(name = "is_short_term") // 단기 대출 여부
    private Boolean isShortTerm;

    @Column(name = "is_long_term") // 장기 대출 여부
    private Boolean isLongTerm;

    @Column(name = "is_overdue") // 연체 여부
    private Boolean isOverdue;

    @Column(name = "is_active", nullable = false) // 활성화 여부
    @Builder.Default
    private Boolean isActive = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public enum LoanType {
        CREDIT,     // 신용대출
        RENT        // 전세대출
    }

    public enum RepaymentType {
        PRINCIPAL_INTEREST,          // 원금+이자 상환
        PRINCIPAL,                   // 원금만 상환
        INTEREST                     // 이자만 상환
    }
} 