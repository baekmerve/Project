package com.example.actionprice.auctionData.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Entity
@Table(name = "auctionData_fish")
public class AuctionEntity_fish extends AuctionBaseEntity {
    public AuctionEntity_fish() {}
}
