package com.example.actionprice.AuctionData.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class CategoryDTO {

    private String large;
    private String middle;
    private String small;
    private List<String> list;


}
