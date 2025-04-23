package com.example.dto.fss;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

@Data
public class RentHouseLoanResponse {
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
        private List<RentHouseLoanProduct> baseList;

        @JsonProperty("optionList")
        private List<RentHouseLoanOption> optionList;
    }

    @Data
    public static class RentHouseLoanProduct {
        @JsonProperty("dcls_month")
        private String dclsMonth;      // 공시월

        @JsonProperty("fin_co_no")
        private String finCoNo;        // 금융회사 코드

        @JsonProperty("kor_co_nm")
        private String korCoNm;        // 금융회사명

        @JsonProperty("fin_prdt_cd")
        private String finPrdtCd;      // 금융상품 코드

        @JsonProperty("fin_prdt_nm")
        private String finPrdtNm;      // 금융상품명

        @JsonProperty("join_way")
        private String joinWay;        // 가입방법

        @JsonProperty("loan_inci_expn")
        private String loanInciExpn;   // 대출 부대비용

        @JsonProperty("erly_rpay_fee")
        private String erlyRpayFee;    // 중도상환 수수료

        @JsonProperty("dly_rate")
        private String dlyRate;        // 연체이자율

        @JsonProperty("loan_lmt")
        private String loanLmt;        // 대출한도

        @JsonProperty("dcls_strt_day")
        private String dclsStrtDay;    // 공시 시작일

        @JsonProperty("dcls_end_day")
        private String dclsEndDay;     // 공시 종료일
    }

    @Data
    public static class RentHouseLoanOption {
        @JsonProperty("dcls_month")
        private String dclsMonth;      // 공시월

        @JsonProperty("fin_co_no")
        private String finCoNo;        // 금융회사 코드

        @JsonProperty("fin_prdt_cd")
        private String finPrdtCd;      // 금융상품 코드

        @JsonProperty("crdt_lend_rate_type")
        private String crdtLendRateType;      // 대출금리유형

        @JsonProperty("crdt_lend_rate_type_nm")
        private String crdtLendRateTypeNm;    // 대출금리유형명

        @JsonProperty("crdt_grad_1")
        private String crdtGrad1;             // 900점 초과

        @JsonProperty("crdt_grad_4")
        private String crdtGrad4;             // 801~900점

        @JsonProperty("crdt_grad_5")
        private String crdtGrad5;             // 701~800점

        @JsonProperty("crdt_grad_6")
        private String crdtGrad6;             // 601~700점

        @JsonProperty("crdt_grad_10")
        private String crdtGrad10;            // 501~600점

        @JsonProperty("crdt_grad_11")
        private String crdtGrad11;            // 401~500점

        @JsonProperty("crdt_grad_12")
        private String crdtGrad12;            // 301~400점

        @JsonProperty("crdt_grad_13")
        private String crdtGrad13;            // 300점 이하

        @JsonProperty("crdt_grad_avg")
        private String crdtGradAvg;           // 평균금리
    }
} 