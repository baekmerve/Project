package com.example.actionprice.AuctionData.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name="auction_category")
@NoArgsConstructor
@AllArgsConstructor
public class AuctionCategoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="del_id")
    private Long delId;

    private String large;
    private String middle;
    private String productName;
    private String productRank;
}
