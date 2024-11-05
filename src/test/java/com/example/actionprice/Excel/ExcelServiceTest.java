package com.example.actionprice.Excel;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.example.actionprice.AuctionData.entity.AuctionBaseEntity;
import com.example.actionprice.AuctionData.service.AuctionCategoryService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class ExcelServiceTest {

    @Autowired
    AuctionCategoryService auctionCategoryService;

//
//    // 엑셀 응답 테스트
//    @Disabled
//    @Test
//    void createExcelFile_ShouldReturnExcelFile() {
//        List<AuctionBaseEntity> transactionHistoryList = new ArrayList<>();
//
//        // Builder 패턴을 사용하여 객체 생성
//        transactionHistoryList.add(AuctionBaseEntity.builder()
//                .delDate(LocalDate.of(2024, 10, 1))
//                .large("축산물")
//                .middle("돼지")
//                .productName("삼겹살")
//                .productRank("1등급")
//                .del_unit("kg")
//                .price(15000)
//                .build());
//
//        // 엑셀 파일 생성 메서드 호출
//        ResponseEntity<byte[]> responseEntity = auctionCategoryService.createExcelFile(transactionHistoryList);
//        // 응답 상태 및 바이트 배열 확인
//        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
//        assertNotNull(responseEntity.getBody());
//    }
}

