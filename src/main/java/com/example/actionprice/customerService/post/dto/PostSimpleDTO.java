package com.example.actionprice.customerService.post.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

/**
 * PostListDTO의 List에 들어갈 각 게시글의 정보를 담는 객체. 그리고 update하기 전에 이미 입력된 내용 확인할 때도 쓰임
 * @value postId
 * @value title
 * @value content
 * @value published
 * @value username
 * @value createdAt
 * @value commentSize : 각 post가 가진 댓글이 몇 개인지 표시해주기 위해 있는 것
 * @author 연상훈
 * @created 2024-10-27 오후 1:11
 * @see "/api/post/list?pageNum=0&keyword=abc"의 PostListDTO에 사용됨
 */
@Builder
@Getter
@ToString
public class PostSimpleDTO {
  private Integer postId;
  private String title;
  private String content;
  private boolean published;
  private String username;
  private LocalDateTime createdAt;
  private int page;
}
