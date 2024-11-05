package com.example.actionprice.AuctionData.service;

import com.example.actionprice.AuctionData.entity.AuctionBaseEntity;
import com.example.actionprice.AuctionData.entity.AuctionEntity_ani;
import com.example.actionprice.AuctionData.entity.AuctionEntity_fish;
import com.example.actionprice.AuctionData.entity.AuctionEntity_foodCrops;
import com.example.actionprice.AuctionData.entity.AuctionEntity_fruit;
import com.example.actionprice.AuctionData.entity.AuctionEntity_vegetable;
import com.example.actionprice.AuctionData.entity.AuctionEntity_specialCrop;
import com.example.actionprice.AuctionData.repository.AniEntity_repo;
import com.example.actionprice.AuctionData.repository.FishEntity_repo;
import com.example.actionprice.AuctionData.repository.FoodCropsEntity_repo;
import com.example.actionprice.AuctionData.repository.FruitEntity_repo;
import com.example.actionprice.AuctionData.repository.VegetableEntity_repo;
import com.example.actionprice.AuctionData.repository.SpecialCropsEntity_repo;
import com.example.actionprice.AuctionData.originAuctionData.originApiRequestObj.OriginAuctionDataRow;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Log4j2
public class AuctionEntityServiceImpl implements AuctionEntityService {

  private final AniEntity_repo aniEntity_repo;
  private final FishEntity_repo fishEntity_repo;
  private final FoodCropsEntity_repo foodCropsEntity_repo;
  private final FruitEntity_repo fruitEntity_repo;
  private final VegetableEntity_repo vegetableEntity_repo;
  private final SpecialCropsEntity_repo specialCropsEntity_repo;

  private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

  /**
   * 객체별로 저장하는 메서드
   * @param row
   * @param date : 날짜(String / 구분자 "-")
   * @param marketName : 지역 이름
   * @param category : 대분류 이름
   * @author 연상훈
   * @created 2024-10-25 오전 12:53
   * @info
   */
  @Override
  public AuctionBaseEntity saveEntityByCategory(OriginAuctionDataRow row, String date, String marketName, String category) {

    log.info("[class] AuctionEntityServiceImpl - [method] saveEntityByCategory");
    AuctionBaseEntity entity = null;


    switch (category) {
      case "식량작물":
        entity = convertRowToFoodCrops_andSave(row, date, marketName, category);
        break;
      case "채소류":
        entity = convertRowToVegetable_andSave(row, date, marketName, category);
        break;
      case "특용작물":
        entity = convertSpecialCrops_andSave(row, date, marketName, category);
        break;
      case "과일류":
        entity = convertRowToFruit_andSave(row, date, marketName, category);
        break;
      case "축산물":
        entity = convertRowToAni_andSave(row, date, marketName, category);
        break;
      case "수산물":
        entity = convertRowToFish_andSave(row, date, marketName, category);
        break;
      default:
        break;
    }

    log.info("save successful");
    return entity;
  }

  private AuctionBaseEntity convertRowToAni_andSave(OriginAuctionDataRow row, String date, String marketName, String category) {
    AuctionEntity_ani ani = AuctionEntity_ani.builder()
        .delDate(convertStrToLocalDate(date))
        .large(category)
        .middle(row.getItem_name())
        .productName(row.getKind_name())
        .price(convertStrToPrice(row.getDpr1()))
        .productRank(row.getRank())
        .market_name(marketName)
        .del_unit(row.getUnit())
        .build();

    return aniEntity_repo.save(ani);
  }

  private AuctionBaseEntity convertRowToFish_andSave(OriginAuctionDataRow row, String date, String marketName, String category) {
    AuctionEntity_fish fish = AuctionEntity_fish.builder()
        .delDate(convertStrToLocalDate(date))
        .large(category)
        .middle(row.getItem_name())
        .productName(row.getKind_name())
        .price(convertStrToPrice(row.getDpr1()))
        .productRank(row.getRank())
        .market_name(marketName)
        .del_unit(row.getUnit())
        .build();

    return fishEntity_repo.save(fish);
  }

  private AuctionBaseEntity convertRowToFoodCrops_andSave(OriginAuctionDataRow row, String date, String marketName, String category) {
    AuctionEntity_foodCrops foodCrops = AuctionEntity_foodCrops.builder()
        .delDate(convertStrToLocalDate(date))
        .large(category)
        .middle(row.getItem_name())
        .productName(row.getKind_name())
        .price(convertStrToPrice(row.getDpr1()))
        .productRank(row.getRank())
        .market_name(marketName)
        .del_unit(row.getUnit())
        .build();

    return foodCropsEntity_repo.save(foodCrops);
  }

  private AuctionBaseEntity convertRowToFruit_andSave(OriginAuctionDataRow row, String date, String marketName, String category) {
    AuctionEntity_fruit fruit = AuctionEntity_fruit.builder()
        .delDate(convertStrToLocalDate(date))
        .large(category)
        .middle(row.getItem_name())
        .productName(row.getKind_name())
        .price(convertStrToPrice(row.getDpr1()))
        .productRank(row.getRank())
        .market_name(marketName)
        .del_unit(row.getUnit())
        .build();

    return fruitEntity_repo.save(fruit);
  }

  private AuctionBaseEntity convertRowToVegetable_andSave(OriginAuctionDataRow row, String date, String marketName, String category) {
    AuctionEntity_vegetable produce = AuctionEntity_vegetable.builder()
        .delDate(convertStrToLocalDate(date))
        .large(category)
        .middle(row.getItem_name())
        .productName(row.getKind_name())
        .price(convertStrToPrice(row.getDpr1()))
        .productRank(row.getRank())
        .market_name(marketName)
        .del_unit(row.getUnit())
        .build();

    return vegetableEntity_repo.save(produce);
  }

  private AuctionBaseEntity convertSpecialCrops_andSave(OriginAuctionDataRow row, String date, String marketName, String category) {
    AuctionEntity_specialCrop specialCrop = AuctionEntity_specialCrop.builder()
        .delDate(convertStrToLocalDate(date))
        .large(category)
        .middle(row.getItem_name())
        .productName(row.getKind_name())
        .price(convertStrToPrice(row.getDpr1()))
        .productRank(row.getRank())
        .market_name(marketName)
        .del_unit(row.getUnit())
        .build();

    return specialCropsEntity_repo.save(specialCrop);
  }

  private Integer convertStrToPrice(String str){
    str = str.replace(",", "");
    return Integer.parseInt(str);
  }

  private LocalDate convertStrToLocalDate(String str) {
    if (str == null || str.length() == 0) {
      log.error("[class] AllSortingComponent > [method] convertStrToLocalDate > 입력된 값이 null입니다.");
      return LocalDate.parse("19991231", formatter);
    }
    str = str.replace("-", "");
    return LocalDate.parse(str, formatter);
  }
}
