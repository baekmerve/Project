package com.example.actionprice.auctionData.controller;

import com.example.actionprice.auctionData.dto.CategoryResultDTO;
import com.example.actionprice.auctionData.dto.CategoryDTO;
import com.example.actionprice.auctionData.entity.AuctionBaseEntity;
import com.example.actionprice.auctionData.service.AuctionCategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/category") //수정할거
@Log4j2
public class AuctionCategoryController {

    private final AuctionCategoryService auctionCategoryService;

    @GetMapping("/{large}") //이런것들도
    public CategoryDTO getCategoriesByLarge(@PathVariable String large) {
        log.info("[class] AuctionCategoryController - [method] getCategoriesByLarge - large : {}", large);
        return auctionCategoryService.getMiddleCategory(large);
    }

    @GetMapping("/{large}/{middle}")
    public CategoryDTO getCategoriesByLargeAndMiddle(
            @PathVariable String large,
            @PathVariable String middle) {
        log.info("[class] AuctionCategoryController - [method] getCategoriesByLargeAndMiddle - large : {} | middle : {}", large, middle);
        return auctionCategoryService.getSmallCategory(large, middle);
    }

    @GetMapping("/{large}/{middle}/{small}")
    public CategoryDTO getCategoriesMyLargeMiddleSmall(
            @PathVariable String large,
            @PathVariable String middle,
            @PathVariable String small) {
        log.info("[class] AuctionCategoryController - [method] getCategoriesMyLargeMiddleSmall - large : {} | middle : {} | small : {}", large, middle, small);
        return auctionCategoryService.getProductRankCategory(large, middle, small);
    }

    @GetMapping("/{large}/{middle}/{small}/{rank}")
    public CategoryResultDTO getPriceMySmallMiddleSmallRank(
            @PathVariable String large,
            @PathVariable String middle,
            @PathVariable String small,
            @PathVariable String rank,
            @RequestParam(value = "startDate",required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(value = "endDate",required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @RequestParam(name = "pageNum", defaultValue = "0", required = false) Integer pageNum
    ) {

        LocalDate today = LocalDate.now();
        LocalDate oneYearAgo = today.minusYears(1);
        // 날짜 유효성 검사
        if (endDate == null || endDate.isBefore(oneYearAgo)) {
            endDate = today; // 기본값으로 오늘 날짜로 설정
        }
        if (startDate == null  || startDate.isBefore(oneYearAgo) || startDate.isAfter(endDate)) {
            // 기본값으로 오늘 날짜로 설정
            startDate = today;
        }
        return auctionCategoryService.getCategoryAndPage(large, middle, small, rank,startDate, endDate, pageNum);
    }


    @GetMapping("/{large}/{middle}/{small}/{rank}/excel")
    public ResponseEntity<byte[]> downloadExcel(
            @PathVariable String large,
            @PathVariable String middle,
            @PathVariable String small,
            @PathVariable String rank,
            @RequestParam(value = "startDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(value = "endDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {

        LocalDate today = LocalDate.now();
        LocalDate oneYearAgo = today.minusYears(1);
        // 날짜 유효성 검사
        if (endDate == null || endDate.isBefore(oneYearAgo)) {
            endDate = today; // 기본값으로 오늘 날짜로 설정
        }
        if (startDate == null  || startDate.isBefore(oneYearAgo) || startDate.isAfter(endDate)) {
            // 기본값으로 오늘 날짜로 설정
            startDate = today;
        }
        // 페이지 없이 데이터를 가져오는 서비스 메서드 호출
        CategoryResultDTO resultDTO = auctionCategoryService.getCategory(large, middle, small, rank, startDate, endDate);
        List<AuctionBaseEntity> categoryList = resultDTO.getCategoryList();

        // 엑셀 파일 생성
        byte[] excelFile = auctionCategoryService.createExcelFile(categoryList);

        // Content-Disposition 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=transaction_history.xlsx");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(excelFile);
    }
}
