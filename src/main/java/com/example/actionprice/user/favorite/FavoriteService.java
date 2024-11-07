package com.example.actionprice.user.favorite;

import java.util.List;

public interface FavoriteService {

  FavoriteSimpleDTO createFavorite(
      String large,
      String middle,
      String small,
      String rank,
      String logined_username,
      String favorite_name
  );

  boolean deleteFavorite(Integer favoriteId, String logined_username, boolean isAdmin);

  List<FavoriteSimpleDTO> getFavoriteList(String logined_username);
}
