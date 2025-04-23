package com.example.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "credit_loans")
public class CreditLoan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "dcls_month")
    private String dclsMonth;          // 공시 제출월 [YYYYMM]

    @Column(name = "fin_co_no")
    private String finCoNo;            // 금융회사 코드

    @Column(name = "fin_prdt_cd")
    private String finPrdtCd;          // 금융상품 코드

    @Column(name = "crdt_prdt_type")
    private String crdtPrdtType;       // 신용대출 종류 코드

    @Column(name = "kor_co_nm")
    private String korCoNm;            // 금융회사명

    @Column(name = "fin_prdt_nm")
    private String finPrdtNm;          // 금융상품명

    @Column(name = "join_way")
    private String joinWay;            // 가입방법

    @Column(name = "cb_name")
    private String cbName;             // CB 회사명

    @Column(name = "crdt_prdt_type_nm")
    private String crdtPrdtTypeNm;     // 신용대출 종류명

    @Column(name = "dcls_strt_day")
    private String dclsStrtDay;        // 공시 시작일

    @Column(name = "dcls_end_day")
    private String dclsEndDay;         // 공시 종료일

    @Column(name = "fin_co_subm_day")
    private String finCoSubmDay;       // 금융회사 제출일 [YYYYMMDDHH24MI]

    @OneToMany(mappedBy = "creditLoan", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CreditLoanOption> options = new ArrayList<>();
} 