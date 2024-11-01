package com.example.actionprice.AuctionData.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@MappedSuperclass
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
public class AuctionBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long del_id;

    @Column(name="del_date")
    private LocalDate delDate; //거래일자

    private String large; //대분류

    private String middle; //중분류

    @Column(name = "product_name")
    private String productName; // 상품명

    @Column(name = "product_rank", nullable = true)
    private String productRank; // 등급(상품, 중품)

    private int price; // 가격

    private String market_name; //거래장 이름

    @Column(nullable = true)
    private String del_unit; // 단위(중량 : kg 등)


}
