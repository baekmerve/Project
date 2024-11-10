package com.example.actionprice.auctionData.service;

import com.example.actionprice.auctionData.dto.CategoryDTO;

public interface AuctionCategoryService {

    // 대분류에 따른 중분류 조회
    CategoryDTO getMiddleCategory(String large);

    // 대분류와 중분류에 따른 소분류 조회
    CategoryDTO getSmallCategory(String large, String middle);

    // 대분류, 중분류, 소분류에 따른 품목 등급 조회
    CategoryDTO getProductRankCategory(String large, String middle, String small);
}
