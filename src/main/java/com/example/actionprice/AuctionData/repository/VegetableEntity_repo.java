package com.example.actionprice.AuctionData.repository;

import com.example.actionprice.AuctionData.entity.AuctionEntity_vegetable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface VegetableEntity_repo extends JpaRepository<AuctionEntity_vegetable, Long> {

    Page<AuctionEntity_vegetable> findByLargeAndMiddleAndProductNameAndProductRankAndDelDateBetween(String large, String middle, String productName, String productRank, LocalDate startDate, LocalDate endDate , Pageable pageable);
}
