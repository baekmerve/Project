package com.example.actionprice.auctionData.originAuctionData;


import com.example.actionprice.auctionData.originAuctionData.originApiRequestObj.OriginAuctionDataRow;
import com.example.actionprice.auctionData.originAuctionData.originApiRequestObj.OriginAuctionDocument;
import com.google.gson.*;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.URI;
import java.net.URISyntaxException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;


@Data
@Component
@Log4j2
public class OriginAuctionDataFetcher {

    @Value("${lastAuctionData.url}")
    String lastAuctionUrl;

    @Value("${lastAuctionData.encodedKey}")
    String lastAuctionEncodedKey;

    private final WebClient webClient;
    private final Gson gson;

    public OriginAuctionDataFetcher() {
        ConnectionProvider provider = ConnectionProvider.builder("custom")
            .maxConnections(4000) // 최대 연결 수
            .pendingAcquireMaxCount(2000) // 대기 큐 최대 수
            .build();

        HttpClient httpClient = HttpClient.create(provider);

        this.webClient = WebClient.builder()
            .clientConnector(new ReactorClientHttpConnector(httpClient))
            .build();

        this.gson = new Gson();
    }


    public String getAuctionData_String(String countryCode, String regday, String category_code) throws Exception {

        URI uri = composeUri(countryCode, regday, category_code);

        ResponseEntity<String> responseEntity = webClient.get()
                .uri(uri)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .toEntity(String.class)
                .block();

        return responseEntity.getBody();
    }

    /**
     * 메인데이터 추출(flux)
     * @param countryCode : 지역 코드
     * @param regday : 날짜
     * @param category_code : 대분류 코드
     * @author 연상훈
     * @created 2024-10-25 오전 12:03
     * @info
     */
    public Flux<OriginAuctionDataRow> getAuctionData_Flux(String countryCode, String regday, String category_code) throws Exception {
        URI uri = composeUri(countryCode, regday, category_code);

        return webClient.get()
            .uri(uri)
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .bodyToMono(String.class)
            .flatMapMany(str -> {
                JsonElement jsonElement = JsonParser.parseString(str);
                if (jsonElement.isJsonObject()) {
                    JsonObject jsonObject = jsonElement.getAsJsonObject();
                    JsonElement dataElement = jsonObject.get("data");
                    if (dataElement.isJsonArray()) {
                        JsonArray dataArray = dataElement.getAsJsonArray();
                        if (dataArray.size() > 0 && dataArray.get(0).isJsonPrimitive() && dataArray.get(0).getAsString().equals("001")) {
                            return Mono.empty();
                        }
                    }
                }
                OriginAuctionDocument document = gson.fromJson(str, OriginAuctionDocument.class);
                return Flux.fromIterable(document.getData().getItem());
            });
    }

    private URI composeUri(String countryCode, String regday, String category_code) throws URISyntaxException {

        String p_cert_id = "lhm3052@naver.com"; // 요청자 id
        String apiType = "json"; // 반환 형식
        String p_convert_kg_yn = "Y"; // 중량 변환 여부

        String url = String.format("%s&p_country_code=%s&p_regday=%s&p_convert_kg_yn=Y&p_item_category_code=%s&p_cert_key=%s&p_cert_id=lhm3052@naver.com&p_returntype=json",
                lastAuctionUrl,
                countryCode,
                regday,
                category_code,
                lastAuctionEncodedKey
        );

        log.info(url);

        return new URI(url);
    }

}



