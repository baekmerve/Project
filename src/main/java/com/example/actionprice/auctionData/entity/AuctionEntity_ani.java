package com.example.actionprice.auctionData.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Entity
@Table(name = "auctionData_ani")
public class AuctionEntity_ani extends AuctionBaseEntity {
    public AuctionEntity_ani() {}

}
