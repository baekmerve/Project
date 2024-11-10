package com.example.actionprice.customerService.post.dto;

import com.example.actionprice.customerService.post.Post;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.ToString;
import org.springframework.data.domain.Page;

/**
 * 게시글 목록 페이지에 사용되는 dto
 * @value postList : 게시글 목록. 생성자로 만들 때 알아서 list로 변환됨. List<PostSimpleDTO>
 * @value currentPageNum : 현재 페이지 번호
 * @value currentPageSize : 현재 페이지에 존재하는 post의 갯수
 * @value itemSizePerPage : 페이지당 post의 갯수(10개로 고정됨)
 * @value listSize : 총 post 갯수
 * @value totalPageNum : 총 페이지
 * @value hasNext : 현재가 마지막 페이지인지
 * @value keyword : 검색에 사용된 키워드
 * @author 연상훈
 * @created 2024-10-27 오후 12:56
 * @info 굳이 Builder로 처리할 필요도 없고, Builder로 처리하기엔 길고 복잡해서 그냥 생성자로 처리.
 * @see "/api/post/list?pageNum=0&keyword=abc" 에 사용됨
 */
@Getter
@ToString
public class PostListDTO {

  private List<PostSimpleDTO> postList;
  private int currentPageNum; // 현재 페이지 번호
  private int currentPageSize; // 현재 페이지에 존재하는 post의 갯수
  private final int itemSizePerPage = 10; // 페이지당 post의 갯수
  private long listSize; // 총 post 갯수
  private int totalPageNum; // 총 페이지
  private boolean hasNext; // 현재가 마지막 페이지인지
  private String keyword; // 검색에 사용된 키워드

  /**
   * PostListDTO의 생성자
   * @param postPages PostRepository에서 반환 받은 Page<Post>. 그대로 넣어주면 됨
   * @param keyword : 검색에 사용된 키워드. 키워드가 없을 경우에는 PostService에서 ""로 처리해줌
   * @author 연상훈
   * @created 2024-10-27 오후 1:05
   */
  public PostListDTO(Page<Post> postPages, String keyword) {
    boolean hasContent = postPages.hasContent();
    this.postList = hasContent ? postPages.getContent()
        .stream()
        .map(post -> PostSimpleDTO.builder()
            .postId(post.getPostId())
            .title(post.getTitle())
            .published(post.isPublished())
            .username(post.getUser().getUsername())
            .createdAt(post.getCreatedAt())
            .page(0)
            .build())
        .toList() : new ArrayList<PostSimpleDTO>();

    this.currentPageNum = postPages.getNumber() + 1; // Page 객체는 첫 페이지가 0부터 시작함. 1부터 시작하려면 1을 더해줘야 함
    this.currentPageSize = postPages.getNumberOfElements();
    this.listSize = postPages.getTotalElements();
    this.totalPageNum = postPages.getTotalPages();
    this.hasNext = postPages.hasNext();
    this.keyword = keyword;
  }
}
