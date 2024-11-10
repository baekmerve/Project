package com.example.actionprice.user.favorite;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

/**
 * favorite entity에서 user 정보를 username만 남기고 반환하는 dto
 * @value favoriteId
 * @value favoriteName
 * @value favoriteURL
 * @value favorite_ownerS_username favorite을 소유한 사용자의 username
 * @author 연상훈
 * @created 2024-11-08 오후 1:04
 */
@Getter
@Builder
@ToString
public class FavoriteSimpleDTO {
  private Integer favoriteId;
  private String favoriteName;
  private String favoriteURL;
  private String favorite_ownerS_username;
}
