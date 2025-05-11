package com.example.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "insurance")
public class Insurance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "company_code")
    private String cmpyCd;

    @Column(name = "company_name")
    private String cmpyNm;

    @Column(name = "pattern")
    private String ptrn;

    @Column(name = "coverage")
    private String mog;

    @Column(name = "product_name")
    private String prdNm;

    @Column(name = "age")
    private String age;

    @Column(name = "male_insurance_rate")
    private String mlInsRt;

    @Column(name = "female_insurance_rate")
    private String fmlInsRt;

    @Column(name = "base_date")
    private String basDt;

    @Column(name = "provider")
    private String ofrInstNm;

    // 기본 생성자, getters & setters
}
