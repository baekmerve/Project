package com.example.actionprice.auctionData.service;

import com.example.actionprice.auctionData.dto.CategoryItemDTO;
import com.example.actionprice.auctionData.dto.CategoryDTO;
import com.example.actionprice.auctionData.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import java.util.*;

@RequiredArgsConstructor
@Service
@Log4j2
public class AuctionCategoryServiceImpl implements AuctionCategoryService {

    private final CategoryEntityRepo categoryEntity_repo;

    // 중분류 갖고오기
    @Override
    public CategoryDTO getMiddleCategory(String large) {
        log.info("[class] AuctionCategoryServiceImpl - [method] - getMiddleCategory - large : {}", large);
        List<CategoryItemDTO> list = categoryEntity_repo.findByLarge(large)
                .stream()
                .map(entity -> new CategoryItemDTO(entity.getMiddle()))
                .distinct()
                .toList();
        
        return CategoryDTO.builder()
                .large(large)
                .list(list)
                .build();
    }

    // 소분류 갖고오기
    @Override
    public CategoryDTO getSmallCategory(String large, String middle) {
        log.info("[class] AuctionCategoryServiceImpl - [method] - getMiddleCategory : large : {} | middle : {}", large, middle);
        List<CategoryItemDTO> list = categoryEntity_repo.findByLargeAndMiddle(large, middle)
                .stream()
                .map(entity -> new CategoryItemDTO(entity.getProductName()))
                .distinct()
                .toList();

        
        return CategoryDTO.builder()
                .large(large)
                .middle(middle)
                .list(list)
                .build();
    }

    // 등급 갖고오기
    @Override
    public CategoryDTO getProductRankCategory(String large, String middle, String small) {
        log.info("[class] AuctionCategoryServiceImpl - [method] - getMiddleCategory : large : {} | middle : {} | small : {}", large, middle, small);
        List<CategoryItemDTO> list = categoryEntity_repo.findByLargeAndMiddleAndProductName(large, middle, small)
                .stream()
                .map(entity -> new CategoryItemDTO(entity.getProductRank()))
                .distinct()
                .toList();
        
        return CategoryDTO.builder()
                .large(large)
                .middle(middle)
                .small(small)
                .list(list)
                .build();
    }
}



