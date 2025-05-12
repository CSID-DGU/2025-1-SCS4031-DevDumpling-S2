package com.example.dummy.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "insurance_account")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InsuranceAccount {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "insurance_id", nullable = false, unique = true) // 보험 계약 번호
    private String insuranceId;

    @Column(name = "product_name", nullable = false) // 보험 상품명
    private String productName;

    @Column(name = "insurance_type", nullable = false) // 보험 타입
    @Enumerated(EnumType.STRING)
    private InsuranceType insuranceType;

    @Column(name = "contract_status", nullable = false) // 계약 상태
    @Enumerated(EnumType.STRING)
    private ContractStatus contractStatus;

    @Column(name = "contract_date", nullable = false) // 계약 일자
    private LocalDate contractDate;

    @Column(name = "maturity_date") // 만기 일자
    private LocalDate maturityDate;

    @Column(name = "insured_amount", nullable = false) // 보험 가입 금액
    private Long insuredAmount;

    @Column(name = "created_at", nullable = false, updatable = false) // 생성일
    private LocalDateTime createdAt;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = false;  // 기본값은 false로 설정

    public enum InsuranceType {
        LIFE,           // 생명보험
        AUTO,          // 자동차보험
        MEDICAL,       // 의료보험
        FIRE,          // 화재보험
        TRAVEL,        // 여행보험
        ACCIDENT,      // 상해보험
        CANCER,        // 암보험
        PENSION        // 연금보험
    }

    public enum ContractStatus {
        ACTIVE,        // 정상
        CANCELLED,     // 해지
        MATURED,       // 만기
        LAPSED,        // 실효
        SUSPENDED      // 효력정지
    }
} 