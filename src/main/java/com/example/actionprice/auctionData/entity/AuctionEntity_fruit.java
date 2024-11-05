package com.example.actionprice.auctionData.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Entity
@Table(name = "auctionData_fruit")
public class AuctionEntity_fruit extends AuctionBaseEntity{
    public AuctionEntity_fruit() {}
}
