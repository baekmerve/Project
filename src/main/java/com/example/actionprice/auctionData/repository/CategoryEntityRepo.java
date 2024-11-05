package com.example.actionprice.auctionData.repository;

import com.example.actionprice.auctionData.entity.AuctionCategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CategoryEntityRepo extends JpaRepository<AuctionCategoryEntity,Long> {

    // large 값으로 middle 조회
    List<AuctionCategoryEntity> findByLarge(String large);

    // large,middle 값으로 productName 조회
    List<AuctionCategoryEntity> findByLargeAndMiddle(String large,String middle);

    // large,middle,productName 값으로 productRank 조회
    List<AuctionCategoryEntity> findByLargeAndMiddleAndProductName(String large,String middle,String productName);

}
