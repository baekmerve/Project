package com.example.actionprice.user.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

/**
 * 사용자 리스트의 요소로 쓰이거나, 적당히 걸러진 사용자 데이터 반환할 때 쓰는 dto
 * @author 연상훈
 * @created 2024-11-07 오후 11:27
 */
@Builder
@Getter
@ToString
public class UserSimpleDTO {
  String username;
  String email;
  int postCount;
  int commentCount;
  String authorities;
  LocalDateTime tokenExpiresAt;
  boolean isBlocked;
  // favorite은 시스템적으로 사이즈 제한을 걸어뒀으니 굳이 확인하지 않음
}