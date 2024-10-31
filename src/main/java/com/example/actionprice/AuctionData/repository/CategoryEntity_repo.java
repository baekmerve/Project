package com.example.actionprice.AuctionData.repository;

import com.example.actionprice.AuctionData.entity.AuctionCategoryEntity;
import com.example.actionprice.AuctionData.entity.AuctionEntity_ani;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface CategoryEntity_repo extends JpaRepository<AuctionCategoryEntity,Long> {

    // large 값으로 middle 조회
    List<AuctionCategoryEntity> findByLarge(String large);

    // large,middle 값으로 productName 조회
    List<AuctionCategoryEntity> findByLargeAndMiddle(String large,String middle);

    // large,middle,productName 값으로 productRank 조회
    List<AuctionCategoryEntity> findByLargeAndMiddleAndProductName(String large,String middle,String productName);

}
