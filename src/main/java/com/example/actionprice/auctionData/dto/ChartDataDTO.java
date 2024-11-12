package com.example.actionprice.auctionData.dto;

import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class ChartDataDTO {
  private String timeIntervals;
  private List<Map<String, Object>> chartDataList;
  private Set<String> countries;

  public ChartDataDTO(
      String timeIntervals,
      List<Map<String, Object>> chartDataList,
      Set<String> countries
  ) {
    this.timeIntervals = timeIntervals;
    this.chartDataList = chartDataList;
    this.countries = countries;
  }
}
