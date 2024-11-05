package com.example.actionprice.auctionData.service;

import com.example.actionprice.auctionData.dto.CategoryItemDTO;
import com.example.actionprice.auctionData.dto.CategoryResultDTO;
import com.example.actionprice.auctionData.dto.CategoryDTO;
import com.example.actionprice.auctionData.entity.*;
import com.example.actionprice.auctionData.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;


@RequiredArgsConstructor
@Service
@Log4j2
public class AuctionCategoryServiceImpl implements AuctionCategoryService {

    private final AniEntity_repo aniEntity_repo;
    private final FishEntity_repo fishEntity_repo;
    private final FoodCropsEntity_repo foodCropsEntity_repo;
    private final FruitEntity_repo fruitEntity_repo;
    private final SpecialCropsEntity_repo specialCropsEntity_repo;
    private final VegetableEntity_repo vegetableEntity_repo;
    private final CategoryEntityRepo categoryEntity_repo;


    // 중분류 갖고오기
    @Override
    public CategoryDTO getMiddleCategory(String large) {
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

    /**
     * @author homin
     * @created 2024. 11. 1. 오후 4:03
     * @updated 2024. 11. 1. 오후 4:03
     * @info 데이터리스트 및 페이지 작업 타입을 일반 리스트로 다 받는거라 에러날 시  AuctionBaseEntity 자식 객체만 받을 수 있게 설정 가능
     */

    @Override
    public CategoryResultDTO getCategoryAndPage(String large, String middle, String small, String rank, LocalDate startDate, LocalDate endDate, Integer pageNum) {

        // 페이징 및 정렬 조건 설정
        Pageable pageable = PageRequest.of(pageNum, 10, Sort.by(Sort.Order.desc("delId")));
        Page<?> pageResult;

        // 대분류에 따라 적절한 리포지토리 메서드 호출
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

        // 거래 내역 리스트 생성 (내용이 있으면 변환, 없으면 빈 리스트)
        List<AuctionBaseEntity> transactionHistoryList = hasContent
                ? convertListObject(page.getContent())
                : Collections.emptyList();

        int currentPageNum = hasContent ? (page.getNumber() + 1) : 1; // 1 기반 페이지 번호
        int currentPageSize = hasContent ? page.getNumberOfElements() : 0; // 현재 페이지의 요소 수
        int totalPageNum = hasContent ? page.getTotalPages() : 1; // 총 페이지 수
        boolean hasNext = hasContent && page.hasNext(); // 다음 페이지 여부

        return CategoryResultDTO.builder()
                .transactionHistoryList(transactionHistoryList)
                .currentPageNum(currentPageNum)
                .currentPageSize(currentPageSize)
                .totalPageNum(totalPageNum)
                .listSize(transactionHistoryList.size())
                .hasNext(hasNext)
                .build();
    }

    private <T> List<AuctionBaseEntity> convertListObject(List<T> list) {
        if (list == null) {
            return Collections.emptyList();
        }
        return list.stream()
                .filter(AuctionBaseEntity.class::isInstance) // AuctionBaseEntity 인스턴스만 필터링
                .map(AuctionBaseEntity.class::cast) // 타입 캐스팅
                .collect(Collectors.toList()); // 리스트로 수집
    }


    /**
     * @author homin
     * @created 2024. 11. 1. 오후 12:43
     * @updated 2024. 11. 1. 오후 12:43
     * @param  transactionHistoryList AuctionBaseEntity 에 담긴 리스트 데이터
     * @info Excel 시트를 위한 로직
     */
    // 엑셀 파일 생성 메서드
    @Override
    public byte[] createExcelFile(List<AuctionBaseEntity> transactionHistoryList) {
        // 거래 내역이 없을 경우 처리
        if (transactionHistoryList == null || transactionHistoryList.isEmpty()) {
            throw new IllegalArgumentException("거래 내역이 없습니다.");
        }

        // 엑셀 워크북 생성
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("TransactionHistory");

            // 헤더 생성
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("날짜");
            headerRow.createCell(1).setCellValue("거래시장");
            headerRow.createCell(2).setCellValue("대분류");
            headerRow.createCell(3).setCellValue("중분류");
            headerRow.createCell(4).setCellValue("상품등급");
            headerRow.createCell(5).setCellValue("상품명");
            headerRow.createCell(6).setCellValue("거래단위");
            headerRow.createCell(7).setCellValue("가격");

            // 데이터 추가
            int rowNum = 1;
            for (AuctionBaseEntity entity : transactionHistoryList) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(entity.getDelDate().toString());
                row.createCell(1).setCellValue(entity.getMarket_name());
                row.createCell(2).setCellValue(entity.getLarge());
                row.createCell(3).setCellValue(entity.getMiddle());
                row.createCell(4).setCellValue(entity.getProductRank());
                row.createCell(5).setCellValue(entity.getProductName());
                row.createCell(6).setCellValue(entity.getDel_unit());
                row.createCell(7).setCellValue(entity.getPrice());
            }

            // 엑셀 파일 기록
            workbook.write(outputStream);
            return outputStream.toByteArray(); // 엑셀 파일 바이트 배열 반환

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("엑셀 파일 생성 중 오류 발생");
        }
    }

}



