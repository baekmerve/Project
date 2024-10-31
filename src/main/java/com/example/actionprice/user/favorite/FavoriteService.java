package com.example.actionprice.user.favorite;

import java.util.List;
import java.util.Map;

public interface FavoriteService {

  Map<String, String> createFavorite(
      String large,
      String middle,
      String small,
      String rank,
      String logined_username,
      String favorite_name
  );

  Map<String, String> deleteFavorite(String logined_username, String favorite_name);

  List<FavoriteSimpleDTO> getFavoriteList(String logined_username);
}
