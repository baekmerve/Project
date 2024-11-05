package com.example.actionprice.auctionData.originAuctionData.originApiRequestObj;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OriginAuctionData {

    @JsonProperty("error_code")
    private String error_code;

    @JsonProperty("item")
    private List<OriginAuctionDataRow> item;

}
