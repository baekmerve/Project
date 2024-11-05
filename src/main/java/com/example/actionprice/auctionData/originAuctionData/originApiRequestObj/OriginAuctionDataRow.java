package com.example.actionprice.auctionData.originAuctionData.originApiRequestObj;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OriginAuctionDataRow {


    @JsonProperty("item_name")
    private String item_name; // 품목명

    @JsonProperty("item_code")
    private String item_code; // 품목코드

    @JsonProperty("kind_name")
    private String kind_name; // 품종명

    @JsonProperty("kind_code")
    private String kind_code; // 품종코드

    @JsonProperty("rank")
    private String rank; // 상태(상품,중품)

    @JsonProperty("rank_code")
    private String rank_code; // 단위

    @JsonProperty("unit")
    private String unit; // 단위

    @JsonProperty("day1")
    private String day1; // 1일자(조회일자 기준)

    @JsonProperty("dpr1")
    private String dpr1; // 조회일자 가격

    @JsonProperty("day2")
    private String day2; // 1일전 일자(조회일자 기준)

    @JsonProperty("dpr2")
    private String dpr2; // 1일전 가격

    @JsonProperty("day3")
    private String day3; // 1주일전 일자(조회일자 기준)

    @JsonProperty("dpr3")
    private String dpr3; // 1주일전 가격

    @JsonProperty("day4")
    private String day4; // 2주일전 일자(조회일자 기준)

    @JsonProperty("dpr4")
    private String dpr4; // 2주일전 가격

    @JsonProperty("day5")
    private String day5; // 1개월전 일자(조회일자 기준)

    @JsonProperty("dpr5")
    private String dpr5; // 1개월전 가격

    @JsonProperty("day6")
    private String day6; // 1년전 일자(조회일자 기준)

    @JsonProperty("dpr6")
    private String dpr6; // 1년전 가격

    @JsonProperty("day7")
    private String day7; // 평년일자

    @JsonProperty("dpr7")
    private String dpr7; // 평년가격

}
