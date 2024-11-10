package com.example.actionprice.auctionData.service;

import com.example.actionprice.auctionData.dto.CategoryResultDTO;
import com.example.actionprice.auctionData.dto.ChartDataDTO;
import com.example.actionprice.auctionData.dto.ChartDataElement;
import com.example.actionprice.auctionData.entity.AuctionBaseEntity;
import com.example.actionprice.auctionData.entity.AuctionEntity_ani;
import com.example.actionprice.auctionData.entity.AuctionEntity_fish;
import com.example.actionprice.auctionData.entity.AuctionEntity_foodCrops;
import com.example.actionprice.auctionData.entity.AuctionEntity_fruit;
import com.example.actionprice.auctionData.entity.AuctionEntity_vegetable;
import com.example.actionprice.auctionData.entity.AuctionEntity_specialCrop;
import com.example.actionprice.auctionData.repository.AniEntity_repo;
import com.example.actionprice.auctionData.repository.FishEntity_repo;
import com.example.actionprice.auctionData.repository.FoodCropsEntity_repo;
import com.example.actionprice.auctionData.repository.FruitEntity_repo;
import com.example.actionprice.auctionData.repository.VegetableEntity_repo;
import com.example.actionprice.auctionData.repository.SpecialCropsEntity_repo;
import com.example.actionprice.auctionData.originAuctionData.originApiRequestObj.OriginAuctionDataRow;
import com.example.actionprice.exception.InvalidCategoryException;
import com.example.actionprice.exception.TransactionDataNotFoundException;
import java.io.ByteArrayOutputStream;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
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
   * 그래프를 그리기 위한 데이터를 반환하는 메서드
   * @author 연상훈
   * @created 2024-11-09 오후 5:02
   */
  @Override
  public ChartDataDTO getChartData(String large,
      String middle,
      String small,
      String rank,
      LocalDate startDate,
      LocalDate endDate
  ) {
    List<AuctionBaseEntity> transactionHistoryList = fetchTransactionHistoryList(large, middle, small, rank, startDate, endDate);
    int daysBetween = (int)ChronoUnit.DAYS.between(startDate, endDate);
    String timeIntervals = "일간";

    // 주간 또는 월간으로 표현해야 하면
    if(daysBetween >= 21){
      if(daysBetween >= 93){
        timeIntervals = "월간";
        // 월간
        transactionHistoryList = transactionHistoryList.stream()
            .map(entity -> {
              LocalDate currentDate = entity.getDelDate();
              entity.setDelDate(currentDate.withDayOfMonth(1));
              return entity;
            })
            .toList();
      } else {
        // 주간
        timeIntervals = "주간";
        transactionHistoryList = transactionHistoryList.stream()
            .map(entity -> {
              LocalDate currentDate = entity.getDelDate();
              entity.setDelDate(currentDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)));
              return entity;
            })
            .toList();
      }
    }

    List<Map<String, Object>> chartDataList =
        convertTransactionHistoryListToChartData(transactionHistoryList);

    List<String> countries = chartDataList.stream()
        .flatMap(map -> map.entrySet()
            .stream()
            .filter(ele1 -> !ele1.getKey().equals("date"))
            .map(ele2 -> ele2.getKey()))
        .distinct()
        .toList();

    return new ChartDataDTO(timeIntervals, chartDataList, countries);
  }

  /**
   * 거래내역을 페이지 형식으로 출력하는 메서드
   * @author 연상훈
   * @created 2024-11-09 오전 11:31
   */
  @Override
  public CategoryResultDTO getCategoryAndPage(
      String large,
      String middle,
      String small,
      String rank,
      LocalDate startDate,
      LocalDate endDate,
      Integer pageNum
  ) {

    // 페이징 및 정렬 조건 설정
    Pageable pageable = PageRequest.of(pageNum, 10, Sort.by(Sort.Order.desc("delDate")));
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
        throw new InvalidCategoryException("Invalid category: " + large);
    }

    // 마지막에 페이지네이션 시키면서 resultDTO로 변환. 자잘한 계산은 메서드 안에서 처리됨
    return convertPageToDTO(pageResult);
  }

  /**
   * 엑셀파일을 위한 카테고리 리스트를 만드는 메서드
   * @author 연상훈
   * @created 2024-11-09 오전 11:30
   * @info
   */
  @Override
  public List<AuctionBaseEntity> fetchTransactionHistoryList(
      String large,
      String middle,
      String small,
      String rank,
      LocalDate startDate,
      LocalDate endDate
  ) {

    switch (large) {
      case "축산물":
        return convertListObject(aniEntity_repo.findByLargeAndMiddleAndProductNameAndProductRankAndDelDateBetween(
            large,
            middle,
            small,
            rank,
            startDate,
            endDate)
        );
      case "수산물":
        return convertListObject(fishEntity_repo.findByLargeAndMiddleAndProductNameAndProductRankAndDelDateBetween(
            large,
            middle,
            small,
            rank,
            startDate,
            endDate)
        );
      case "식량작물":
        return convertListObject(foodCropsEntity_repo.findByLargeAndMiddleAndProductNameAndProductRankAndDelDateBetween(
            large,
            middle,
            small,
            rank,
            startDate,
            endDate)
        );
      case "과일류":
        return convertListObject(fruitEntity_repo.findByLargeAndMiddleAndProductNameAndProductRankAndDelDateBetween(
            large,
            middle,
            small,
            rank,
            startDate,
            endDate)
        );
      case "특용작물":
        return convertListObject(specialCropsEntity_repo.findByLargeAndMiddleAndProductNameAndProductRankAndDelDateBetween(
            large,
            middle,
            small,
            rank,
            startDate,
            endDate)
        );
      case "채소류":
        return convertListObject(vegetableEntity_repo.findByLargeAndMiddleAndProductNameAndProductRankAndDelDateBetween(
            large,
            middle,
            small,
            rank,
            startDate,
            endDate)
        );
      default:
        throw new InvalidCategoryException("Invalid category: " + large);
    }
  }

  /**
   * 엑셀 파일 생성 메서드
   * @author 연상훈
   * @created 2024-11-09 오전 11:30
   */
  @Override
  public byte[] createExcelFile(List<AuctionBaseEntity> transactionHistoryList) {
    // 거래 내역이 없을 경우 처리
    if (transactionHistoryList == null || transactionHistoryList.isEmpty()) {
      throw new TransactionDataNotFoundException("거래 내역이 없습니다.");
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
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        row.createCell(0).setCellValue(entity.getDelDate().format(formatter));
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
      throw new TransactionDataNotFoundException("엑셀 파일 생성 중 오류 발생");
    }
  }

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

    switch (category) {
      case "식량작물":
        return convertRowToFoodCrops_andSave(row, date, marketName, category);
      case "채소류":
        return convertRowToVegetable_andSave(row, date, marketName, category);
      case "특용작물":
        return convertSpecialCrops_andSave(row, date, marketName, category);
      case "과일류":
        return convertRowToFruit_andSave(row, date, marketName, category);
      case "축산물":
        return convertRowToAni_andSave(row, date, marketName, category);
      case "수산물":
        return convertRowToFish_andSave(row, date, marketName, category);
      default:
        return null;
    }
  }

  /**
   * DB에서 나온 거래내역 데이터를 그래프에 사용하기에 적합하도록 날짜별/지역별로 합쳐주는 메서드(그래프 데이터에 사용)
   * @author 연상훈
   * @created 2024-11-09 오후 4:16
   * @info 그냥 List<Map<String, Object>> 형태가 그래프 만들기에 좋다고 해서 그거에 맞춰준 것뿐임.
   * @info 최종적으로 List<Map<String,Obejct>> 형태로 구성해야 함
   * @info 안에 들어가는 map은 Map<String, Object> map = Map.of("date", date, "country1", averagePrice1, ...); 형태로 구성해야 함
   * @info Collectors.toMap()을 사용할 때, 키값이 중복되는 것이 들어오면 에러가 발생하면서 그 에러를 해결하기 위한 추가적인 로직을 실행하게 됨
   * @info 그 추가적으로 실행하는 로직을 이용해서 중복값이 있으면 그것이 계속해서 누적되거나 병합되도록 구성함
   */
  private List<Map<String, Object>> convertTransactionHistoryListToChartData(List<AuctionBaseEntity> transactionHistoryList){
    List<Map<String, Object>> dataList = transactionHistoryList.stream()
        .collect(Collectors.groupingBy(
            AuctionBaseEntity::getDelDate, // 그룹으로 묶을 기준
            Collectors.toMap( // 그룹을 하나의 map으로 구성
                AuctionBaseEntity::getMarket_name, // key
                entity -> new ChartDataElement(entity.getPrice()), // value(ChartDataElement)
                (existing, incoming) -> { // key 중복 시 발생할 에러의 해결 로직
                  existing.stackData(incoming); // 값 누적
                  return existing;
                })
        )) // 현 시점에서 객체 형태 Map<LocalDate, Map<String, ChartDataElement>>
        .entrySet() // map의 특성을 이용한 중복값 거르기가 끝났으니, 리스트로 변환
        .stream()
        .map(entry -> {
          // 각 날짜별로 맵을 구성함
          // entry[key : delDate, value = Map<marketName, chartDataElement>]
          Map<String, Object> elementMap = new HashMap<>();
          elementMap.put("date", entry.getKey()); // 날짜값 옮기기
          entry.getValue() // = Map<marketName, chartDataElement>
              .forEach((country, chartDataElement) -> // (key, value)
                  elementMap.put(country, (chartDataElement.getPrice()/chartDataElement.getCount()))
              ); // 그 외에는 [지역 이름 : 평균가격] 형태로 옮기기
          return elementMap; // 그렇게 map 하나로 완성해서 리턴
        })
        .sorted(Comparator.comparing(map -> (LocalDate) map.get("date"))) // 정렬
        .toList(); // List<Map<>>

    return dataList;
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
        .map(AuctionBaseEntity.class::cast)  // 타입 캐스팅
        .collect(Collectors.toList()); // 리스트로 수집
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
