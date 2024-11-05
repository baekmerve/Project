package com.example.actionprice.auctionData.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Entity
@Table(name = "auctionData_vegetable")
public class AuctionEntity_vegetable extends AuctionBaseEntity{
    public AuctionEntity_vegetable() {}
}
