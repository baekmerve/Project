package com.example.actionprice.AuctionData.repository;

import com.example.actionprice.AuctionData.entity.AuctionEntity_fish;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;

public interface FishEntity_repo extends JpaRepository<AuctionEntity_fish, Long> {

    Page<AuctionEntity_fish> findByLargeAndMiddleAndProductNameAndProductRankAndDelDateBetween(String large, String middle, String productName, String productRank, LocalDate startDate, LocalDate endDate , Pageable pageable);

}
