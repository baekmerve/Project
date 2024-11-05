package com.example.actionprice.AuctionData.dto;

import com.example.actionprice.AuctionData.entity.AuctionBaseEntity;
import lombok.Builder;
import lombok.Getter;
import java.util.List;

@Getter
@Builder

public class CategoryResultDTO {

    private List<AuctionBaseEntity> transactionHistoryList; //데이터 리스트
    private int currentPageNum; // 현재 페이지 번호
    private int currentPageSize; // 현재 페이지에 존재하는 데이터 갯수
    private final int itemSizePerPage = 10; // 페이지당 데이터 갯수
    private long listSize; // 총 데이터 갯수
    private int totalPageNum; // 총 데이터 페이지
    private boolean hasNext;  // 다음 페이지가 있는지(= 현재가 마지막 페이지가 아닌지)
}
