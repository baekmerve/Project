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

/**
 * 즐겨찾기 컨트롤러
 * @author 연상훈
 * @created 2024-10-28 오후 2:12
 * @info update 기능 없이, create와 delete만 있을 거임.
 */
@RestController
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/api/category")
public class FavoriteController {

  private final FavoriteService favoriteService;

  /**
   * 즐겨찾기 생성 메서드
   * @param large 대분류
   * @param middle 중분류
   * @param small 소분류
   * @param rank 품질/등급
   * @param requestBody favorite_name을 담고 있어야 함
   * @author 연상훈
   * @created 2024-11-08 오후 1:00
   * @info 로그인 한 사용자만 이용 가능
   */
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

    UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

    String logined_username = userDetails.getUsername();
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

  /**
   * 즐겨찾기 삭제 메서드
   * @param favoriteId
   * @author 연상훈
   * @created 2024-11-08 오후 1:03
   * @info 로그인 한 사용자만 이용 가능
   * @info 인증은 내부적으로 알아서 진행함
   */
  @PreAuthorize("isAuthenticated()")
  @PostMapping("/favorite/{favoriteId}/delete")
  public Map<String, Object> deleteFavorite(@PathVariable("favoriteId") Integer favoriteId){
    log.info("[class] deleteFavorite - [method] deleteFavorite > 실행");

    UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

    String logined_username = userDetails.getUsername();

    SimpleGrantedAuthority adminAuthority = new SimpleGrantedAuthority(UserRole.ROLE_ADMIN.name()); // 비교대상
    boolean isAdmin = userDetails.getAuthorities().contains(adminAuthority);

    boolean isDeleted = favoriteService.deleteFavorite(favoriteId, logined_username, isAdmin);

    if(isDeleted){
      log.info("[class] deleteFavorite - [method] deleteFavorite - 성공");
      return Map.of("status", "success", "favoriteId", favoriteId);
    }

    log.info("[class] deleteFavorite - [method] deleteFavorite - 실패");
    return Map.of("status", "failure", "favoriteId", favoriteId);
  }
}
