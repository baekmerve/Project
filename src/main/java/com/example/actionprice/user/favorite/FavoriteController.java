package com.example.actionprice.user.favorite;

import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;

// return을 responseEntity로 해서 에러 처리도 같이 해버릴까?
/**
 * 즐겨찾기 컨트롤러
 * @author 연상훈
 * @created 2024-10-28 오후 2:12
 * @info update 기능 없이, create와 delete만 있을 거임.
 */
@RestController
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/api/category") // 상황에 따라 많이 바꿔야 할 수 있음
public class FavoriteController {

  private final FavoriteService favoriteService;

  // principal을 쓸까 그냥 요청으로 받을까
  @PostMapping("/{large}/{middle}/{small}/{rank}/favorite")
  public Map<String, String> createFavorite(
      @PathVariable("large") String large,
      @PathVariable("middle") String middle,
      @PathVariable("small") String small,
      @PathVariable("rank") String rank,
      @RequestBody Map<String, String> requestBody
  ){
    String logined_username = requestBody.get("logined_username");
    if (logined_username == null || logined_username.isEmpty()) {
      log.error("logined_username is null or empty");
      return Map.of("status", "bad request", "messege", "logined_username is null or empty");
    }

    String favorite_name = requestBody.get("favorite_name");
    if (favorite_name == null || favorite_name.isEmpty()) {
      log.error("favorite_name is null or empty");
      return Map.of("status", "bad request", "messege", "favorite_name is null or empty");
    }

    return favoriteService.createFavorite(
            large,
            middle,
            small,
            rank,
            logined_username,
            favorite_name
    );
  }

  @PostMapping("/favorite/{favorite_name}/delete")
  public Map<String, String> deleteFavorite(
          @PathVariable("favorite_name") String favorite_name,
          @RequestBody Map<String, String> requestBody
  ){
    String logined_username = requestBody.get("logined_username");
    if (logined_username == null || logined_username.isEmpty()) {
      log.error("logined_username is null or empty");
      return Map.of("status", "bad request", "messege", "logined_username is null or empty");
    }

    return favoriteService.deleteFavorite(logined_username, favorite_name);
  }

  @GetMapping("/favorite/getlist")
  public Map<String, Object> getFavoriteList(@RequestBody Map<String, String> requestBody){
    String logined_username = requestBody.get("logined_username");
    if (logined_username == null || logined_username.isEmpty()) {
      log.error("logined_username is null or empty");
      return Map.of("favoriteList", "no user, no list");
    }

    List<FavoriteSimpleDTO> favoriteList = favoriteService.getFavoriteList(logined_username);

    return Map.of("favoriteList", favoriteList, "username", logined_username);
  }

}
