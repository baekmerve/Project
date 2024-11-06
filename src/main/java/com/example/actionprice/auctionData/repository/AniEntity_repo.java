package com.example.actionprice.auctionData.repository;

import com.example.actionprice.auctionData.entity.AuctionEntity_ani;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;


public interface AniEntity_repo extends JpaRepository<AuctionEntity_ani, Long> {

    // large,middle,small,rank 로 조건에 맞는 데이터 조회 후 날짜 및 페이지 받고 보여주기
    Page<AuctionEntity_ani>  findByLargeAndMiddleAndProductNameAndProductRankAndDelDateBetween(String large, String middle, String productName, String productRank, LocalDate startDate, LocalDate endDate , Pageable pageable);

    List<AuctionEntity_ani> findByLargeAndMiddleAndProductNameAndProductRankAndDelDateBetween(String large, String middle, String productName, String productRank, LocalDate startDate, LocalDate endDate);


}
