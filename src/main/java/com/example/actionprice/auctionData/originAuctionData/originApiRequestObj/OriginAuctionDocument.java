package com.example.actionprice.auctionData.originAuctionData.originApiRequestObj;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OriginAuctionDocument {

    @JsonProperty("condition")
    private List<OriginAuctionCondition> condition;

    @JsonProperty("data")
    private OriginAuctionData data;
}
