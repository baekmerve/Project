package com.example.actionprice.AuctionData.dto;

import lombok.Getter;

@Getter
public class CategoryItemDTO {

    private int id; // 고유 ID
    private String name; // 이름

    public CategoryItemDTO(String name) {
        this.name = name;
        this.id = name.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return this.name.equals(((CategoryItemDTO) obj).name);
    }

    @Override
    public int hashCode() {
        return this.name.hashCode();
    }
}
