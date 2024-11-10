package com.example.actionprice.auctionData.service;

import com.example.actionprice.auctionData.dto.CategoryResultDTO;
import com.example.actionprice.auctionData.dto.ChartDataDTO;
import com.example.actionprice.auctionData.entity.AuctionBaseEntity;
import com.example.actionprice.auctionData.originAuctionData.originApiRequestObj.OriginAuctionDataRow;
import java.time.LocalDate;
import java.util.List;

public interface AuctionEntityService {
  AuctionBaseEntity saveEntityByCategory(
      OriginAuctionDataRow row,
      String date,
      String marketName,
      String category
  );

  CategoryResultDTO getCategoryAndPage(
      String large,
      String middle,
      String small,
      String rank,
      LocalDate startDate,
      LocalDate endDate,
      Integer pageNum
  );

  List<AuctionBaseEntity> fetchTransactionHistoryList(
      String large,
      String middle,
      String small,
      String rank,
      LocalDate startDate,
      LocalDate endDate
  );

  byte[] createExcelFile(List<AuctionBaseEntity> fetchTransactionHistoryList);

  ChartDataDTO getChartData(
      String large,
      String middle,
      String small,
      String rank,
      LocalDate startDate,
      LocalDate endDate
  );
}
