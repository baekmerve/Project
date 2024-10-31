package com.example.actionprice.AuctionData.repository;

import com.example.actionprice.AuctionData.entity.AuctionBaseEntity;
import com.example.actionprice.AuctionData.entity.AuctionEntity_ani;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface AniEntity_repo extends JpaRepository<AuctionEntity_ani, Long> {


    // large,middle,productName,productRank 값으로 price 조회 후 평균 도출
    List<AuctionEntity_ani> findByLargeAndMiddleAndProductNameAndProductRankAndDelDateBetween(String large, String middle, String productName, String productRank, LocalDate startDate, LocalDate endDate);
    Page<AuctionEntity_ani>  findByLargeAndMiddleAndProductNameAndProductRankAndDelDateBetween(String large, String middle, String productName, String productRank, LocalDate startDate, LocalDate endDate , Pageable pageable);


}
