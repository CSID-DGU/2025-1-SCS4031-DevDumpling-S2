package com.example.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "rent_house_loans")
public class RentHouseLoan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "dcls_month", nullable = false)
    private String dclsMonth;      // 공시월

    @Column(name = "fin_co_no", nullable = false)
    private String finCoNo;        // 금융회사 코드

    @Column(name = "kor_co_nm", nullable = false)
    private String korCoNm;        // 금융회사명

    @Column(name = "fin_prdt_cd", nullable = false)
    private String finPrdtCd;      // 금융상품 코드

    @Column(name = "fin_prdt_nm", nullable = false)
    private String finPrdtNm;      // 금융상품명

    @Column(name = "join_way")
    private String joinWay;        // 가입방법

    @Column(name = "loan_inci_expn")
    private String loanInciExpn;   // 대출 부대비용

    @Column(name = "erly_rpay_fee")
    private String erlyRpayFee;    // 중도상환 수수료

    @Column(name = "dly_rate")
    private String dlyRate;        // 연체이자율

    @Column(name = "loan_lmt")
    private String loanLmt;        // 대출한도

    @Column(name = "dcls_strt_day")
    private String dclsStrtDay;    // 공시 시작일

    @Column(name = "dcls_end_day")
    private String dclsEndDay;     // 공시 종료일

    @OneToMany(mappedBy = "rentHouseLoan", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RentHouseLoanOption> options = new ArrayList<>();
} 