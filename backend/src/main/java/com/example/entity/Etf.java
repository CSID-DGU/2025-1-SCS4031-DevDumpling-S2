package com.example.entity;

import com.example.dto.krx.EtfResponse;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "etfs", indexes = {
    @Index(name = "idx_bas_dd_isu_cd", columnList = "basDd,isuCd", unique = true)
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Etf {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String basDd;              // 기준일자
    
    @Column(nullable = false)
    private String isuCd;              // 종목코드
    
    private String isuNm;              // 종목명
    private String mktNm;              // 시장구분
    private String sectTpNm;           // 소속부
    private Long tddClsprc;            // 종가
    private Long cmpprevddPrc;         // 대비
    private Float flucRt;              // 등락률
    private Float nav;                 // 순자산가치(NAV)
    private Long tddOpnprc;            // 시가
    private Long tddHgprc;             // 고가
    private Long tddLwprc;             // 저가
    private Long accTrdvol;            // 거래량
    private Long accTrdval;            // 거래대금
    private Long mktcap;               // 시가총액
    private Long invstasstNetasstTotamt; // 순자산총액
    private Long listShrs;             // 상장주식수
    private String idxIndNm;           // 기초지수
    private Float objStkprcIdx;        // 기초지수_종가
    private Float cmpprevddIdx;        // 기초지수_대비
    private Float flucRtIdx;           // 기초지수_등락률

    public static Etf fromResponse(EtfResponse response) {
        return Etf.builder()
                .basDd(response.getBasDd())
                .isuCd(response.getIsuCd())
                .isuNm(response.getIsuNm())
                .mktNm(response.getMktNm())
                .sectTpNm(response.getSectTpNm())
                .tddClsprc(parseLong(response.getTddClsprc()))
                .cmpprevddPrc(parseLong(response.getCmpprevddPrc()))
                .flucRt(parseFloat(response.getFlucRt()))
                .nav(parseFloat(response.getNav()))
                .tddOpnprc(parseLong(response.getTddOpnprc()))
                .tddHgprc(parseLong(response.getTddHgprc()))
                .tddLwprc(parseLong(response.getTddLwprc()))
                .accTrdvol(parseLong(response.getAccTrdvol()))
                .accTrdval(parseLong(response.getAccTrdval()))
                .mktcap(parseLong(response.getMktcap()))
                .invstasstNetasstTotamt(parseLong(response.getInvstasstNetasstTotamt()))
                .listShrs(parseLong(response.getListShrs()))
                .idxIndNm(response.getIdxIndNm())
                .objStkprcIdx(parseFloat(response.getObjStkprcIdx()))
                .cmpprevddIdx(parseFloat(response.getCmpprevddIdx()))
                .flucRtIdx(parseFloat(response.getFlucRtIdx()))
                .build();
    }

    public void updateFromResponse(EtfResponse response) {
        this.isuNm = response.getIsuNm();
        this.mktNm = response.getMktNm();
        this.sectTpNm = response.getSectTpNm();
        this.tddClsprc = parseLong(response.getTddClsprc());
        this.cmpprevddPrc = parseLong(response.getCmpprevddPrc());
        this.flucRt = parseFloat(response.getFlucRt());
        this.nav = parseFloat(response.getNav());
        this.tddOpnprc = parseLong(response.getTddOpnprc());
        this.tddHgprc = parseLong(response.getTddHgprc());
        this.tddLwprc = parseLong(response.getTddLwprc());
        this.accTrdvol = parseLong(response.getAccTrdvol());
        this.accTrdval = parseLong(response.getAccTrdval());
        this.mktcap = parseLong(response.getMktcap());
        this.invstasstNetasstTotamt = parseLong(response.getInvstasstNetasstTotamt());
        this.listShrs = parseLong(response.getListShrs());
        this.idxIndNm = response.getIdxIndNm();
        this.objStkprcIdx = parseFloat(response.getObjStkprcIdx());
        this.cmpprevddIdx = parseFloat(response.getCmpprevddIdx());
        this.flucRtIdx = parseFloat(response.getFlucRtIdx());
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