package com.example.actionprice.auctionData.service;

import com.example.actionprice.auctionData.dto.CategoryResultDTO;
import com.example.actionprice.auctionData.dto.CategoryDTO;
import com.example.actionprice.auctionData.entity.AuctionBaseEntity;

import java.time.LocalDate;
import java.util.List;

public interface AuctionCategoryService {

    // 대분류에 따른 중분류 조회
    CategoryDTO getMiddleCategory(String large);

    // 대분류와 중분류에 따른 소분류 조회
    CategoryDTO getSmallCategory(String large, String middle);

    // 대분류, 중분류, 소분류에 따른 품목 등급 조회
    CategoryDTO getProductRankCategory(String large, String middle, String small);

    // 대분류, 중분류, 소분류, 등급에 날짜에 따른 데이터 조회 및 페이징 처리
    CategoryResultDTO getCategoryAndPage(String large, String middle, String small, String rank, LocalDate startDate, LocalDate endDate ,Integer pageNum);

    // 엑셀 다운로드
    byte[] createExcelFile(List<AuctionBaseEntity> transactionHistoryList);

    CategoryResultDTO getCategory(String large, String middle, String small, String rank, LocalDate startDate, LocalDate endDate);

}
