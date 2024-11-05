package com.example.actionprice.auctionData.detailCategory;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import java.util.Map;

/**
 * api로부터 메인데이터를 추출하는 과정에서 변환하는 데에 쓰이는 객체들을 모아둔 컴포넌트
 * @author 연상훈
 * @created 2024-10-16 오후 3:01
 * @updated 2024-10-16 오후 3:01
 * @info : 추후 라이브서비스 때 scheduler에 넣기 위해 컴포넌트로 만듬
 * @see :
 * 표로 정리된 구글 시트 링크
 * https://docs.google.com/spreadsheets/d/1v83UPPUlm5D36oOcQTuxpi7xn1DBw-OsUCiFhKff2-M/edit?gid=0#gid=0
 */
@Component
@Getter
@Log4j2
public class AllSortingComponent {

    /**
     * 시장코드 모음집
     * @author 연상훈
     * @created 2024-10-17 오후 1:34
     */
    private final Map<String, String> market_code_map = Map.ofEntries(
        Map.entry("1101", "서울"),
        Map.entry("2100", "부산"),
        Map.entry("2200", "대구"),
        Map.entry("2300", "인천"),
        Map.entry("2401", "광주"),
        Map.entry("2501", "대전"),
        Map.entry("2601", "울산"),
        Map.entry("3111", "수원"),
        Map.entry("3214", "강릉"),
        Map.entry("3211", "춘천"),
        Map.entry("3311", "청주"),
        Map.entry("3511", "전주"),
        Map.entry("3711", "포항"),
        Map.entry("3911", "제주"),
        Map.entry("3113", "의정부"),
        Map.entry("3613", "순천"),
        Map.entry("3714", "안동"),
        Map.entry("3814", "창원"),
        Map.entry("3145", "용인"),
        Map.entry("2701", "세종"),
        Map.entry("3112", "성남"),
        Map.entry("3138", "고양"),
        Map.entry("3411", "천안"),
        Map.entry("3818", "김해")
    );

    private final Map<String, String> grand_sort = Map.of(
        "100", "식량작물",
        "200", "채소류",
        "300", "특용작물",
        "400", "과일류",
        "500", "축산물",
        "600", "수산물"
    );
}
