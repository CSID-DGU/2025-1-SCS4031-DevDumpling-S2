package com.example.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "credit_loan_options")
public class CreditLoanOption {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "credit_loan_id")
    private CreditLoan creditLoan;

    @Column(name = "crdt_lend_rate_type")
    private String crdtLendRateType;   // 금리구분 코드

    @Column(name = "crdt_lend_rate_type_nm")
    private String crdtLendRateTypeNm; // 금리구분명

    @Column(name = "crdt_grad_1")
    private Double crdtGrad1;          // 신용등급 1등급 금리

    @Column(name = "crdt_grad_2")
    private String crdtGrad2;          // 신용등급 2등급

    @Column(name = "crdt_grad_3")
    private String crdtGrad3;          // 신용등급 3등급

    @Column(name = "crdt_grad_4")
    private Double crdtGrad4;          // 신용등급 4등급 금리

    @Column(name = "crdt_grad_5")
    private Double crdtGrad5;          // 신용등급 5등급 금리

    @Column(name = "crdt_grad_6")
    private Double crdtGrad6;          // 신용등급 6등급 금리

    @Column(name = "crdt_grad_7")
    private String crdtGrad7;          // 신용등급 7등급

    @Column(name = "crdt_grad_8")
    private String crdtGrad8;          // 신용등급 8등급

    @Column(name = "crdt_grad_9")
    private String crdtGrad9;          // 신용등급 9등급

    @Column(name = "crdt_grad_10")
    private Double crdtGrad10;         // 신용등급 10등급 금리

    @Column(name = "crdt_grad_avg")
    private Double crdtGradAvg;        // 평균 금리
} 