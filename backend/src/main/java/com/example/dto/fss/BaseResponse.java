package com.example.dto.fss;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class BaseResponse {
    @JsonProperty("err_cd")
    private String errCode;
    
    @JsonProperty("err_msg")
    private String errMsg;
    
    @JsonProperty("now_page_no")
    private int nowPageNo;
    
    @JsonProperty("max_page_no")
    private int maxPageNo;
    
    @JsonProperty("total_count")
    private int totalCount;
} 