package com.example.actionprice.user.favorite;

import java.util.Map;

import com.example.actionprice.user.UserRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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

  @PreAuthorize("isAuthenticated()")
  @PostMapping("/{large}/{middle}/{small}/{rank}/favorite")
  public FavoriteSimpleDTO createFavorite(
      @PathVariable("large") String large,
      @PathVariable("middle") String middle,
      @PathVariable("small") String small,
      @PathVariable("rank") String rank,
      @RequestBody Map<String, String> requestBody
  ){
    log.info("[class] FavoriteController - [method] createFavorite 실행");

    String logined_username = getUsernameWithPrincipal();
    String favorite_name = requestBody.get("favorite_name");

    return favoriteService.createFavorite(
            large,
            middle,
            small,
            rank,
            logined_username,
            favorite_name
    );
  }

  @PostMapping("/favorite/{favoriteId}/delete")
  public Map<String, Object> deleteFavorite(@PathVariable("favoriteId") Integer favoriteId){
    log.info("[class] deleteFavorite - [method] deleteFavorite > 실행");

    boolean isDeleted = favoriteService.deleteFavorite(favoriteId, getUsernameWithPrincipal(), isLoginedUserAdmin());

    if(isDeleted){
      log.info("[class] deleteFavorite - [method] deleteFavorite - 성공");
      return Map.of("status", "success", "favoriteId", favoriteId);
    }

    log.info("[class] deleteFavorite - [method] deleteFavorite - 실패");
    return Map.of("status", "failure", "favoriteId", favoriteId);
  }

  private String getUsernameWithPrincipal(){
    UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    return userDetails.getUsername();
  }

  private boolean isLoginedUserAdmin(){
    UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    SimpleGrantedAuthority adminAuthority = new SimpleGrantedAuthority(UserRole.ROLE_ADMIN.name()); // 비교대상
    return userDetails.getAuthorities().contains(adminAuthority);
  }

}
