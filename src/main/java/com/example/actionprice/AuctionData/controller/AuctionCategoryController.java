package com.example.actionprice.AuctionData.controller;

import com.example.actionprice.AuctionData.dto.CategoryResultDTO;
import com.example.actionprice.AuctionData.dto.CategoryDTO;
import com.example.actionprice.AuctionData.service.AuctionCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/category") //수정할거
public class AuctionCategoryController {

    private final AuctionCategoryService auctionCategoryService;

    @GetMapping("/{large}") //이런것들도
    public CategoryDTO getCategoriesByLarge(@PathVariable String large) {
        return auctionCategoryService.getMiddleCategory(large);
    }

    @GetMapping("/{large}/{middle}")
    public CategoryDTO getCategoriesByLargeAndMiddle(
            @PathVariable String large,
            @PathVariable String middle) {
        return auctionCategoryService.getSmallCategory(large, middle);
    }

    @GetMapping("/{large}/{middle}/{small}")
    public CategoryDTO getCategoriesMyLargeMiddleSmall(
            @PathVariable String large,
            @PathVariable String middle,
            @PathVariable String small) {
        return auctionCategoryService.getProductRankCategory(large, middle, small);
    }

    @GetMapping("/{large}/{middle}/{small}/{rank}")
    public CategoryResultDTO getPriceMySmallMiddleSmallRank(
            @PathVariable String large,
            @PathVariable String middle,
            @PathVariable String small,
            @PathVariable String rank,
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate,
            @RequestParam(name = "page", defaultValue = "0", required = false) Integer page
    ) {
        return auctionCategoryService.getCategoryAndPage(large, middle, small, rank,startDate, endDate, page);
    }
}
