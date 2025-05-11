package com.example.dto.fss;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper=false)
public class CompanyResponse extends BaseResponse {
    @JsonProperty("result")
    private Result result;

    @Data
    public static class Result {
        @JsonProperty("baseList")
        private List<Company> baseList;
    }

    @Data
    public static class Company {
        @JsonProperty("fin_co_no")
        private String finCoNo;
        
        @JsonProperty("kor_co_nm")
        private String korCoNm;
        
        @JsonProperty("dcls_month")
        private String dclsMonth;
        
        @JsonProperty("cal_tel")
        private String calTel;
        
        @JsonProperty("homp_url")
        private String homeUrl;
        
        @JsonProperty("dcls_chrg_man")
        private String dclsChrgMan;
        
        @JsonProperty("area_cd")
        private String areaCd;
        
        @JsonProperty("area_nm")
        private String areaNm;
    }
} 