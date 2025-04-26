package com.example.dto.krx;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EtfResponse {
    @JsonProperty("BAS_DD")
    private String basDd;              // 기준일자

    @JsonProperty("ISU_CD")
    private String isuCd;              // 종목코드

    @JsonProperty("ISU_NM")
    private String isuNm;              // 종목명

    @JsonProperty("MKT_NM")
    private String mktNm;              // 시장구분

    @JsonProperty("SECT_TP_NM")
    private String sectTpNm;           // 소속부

    @JsonProperty("TDD_CLSPRC")
    private String tddClsprc;          // 종가

    @JsonProperty("CMPPREVDD_PRC")
    private String cmpprevddPrc;       // 대비

    @JsonProperty("FLUC_RT")
    private String flucRt;             // 등락률

    @JsonProperty("NAV")
    private String nav;                // 순자산가치(NAV)

    @JsonProperty("TDD_OPNPRC")
    private String tddOpnprc;          // 시가

    @JsonProperty("TDD_HGPRC")
    private String tddHgprc;           // 고가

    @JsonProperty("TDD_LWPRC")
    private String tddLwprc;           // 저가

    @JsonProperty("ACC_TRDVOL")
    private String accTrdvol;          // 거래량

    @JsonProperty("ACC_TRDVAL")
    private String accTrdval;          // 거래대금

    @JsonProperty("MKTCAP")
    private String mktcap;             // 시가총액

    @JsonProperty("INVSTASST_NETASST_TOTAMT")
    private String invstasstNetasstTotamt;  // 순자산총액

    @JsonProperty("LIST_SHRS")
    private String listShrs;           // 상장주식수

    @JsonProperty("IDX_IND_NM")
    private String idxIndNm;           // 기초지수

    @JsonProperty("OBJ_STKPRC_IDX")
    private String objStkprcIdx;       // 기초지수_종가

    @JsonProperty("CMPPREVDD_IDX")
    private String cmpprevddIdx;       // 기초지수_대비

    @JsonProperty("FLUC_RT_IDX")
    private String flucRtIdx;          // 기초지수_등락률
} 