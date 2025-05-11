package com.example.dto.fss;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper=false)
public class DepositProductResponse extends BaseResponse {
    @JsonProperty("result")
    private Result result;

    @Data
    public static class Result {
        @JsonProperty("baseList")
        private List<BaseProduct> baseList;
        
        @JsonProperty("optionList")
        private List<ProductOption> optionList;
    }

    @Data
    public static class BaseProduct {
        @JsonProperty("dcls_month")
        private String dclsMonth;
        
        @JsonProperty("fin_co_no")
        private String finCoNo;
        
        @JsonProperty("fin_prdt_cd")
        private String finPrdtCd;
        
        @JsonProperty("kor_co_nm")
        private String korCoNm;
        
        @JsonProperty("fin_prdt_nm")
        private String finPrdtNm;
        
        @JsonProperty("join_way")
        private String joinWay;
        
        @JsonProperty("mtrt_int")
        private String mtrtInt;
        
        @JsonProperty("spcl_cnd")
        private String spclCnd;
        
        @JsonProperty("join_deny")
        private String joinDeny;
        
        @JsonProperty("join_member")
        private String joinMember;
        
        @JsonProperty("etc_note")
        private String etcNote;
        
        @JsonProperty("max_limit")
        private Long maxLimit;
        
        @JsonProperty("dcls_strt_day")
        private String dclsStrtDay;
        
        @JsonProperty("dcls_end_day")
        private String dclsEndDay;
        
        @JsonProperty("fin_co_subm_day")
        private String finCoSubmDay;
    }

    @Data
    public static class ProductOption {
        @JsonProperty("dcls_month")
        private String dclsMonth;
        
        @JsonProperty("fin_co_no")
        private String finCoNo;
        
        @JsonProperty("fin_prdt_cd")
        private String finPrdtCd;
        
        @JsonProperty("intr_rate_type")
        private String intrRateType;
        
        @JsonProperty("intr_rate_type_nm")
        private String intrRateTypeNm;
        
        @JsonProperty("save_trm")
        private Integer saveTrm;
        
        @JsonProperty("intr_rate")
        private Double intrRate;
        
        @JsonProperty("intr_rate2")
        private Double intrRate2;
    }
} 