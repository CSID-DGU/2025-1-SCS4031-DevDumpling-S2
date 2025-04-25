package com.example.entity;

import com.example.dto.krx.StockResponse;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "stocks", indexes = {
    @Index(name = "idx_bas_dd_srtn_cd", columnList = "basDd,srtnCd", unique = true)
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Stock {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String basDd;        // 기준일자
    
    @Column(nullable = false)
    private String srtnCd;       // 종목코드
    
    private String isinCd;       // ISIN코드
    private String itmsNm;       // 종목명
    private String mrktCtg;      // 시장구분
    private Long clpr;           // 종가
    private Long vs;             // 전일 대비
    private Float fltRt;         // 등락률
    private Long mkp;            // 시가
    private Long hipr;           // 고가
    private Long lopr;           // 저가
    private Long trqu;           // 거래량
    private Long trPrc;          // 거래대금
    private Long lstgStCnt;      // 상장주식수
    private Long mrktTotAmt;     // 시가총액

    public static Stock fromResponse(StockResponse response) {
        return Stock.builder()
                .srtnCd(response.getIsuCd())          // ISU_CD를 종목코드로 사용
                .isinCd(response.getIsuCd())          // ISU_CD는 ISIN 코드도 겸함
                .itmsNm(response.getIsuNm())          // ISU_NM
                .mrktCtg(response.getMktNm())         // MKT_NM
                .clpr(parseLong(response.getTddClsprc()))        // TDD_CLSPRC
                .vs(parseLong(response.getCmpprevddPrc()))       // CMPPREVDD_PRC
                .fltRt(parseFloat(response.getFlucRt()))         // FLUC_RT
                .mkp(parseLong(response.getTddOpnprc()))         // TDD_OPNPRC
                .hipr(parseLong(response.getTddHgprc()))         // TDD_HGPRC
                .lopr(parseLong(response.getTddLwprc()))         // TDD_LWPRC
                .trqu(parseLong(response.getAccTrdvol()))        // ACC_TRDVOL
                .trPrc(parseLong(response.getAccTrdval()))       // ACC_TRDVAL
                .lstgStCnt(parseLong(response.getListShrs()))    // LIST_SHRS
                .mrktTotAmt(parseLong(response.getMktcap()))     // MKTCAP
                .build();
    }

    public void updateFromStock(Stock stock) {
        this.isinCd = stock.getIsinCd();
        this.itmsNm = stock.getItmsNm();
        this.mrktCtg = stock.getMrktCtg();
        this.clpr = stock.getClpr();
        this.vs = stock.getVs();
        this.fltRt = stock.getFltRt();
        this.mkp = stock.getMkp();
        this.hipr = stock.getHipr();
        this.lopr = stock.getLopr();
        this.trqu = stock.getTrqu();
        this.trPrc = stock.getTrPrc();
        this.lstgStCnt = stock.getLstgStCnt();
        this.mrktTotAmt = stock.getMrktTotAmt();
    }

    private static Long parseLong(String value) {
        try {
            if (value == null || value.trim().isEmpty() || value.equals("-")) {
                return null;
            }
            return Long.parseLong(value.replaceAll(",", "").replaceAll("-", ""));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static Float parseFloat(String value) {
        try {
            if (value == null || value.trim().isEmpty() || value.equals("-")) {
                return null;
            }
            return Float.parseFloat(value.replaceAll(",", "").replaceAll("-", ""));
        } catch (NumberFormatException e) {
            return null;
        }
    }
} 