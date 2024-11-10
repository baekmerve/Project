package com.example.actionprice.auctionData.dto;

import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class ChartDataDTO {
  private String timeIntervals;
  private List<Map<String, Object>> chartDataList;
  private List<String> countries;

  public ChartDataDTO(String timeIntervals, List<Map<String, Object>> chartDataList, List<String> countries) {
    this.timeIntervals = timeIntervals;
    this.chartDataList = chartDataList;
    this.countries = countries;
  }
}
