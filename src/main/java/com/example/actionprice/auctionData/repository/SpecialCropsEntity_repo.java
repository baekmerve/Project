package com.example.actionprice.auctionData.repository;

import com.example.actionprice.auctionData.entity.AuctionEntity_ani;
import com.example.actionprice.auctionData.entity.AuctionEntity_specialCrop;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface SpecialCropsEntity_repo extends JpaRepository<AuctionEntity_specialCrop, Long> {

    Page<AuctionEntity_specialCrop> findByLargeAndMiddleAndProductNameAndProductRankAndDelDateBetween(String large, String middle, String productName, String productRank, LocalDate startDate, LocalDate endDate , Pageable pageable);

    List<AuctionEntity_specialCrop> findByLargeAndMiddleAndProductNameAndProductRankAndDelDateBetween(String large, String middle, String productName, String productRank, LocalDate startDate, LocalDate endDate);


}
