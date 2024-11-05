package com.example.actionprice.auctionData.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Entity
@Table(name = "auctionData_foodCrops")
public class AuctionEntity_foodCrops extends AuctionBaseEntity{
    public AuctionEntity_foodCrops() {}

}
