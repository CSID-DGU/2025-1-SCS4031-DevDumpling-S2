package com.example.dto.dgk;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import lombok.Data;

@Data
@JacksonXmlRootElement(localName = "response")
public class InsuranceResponse {
    @JacksonXmlProperty(localName = "header")
    private Header header;

    @JacksonXmlProperty(localName = "body")
    private Body body;

    @Data
    public static class Header {
        @JacksonXmlProperty(localName = "resultCode")
        private String resultCode;

        @JacksonXmlProperty(localName = "resultMsg")
        private String resultMsg;
    }

    @Data
    public static class Body {
        @JacksonXmlProperty(localName = "numOfRows")
        private int numOfRows;

        @JacksonXmlProperty(localName = "pageNo")
        private int pageNo;

        @JacksonXmlProperty(localName = "totalCount")
        private int totalCount;

        @JacksonXmlProperty(localName = "items")
        private Items items;
    }

    @Data
    public static class Items {
        @JacksonXmlElementWrapper(useWrapping = false)
        @JacksonXmlProperty(localName = "item")
        private Item[] item;
    }

    @Data
    public static class Item {
        @JacksonXmlProperty(localName = "cmpyCd")
        private String cmpyCd;

        @JacksonXmlProperty(localName = "cmpyNm")
        private String cmpyNm;

        @JacksonXmlProperty(localName = "ptrn")
        private String ptrn;

        @JacksonXmlProperty(localName = "mog")
        private String mog;

        @JacksonXmlProperty(localName = "prdNm")
        private String prdNm;

        @JacksonXmlProperty(localName = "age")
        private String age;

        @JacksonXmlProperty(localName = "mlInsRt")
        private String mlInsRt;

        @JacksonXmlProperty(localName = "fmlInsRt")
        private String fmlInsRt;

        @JacksonXmlProperty(localName = "basDt")
        private String basDt;

        @JacksonXmlProperty(localName = "ofrInstNm")
        private String ofrInstNm;
    }
}
