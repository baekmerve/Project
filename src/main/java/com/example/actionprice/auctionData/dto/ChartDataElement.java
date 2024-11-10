package com.example.actionprice.auctionData.dto;

import lombok.Getter;

/**
 * 중간에 값을 계산할 때 저장하고, 정리하는 용도
 * @author 연상훈
 * @created 2024-11-10 오전 2:24
 * @info 실제로 리턴에 사용되지는 않음
 */
@Getter
public class ChartDataElement {
  private Integer price;
  private int count;

  public ChartDataElement(Integer price) {
    this.price = price;
    this.count = 1;
  }

  public void stackData(ChartDataElement other){
    this.price += other.getPrice();
    this.count += other.getCount();
  }
}
