package com.example.actionprice.auctionData.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Entity
@Table(name = "auctionData_specialCrop")
public class AuctionEntity_specialCrop extends AuctionBaseEntity {
    public AuctionEntity_specialCrop() {}
}
