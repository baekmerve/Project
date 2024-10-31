package com.example.actionprice.user.favorite;

import com.example.actionprice.exception.UserNotFoundException;
import com.example.actionprice.user.User;
import com.example.actionprice.user.UserRepository;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@Log4j2
@RequiredArgsConstructor
public class FavoriteServiceImpl implements FavoriteService {

  private final FavoriteRepository favoriteRepository;
  private final UserRepository userRepository;

  @Override
  public Map<String, String> createFavorite(
      String large,
      String middle,
      String small,
      String rank,
      String logined_username,
      String favorite_name
  ) {
    User user = userRepository.findById(logined_username)
        .orElseThrow(() -> new UserNotFoundException("user(" +logined_username + ") does not exist"));

    String favoriteUrl = String.format(
            "http://localhost:3000/api/category/%s/%s/%s/%s",
            large,
            middle,
            small,
            rank
    );

    Favorite favorite = Favorite.builder()
        .favoriteName(favorite_name)
        .favoriteURL(favoriteUrl)
        .user(user)
        .build();

    // 레포지토리에 저장해야만 id가 발급되고, 그래야 user에 추가 가능
    favorite = favoriteRepository.save(favorite);

    user.addFavorite(favorite);
    userRepository.save(user);

    return Map.of("status", "OK", "method", "create", "username", logined_username, "favoriteName", favorite_name, "url", favoriteUrl);
  }

  // 이건 뭔가 더 효율적인 방법이 있을 것 같은데
  // favorite_name은 중복된 게 있을 수 있어서 이걸로 검색하면 안 됨.
  // 아니면 favorite id로 검색해야 하는데, 어떻게 될 지 몰라서 지금은 일단 이렇게 함
  @Override
  public Map<String, String> deleteFavorite(String logined_username, String favorite_name) {

    User user = userRepository.findById(logined_username)
        .orElseThrow(() -> new UserNotFoundException("user(" +logined_username + ") does not exist"));

    // user와 favorite의 관계상 연결이 끊어지면 자동 삭제
    boolean isFavoriteRemoved = user.getFavoriteSet()
        .removeIf(favorite -> favorite.getFavoriteName().equals(favorite_name));

    // 대신 save를 해서 해당 user에 대한 변경 사항을 레포지토리에 반영해줘야 함
    userRepository.save(user);

    return Map.of("status", "OK", "method", "delete", "username", logined_username, "favoriteName", favorite_name);
  }

  @Override
  public List<FavoriteSimpleDTO> getFavoriteList(String logined_username) {
    List<Favorite> favorites = favoriteRepository.findByUser_Username(logined_username);
    if (favorites.isEmpty() || favorites == null){
      return null;
    }

    return favorites.stream()
        .map(favorite -> FavoriteSimpleDTO.builder()
            .favoriteId(favorite.getFavoriteId())
            .favoriteName(favorite.getFavoriteName())
            .favoriteURL(favorite.getFavoriteURL())
            .favorite_ownerS_username(favorite.getUser().getUsername())
            .build())
        .toList();
  }
}
