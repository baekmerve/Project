package com.example.actionprice.AuctionData.service;

import com.example.actionprice.AuctionData.dto.CategoryResultDTO;
import com.example.actionprice.AuctionData.dto.CategoryDTO;
import com.example.actionprice.AuctionData.entity.AuctionBaseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface AuctionCategoryService {

    // 대분류에 따른 중분류 조회
    CategoryDTO getMiddleCategory(String large);

    // 대분류와 중분류에 따른 소분류 조회
    CategoryDTO getSmallCategory(String large, String middle);

    // 대분류, 중분류, 소분류에 따른 품목 등급 조회
    CategoryDTO getProductRankCategory(String large, String middle, String small);

    // 대분류, 중분류, 소분류, 등급에 날짜에 따른 데이터 조회 및 페이징 처리
    CategoryResultDTO getCategoryAndPage(String large, String middle, String small, String rank, LocalDate startDate, LocalDate endDate ,Integer pageNum);
}
