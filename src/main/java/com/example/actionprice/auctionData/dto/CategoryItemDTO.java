package com.example.actionprice.auctionData.dto;

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
        return this.id == ((CategoryItemDTO) obj).id;
    }

    @Override
    public int hashCode() {
        return this.id;
    }
}
