package com.example.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "finance_company")
public class FinanceCompany {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "dcls_month")
    private String dclsMonth;

    @Column(name = "fin_co_no")
    private String finCoNo;

    @Column(name = "kor_co_nm")
    private String korCoNm;

    @Column(name = "dcls_chrg_man")
    private String dclsChrgMan;

    @Column(name = "home_url")
    private String homeUrl;

    @Column(name = "cal_tel")
    private String calTel;
} 