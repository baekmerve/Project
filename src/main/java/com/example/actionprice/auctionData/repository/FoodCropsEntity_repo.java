package com.example.actionprice.auctionData.repository;

import com.example.actionprice.auctionData.entity.AuctionEntity_foodCrops;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface FoodCropsEntity_repo extends JpaRepository<AuctionEntity_foodCrops, Long> {

    Page<AuctionEntity_foodCrops> findByLargeAndMiddleAndProductNameAndProductRankAndDelDateBetween(String large, String middle, String productName, String productRank, LocalDate startDate, LocalDate endDate , Pageable pageable);

}
