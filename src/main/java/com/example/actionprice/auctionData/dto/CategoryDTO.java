package com.example.actionprice.auctionData.dto;

import lombok.Builder;
import lombok.Getter;
import java.util.List;

@Getter
@Builder
public class CategoryDTO {


    private String large;
    private String middle;
    private String small;
    private String rank;
    private List<CategoryItemDTO> list;
}
