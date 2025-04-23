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
@Table(name = "rent_house_loan_options")
public class RentHouseLoanOption {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rent_house_loan_id")
    private RentHouseLoan rentHouseLoan;

    @Column(name = "crdt_lend_rate_type")
    private String crdtLendRateType;      // 대출금리유형

    @Column(name = "crdt_lend_rate_type_nm")
    private String crdtLendRateTypeNm;    // 대출금리유형명

    @Column(name = "crdt_grad_1")
    private Double crdtGrad1;             // 900점 초과

    @Column(name = "crdt_grad_4")
    private Double crdtGrad4;             // 801~900점

    @Column(name = "crdt_grad_5")
    private Double crdtGrad5;             // 701~800점

    @Column(name = "crdt_grad_6")
    private Double crdtGrad6;             // 601~700점

    @Column(name = "crdt_grad_10")
    private Double crdtGrad10;            // 501~600점

    @Column(name = "crdt_grad_11")
    private Double crdtGrad11;            // 401~500점

    @Column(name = "crdt_grad_12")
    private Double crdtGrad12;            // 301~400점

    @Column(name = "crdt_grad_13")
    private Double crdtGrad13;            // 300점 이하

    @Column(name = "crdt_grad_avg")
    private Double crdtGradAvg;           // 평균금리
} 