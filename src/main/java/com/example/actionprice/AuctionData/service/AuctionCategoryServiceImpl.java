package com.example.actionprice.AuctionData.service;

import com.example.actionprice.AuctionData.dto.CategoryResultDTO;
import com.example.actionprice.AuctionData.dto.CategoryDTO;
import com.example.actionprice.AuctionData.entity.*;
import com.example.actionprice.AuctionData.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class AuctionCategoryServiceImpl implements AuctionCategoryService {

    private final AniEntity_repo aniEntity_repo;
    private final FishEntity_repo fishEntity_repo;
    private final FoodCropsEntity_repo foodCropsEntity_repo;
    private final FruitEntity_repo fruitEntity_repo;
    private final SpecialCropsEntity_repo specialCropsEntity_repo;
    private final VegetableEntity_repo vegetableEntity_repo;
    private final CategoryEntity_repo categoryEntity_repo;


    @Override
    public CategoryDTO getMiddleCategory(String large) {
        List<String> list;
        switch (large){
            case "축산물":
                list = categoryEntity_repo.findByLarge(large).stream()
                        .map(AuctionCategoryEntity::getMiddle)
                        .distinct()
                        .collect(Collectors.toList());
                break;

            case "수산물":
                list = categoryEntity_repo.findByLarge(large).stream()
                        .map(AuctionCategoryEntity::getMiddle)
                        .distinct()
                        .collect(Collectors.toList());
                break;

            case "식량작물":
                list = categoryEntity_repo.findByLarge(large).stream()
                        .map(AuctionCategoryEntity::getMiddle)
                        .distinct()
                        .collect(Collectors.toList());
                break;

            case "과일류":
                list = categoryEntity_repo.findByLarge(large).stream()
                        .map(AuctionCategoryEntity::getMiddle)
                        .distinct()
                        .collect(Collectors.toList());
                break;

            case "특용작물":
                list = categoryEntity_repo.findByLarge(large).stream()
                        .map(AuctionCategoryEntity::getMiddle)
                        .distinct()
                        .collect(Collectors.toList());
                break;

            case "채소류":
                list = categoryEntity_repo.findByLarge(large).stream()
                        .map(AuctionCategoryEntity::getMiddle)
                        .distinct()
                        .collect(Collectors.toList());
                break;
            default:
                list = List.of();
        }
        return CategoryDTO.builder()
                .large(large)
                .list(list)
                .build();
    }

    @Override
    public CategoryDTO getSmallCategory(String large, String middle) {
        List<String> list;
        switch (large){
            case "축산물":
                list = categoryEntity_repo.findByLargeAndMiddle(large,middle).stream()
                        .map(AuctionCategoryEntity::getProductName)
                        .distinct()
                        .collect(Collectors.toList());
                break;

            case "수산물":
                list = categoryEntity_repo.findByLargeAndMiddle(large,middle).stream()
                        .map(AuctionCategoryEntity::getProductName)
                        .distinct()
                        .collect(Collectors.toList());
                break;

            case "식량작물":
                list = categoryEntity_repo.findByLargeAndMiddle(large,middle).stream()
                        .map(AuctionCategoryEntity::getProductName)
                        .distinct()
                        .collect(Collectors.toList());
                break;

            case "과일류":
                list = categoryEntity_repo.findByLargeAndMiddle(large,middle).stream()
                        .map(AuctionCategoryEntity::getProductName)
                        .distinct()
                        .collect(Collectors.toList());
                break;

            case "특용작물":
                list = categoryEntity_repo.findByLargeAndMiddle(large,middle).stream()
                        .map(AuctionCategoryEntity::getProductName)
                        .distinct()
                        .collect(Collectors.toList());
                break;

            case "채소류":
                list = categoryEntity_repo.findByLargeAndMiddle(large,middle).stream()
                        .map(AuctionCategoryEntity::getProductName)
                        .distinct()
                        .collect(Collectors.toList());
                break;
            default:
                list = List.of();
        }
        return CategoryDTO.builder()
                .large(large)
                .middle(middle)
                .list(list)
                .build();
    }

    @Override
    public CategoryDTO getProductRankCategory(String large, String middle, String small) {
        List<String> list;
        switch (large){
            case "축산물":
                list = categoryEntity_repo.findByLargeAndMiddleAndProductName(large,middle,small).stream()
                        .map(AuctionCategoryEntity::getProductRank)
                        .distinct()
                        .collect(Collectors.toList());
                break;

                case "수산물":
                    list = categoryEntity_repo.findByLargeAndMiddleAndProductName(large,middle,small).stream()
                            .map(AuctionCategoryEntity::getProductRank)
                            .distinct()
                            .collect(Collectors.toList());
                    break;
                case "식량작물":
                    list = categoryEntity_repo.findByLargeAndMiddleAndProductName(large,middle,small).stream()
                            .map(AuctionCategoryEntity::getProductRank)
                            .distinct()
                            .collect(Collectors.toList());
                    break;

                case "과일류":
                    list = categoryEntity_repo.findByLargeAndMiddleAndProductName(large,middle,small).stream()
                            .map(AuctionCategoryEntity::getProductRank)
                            .distinct()
                            .collect(Collectors.toList());
                    break;

                case "특용작물":
                    list = categoryEntity_repo.findByLargeAndMiddleAndProductName(large,middle,small).stream()
                            .map(AuctionCategoryEntity::getProductRank)
                                .distinct()
                                .collect(Collectors.toList());
                        break;

                case "채소류":
                    list = categoryEntity_repo.findByLargeAndMiddleAndProductName(large,middle,small).stream()
                            .map(AuctionCategoryEntity::getProductRank)
                                .distinct()
                                .collect(Collectors.toList());
                        break;
                default:
                    list = List.of();
        }
        return CategoryDTO.builder()
                .large(large)
                .middle(middle)
                .small(small)
                .list(list)
                .build();
    }

    @Override
    public CategoryResultDTO getCategoryAndPage(String large, String middle, String small, String rank, LocalDate startDate, LocalDate endDate,Integer pageNum) {

        LocalDate today = LocalDate.now();
        if (startDate == null) {
            startDate = today; // startDate가 null일 경우 현재 날짜를 사용
        }
        if (endDate == null) {
            endDate = today; // endDate가 null일 경우 현재 날짜를 사용
        }

        Pageable pageable = PageRequest.of(pageNum, 10, Sort.by(Sort.Order.desc("del_id"))); // 페이징 및 정렬 조건 설정
        Page<?> pageResult;

        switch (large) {
            case "축산물":
                pageResult = aniEntity_repo.findByLargeAndMiddleAndProductNameAndProductRankAndDelDateBetween(
                        large, middle, small, rank, startDate, endDate, pageable);
                break;

            case "수산물":
                pageResult = fishEntity_repo.findByLargeAndMiddleAndProductNameAndProductRankAndDelDateBetween(
                        large, middle, small, rank, startDate, endDate, pageable);
                break;

            case "식량작물":
                pageResult = foodCropsEntity_repo.findByLargeAndMiddleAndProductNameAndProductRankAndDelDateBetween(
                        large, middle, small, rank, startDate, endDate, pageable);
                break;

            case "과일류":
                pageResult = fruitEntity_repo.findByLargeAndMiddleAndProductNameAndProductRankAndDelDateBetween(
                        large, middle, small, rank, startDate, endDate, pageable);
                break;

            case "특용작물":
                pageResult = specialCropsEntity_repo.findByLargeAndMiddleAndProductNameAndProductRankAndDelDateBetween(
                        large, middle, small, rank, startDate, endDate, pageable);
                break;

            case "채소류":
                pageResult = vegetableEntity_repo.findByLargeAndMiddleAndProductNameAndProductRankAndDelDateBetween(
                        large, middle, small, rank, startDate, endDate, pageable);
                break;

            default:
                throw new IllegalArgumentException("Invalid category: " + large);
        }

        return convertPageToDTO(pageResult);
    }

    private <T> CategoryResultDTO convertPageToDTO(Page<T> page) {
        boolean hasContent = (page != null && page.hasContent());

        List<AuctionBaseEntity> transactionHistoryList = hasContent ? convertListObject(page.getContent(), hasContent) : Collections.emptyList();

        int currentPageNum = hasContent ? (page.getNumber() + 1) : 1;
        int currentPageSize = hasContent ? page.getNumberOfElements() : 0;
        int totalPageNum = hasContent ? page.getTotalPages() : 1;
        boolean hasNext = hasContent && page.hasNext();

        return CategoryResultDTO.builder()
                .transactionHistoryList(transactionHistoryList)
                .currentPageNum(currentPageNum)
                .currentPageSize(currentPageSize)
                .totalPageNum(totalPageNum)
                .listSize(transactionHistoryList.size())
                .hasNext(hasNext)
                .build();
    }

    private <T> List<AuctionBaseEntity> convertListObject(List<T> list, boolean hasContent) {
        return hasContent ? (List<AuctionBaseEntity>) list : Collections.emptyList();
    }




//    @Override
//    public CategoryResultDTO getAveragePrice(String large, String middle, String small, String rank, LocalDate startDate, LocalDate endDate) {
//        List<Integer> prices;
//        switch (large) {
//            case "축산물":
//                prices = aniEntity_repo.findByLargeAndMiddleAndProductNameAndProductRankAndDelDateBetween(large, middle, small, rank, startDate, endDate).stream()
//                        .map(AuctionEntity_ani::getPrice)
//                        .collect(Collectors.toList());
//                break;
//            case "수산물":
//                prices = fishEntity_repo.findByLargeAndMiddleAndProductNameAndProductRank(large, middle, small, rank).stream()
//                        .map(AuctionEntity_fish::getPrice)
//                        .collect(Collectors.toList());
//                break;
//            case "식량작물":
//                prices = foodCropsEntity_repo.findByLargeAndMiddleAndProductNameAndProductRank(large, middle, small, rank).stream()
//                        .map(AuctionEntity_foodCrops::getPrice)
//                        .collect(Collectors.toList());
//                break;
//            case "과일류":
//                prices = fruitEntity_repo.findByLargeAndMiddleAndProductNameAndProductRank(large, middle, small, rank).stream()
//                        .map(AuctionEntity_fruit::getPrice)
//                        .collect(Collectors.toList());
//                break;
//            case "특용작물":
//                prices = specialCropsEntity_repo.findByLargeAndMiddleAndProductNameAndProductRank(large, middle, small, rank).stream()
//                        .map(AuctionEntity_specialCrop::getPrice)
//                        .collect(Collectors.toList());
//                break;
//            case "채소류":
//                prices = vegetableEntity_repo.findByLargeAndMiddleAndProductNameAndProductRank(large, middle, small, rank).stream()
//                        .map(AuctionEntity_vegetable::getPrice)
//                        .collect(Collectors.toList());
//                break;
//            default:
//                prices = List.of();
//        }
//        OptionalDouble average = prices.stream().mapToInt(Integer::intValue).average();
//        int averagePrice = (int) Math.round(average.orElse(0.0));
//        return CategoryResultDTO.builder()
//                .large(large)
//                .middle(middle)
//                .small(small)
//                .rank(rank)
//                .startDate(startDate)
//                .endDate(endDate)
//                .averagePrice(averagePrice)
//                .build();
//    }


}



