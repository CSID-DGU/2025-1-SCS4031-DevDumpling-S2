package com.example.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "finance_product_options")
@Getter
@Setter
public class FinanceProductOption {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private FinanceProduct product;

    @Column(name = "dcls_month")
    private String dclsMonth;

    @Column(name = "fin_co_no")
    private String finCoNo;

    @Column(name = "fin_prdt_cd")
    private String finPrdtCd;

    @Column(name = "intr_rate_type")
    private String intrRateType;

    @Column(name = "intr_rate_type_nm")
    private String intrRateTypeNm;

    @Column(name = "rsrv_type")
    private String rsrvType;

    @Column(name = "rsrv_type_nm")
    private String rsrvTypeNm;

    @Column(name = "save_trm")
    private Integer saveTrm;

    @Column(name = "intr_rate")
    private Double intrRate;

    @Column(name = "intr_rate2")
    private Double intrRate2;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
} 