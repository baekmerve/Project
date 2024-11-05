package com.example.actionprice.admin;

import com.example.actionprice.security.jwt.refreshToken.RefreshTokenEntity;
import com.example.actionprice.user.User;
import com.example.actionprice.user.UserRole;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.ToString;
import org.springframework.data.domain.Page;

@Getter
@ToString
public class UserListDTO {
  public List<UserSimpleDTO> userList;
  private int currentPageNum; // 현재 페이지 번호
  private int currentPageSize; // 현재 페이지에 존재하는 post의 갯수
  private final int itemSizePerPage = 10; // 페이지당 post의 갯수
  private long listSize; // 총 post 갯수
  private int totalPageNum; // 총 페이지
  private boolean hasNext; // 현재가 마지막 페이지인지
  private String keyword; // 검색에 사용된 키워드

  public UserListDTO(Page<User> userPage, String keyword) {
    boolean hasContent = userPage.hasContent();
    this.userList = hasContent ? userPage.getContent()
        .stream()
        .map(user -> {
          RefreshTokenEntity refreshToken = user.getRefreshToken();
          LocalDateTime tokenExpiresAt = (refreshToken == null) ? null : refreshToken.getExpiresAt();
          boolean isBlocked = (refreshToken == null) ? false : refreshToken.isBlocked();
          boolean isAdmin = user.getAuthorities().contains(UserRole.ROLE_ADMIN.name());

          return UserSimpleDTO.builder()
              .username(user.getUsername()).email(user.getEmail())
              .postCount(user.getPostSet().size())
              .commentCount(user.getCommentSet().size())
              .authorities(isAdmin? UserRole.ROLE_ADMIN.name() : UserRole.ROLE_USER.name())
              .tokenExpiresAt(tokenExpiresAt)
              .isBlocked(isBlocked)
              .build();
        })
        .toList() : new ArrayList<UserSimpleDTO>();

    this.currentPageNum = userPage.getNumber() + 1;
    this.currentPageSize = userPage.getNumberOfElements();
    this.listSize = userPage.getTotalElements();
    this.totalPageNum = userPage.getTotalPages();
    this.hasNext = userPage.hasNext();
    this.keyword = keyword;
  }
}
