package com.example.dto.fss;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

@Data
public class CreditLoanResponse {
    @JsonProperty("result")
    private Result result;

    @Data
    public static class Result {
        @JsonProperty("prdt_div")
        private String prdtDiv;        // 상품구분

        @JsonProperty("total_count")
        private String totalCount;     // 총 건수

        @JsonProperty("max_page_no")
        private String maxPageNo;      // 최대 페이지 수

        @JsonProperty("now_page_no")
        private String nowPageNo;      // 현재 페이지 번호

        @JsonProperty("err_cd")
        private String errCd;          // 에러 코드

        @JsonProperty("err_msg")
        private String errMsg;         // 에러 메시지

        @JsonProperty("baseList")
        private List<CreditLoanProduct> baseList;

        @JsonProperty("optionList")
        private List<CreditLoanOption> optionList;
    }

    @Data
    public static class CreditLoanProduct {
        @JsonProperty("dcls_month")
        private String dclsMonth;      // 공시 제출월 [YYYYMM]

        @JsonProperty("fin_co_no")
        private String finCoNo;        // 금융회사 코드

        @JsonProperty("fin_prdt_cd")
        private String finPrdtCd;      // 금융상품 코드

        @JsonProperty("crdt_prdt_type")
        private String crdtPrdtType;   // 신용대출 종류 코드

        @JsonProperty("kor_co_nm")
        private String korCoNm;        // 금융회사명

        @JsonProperty("fin_prdt_nm")
        private String finPrdtNm;      // 금융상품명

        @JsonProperty("join_way")
        private String joinWay;        // 가입방법

        @JsonProperty("cb_name")
        private String cbName;         // CB 회사명

        @JsonProperty("crdt_prdt_type_nm")
        private String crdtPrdtTypeNm; // 신용대출 종류명

        @JsonProperty("dcls_strt_day")
        private String dclsStrtDay;    // 공시 시작일

        @JsonProperty("dcls_end_day")
        private String dclsEndDay;     // 공시 종료일

        @JsonProperty("fin_co_subm_day")
        private String finCoSubmDay;   // 금융회사 제출일 [YYYYMMDDHH24MI]
    }

    @Data
    public static class CreditLoanOption {
        @JsonProperty("dcls_month")
        private String dclsMonth;      // 공시 제출월 [YYYYMM]

        @JsonProperty("fin_co_no")
        private String finCoNo;        // 금융회사 코드

        @JsonProperty("fin_prdt_cd")
        private String finPrdtCd;      // 금융상품 코드

        @JsonProperty("crdt_lend_rate_type")
        private String crdtLendRateType;   // 금리구분 코드

        @JsonProperty("crdt_lend_rate_type_nm")
        private String crdtLendRateTypeNm; // 금리구분명

        @JsonProperty("crdt_grad_1")
        private String crdtGrad1;      // 신용등급 1등급 금리

        @JsonProperty("crdt_grad_4")
        private String crdtGrad4;      // 신용등급 4등급 금리

        @JsonProperty("crdt_grad_5")
        private String crdtGrad5;      // 신용등급 5등급 금리

        @JsonProperty("crdt_grad_6")
        private String crdtGrad6;      // 신용등급 6등급 금리

        @JsonProperty("crdt_grad_10")
        private String crdtGrad10;     // 신용등급 10등급 금리

        @JsonProperty("crdt_grad_avg")
        private String crdtGradAvg;    // 평균 금리
    }
} 