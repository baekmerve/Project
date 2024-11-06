package com.example.actionprice.user;

import com.example.actionprice.customerService.comment.Comment;
import com.example.actionprice.security.jwt.refreshToken.RefreshTokenEntity;
import com.example.actionprice.user.favorite.Favorite;
import com.example.actionprice.customerService.post.Post;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.BatchSize;

/**
 * @author 연상훈
 * @created 2024-10-05 오후 10:04
 * @updated 2024-10-06 오후 12:29 : 간편한 권한 관리를 위해 Set<String> authorities 사용
 * @updated 2024-10-20 오전 10:16 : set 객체들의 add와 remove 로직 간략화.
 * @updated 2024-10-20 오후 12:33 : RefreshTokenEntity 객체와 연결함
 * 중복된 것을 추가하거나 없는 것을 삭제하면 오류가 난다고 알고 있었는데, 다시 조사해 보니 그냥 true/false만 반환함.
 * 그러면 검사 로직이 따로 필요가 없으니 다 빼버림
 * @info 1. 순환참조의 문제를 피하기 위해 @ToString(exclude={})와 @JsonManagedReference를 사용했습니다.
 * @info 2. 원활한 하위 객체 관리를 위해 orphanRemoval = true과 cascade = {CascadeType.ALL}를 사용했습니다.
 * @info 3. 객체 호출 시의 효율을 위해 fetch = FetchType.LAZY와 @BatchSize(size)를 사용했습니다.
 * @info 4. 이메일은 회원가입 시 필수이기 때문에 unique이며, not null입니다.
 * @info 5. 즐겨찾기가 과도하게 늘어나는 것을 막기 위해 10개로 제한했습니다.
 * @info 6. OneToMany 관계에 있는 객체의 컬렉션 타입은, 해당 객체를 쉽게 찾아낼 수 있도록 set으로 설정했습니다.
 * @info 7. jwt 토큰은 로그인 시에 발급하는 것이지, 계정 생성 시에 발급하는 것이 아니기 때문에 생성할 당시에는 발급되지 않습니다. 그렇기 때문에 nullable입니다.
 * @info 8. 추후 수정 및 추가가 예고된 항목들의 수정 및 추가를 위해 Builder를 사용하는 것은 불필요한 코드의 증가를 야기하므로, set 메서드를 지정하여 단순화합니다.
 */
@Entity
@Table(name="user")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = {"postSet", "commentSet", "favoriteSet"}) //exclude : 제외시킨다
public class User {

  // field

  // field - basic
  @Id
  @Column(name = "username")
  @Size(min = 6, max=20)
  private String username;

  @Column(nullable=false, name = "password")
  private String password;

  @Column(nullable=false, unique=true)
  @Email
  private String email;

  @Column(nullable = false)
  @Builder.Default
  private int loginFailureCount = 0;

  @Column(nullable = true)
  private LocalDateTime lockedAt;

  @OneToOne(mappedBy = "user",
      orphanRemoval = true,
      cascade = {CascadeType.ALL},
      fetch = FetchType.LAZY)
  @JoinColumn(name = "refresh_token_id", nullable = true) //유저 생성당시는 null일 수 밖에 없음 로그인시 생성되기에
  private RefreshTokenEntity refreshToken;

  /**
   * 유저 권한
   * @author : 연상훈
   * @created : 2024-10-11 오후 11:33
   * @updated : 2024-10-11 오후 11:33
   * @info : 권한 관리를 간편하게 하기 위해 Set<String>으로 변경
   */
  @ElementCollection(fetch = FetchType.EAGER)
  @Builder.Default
  private Set<String> authorities = new HashSet<>(); // 권한 객체를 별도로 관리해서 유저 생성할 때마다 권한이 쓸데없이 늘어나는 것을 방지

  // field - relationship
  @JsonManagedReference //부모객체에서 자식객체 관리 json형태로 반환될때 이게 부모라는것을 알려줌
  @OneToMany(mappedBy = "user",
      orphanRemoval = true, //유저에게서 나가면 삭제가 됨
      cascade = {CascadeType.ALL}, //유저객체에서 포스트를 불러옴 ,포스트 내용을 수정 그것을 세이브 (전체반영)
      fetch = FetchType.LAZY)
  @BatchSize(size = 10) //한번에 불러오는 양
  @Builder.Default //만들때 빌더를 사용해 자신의 필드를 다 설정해야하지만 안하면 null 이기에 dafault는 new HashSet<>() 이다
  private Set<Post> postSet = new HashSet<>(); //게시글 (유저 참조중)

  @JsonManagedReference
  @OneToMany(mappedBy = "user",
      orphanRemoval = true,
      cascade = {CascadeType.ALL}, //변환할떄
      fetch = FetchType.LAZY) //유저를 불러올떄 포스트는 나중에 불러오게 함
  @BatchSize(size = 10)
  @Builder.Default
  private Set<Comment> commentSet = new HashSet<>(); //댓글

  @JsonManagedReference
  @OneToMany(mappedBy = "user",
      orphanRemoval = true,
      cascade = {CascadeType.ALL},
      fetch = FetchType.LAZY)
  @BatchSize(size = 10)
  @Size(max = 10)
  @Builder.Default
  private Set<Favorite> favoriteSet = new HashSet<>();

  // method
  public void setRefreshToken(RefreshTokenEntity refreshToken) {
    this.refreshToken = refreshToken;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public int getLoginFailureCount() {
    return loginFailureCount;
  }

  public void setLoginFailureCount(int loginFailureCount) {
    this.loginFailureCount = loginFailureCount;
  }

  public LocalDateTime getLockedAt() {
    return lockedAt;
  }

  public void setLockedAt(LocalDateTime lockedAt) {
    this.lockedAt = lockedAt;
  }

  /**
   * @param isRoleSetForAdmin admin을 위한 roleSet인지 여부
   * @author 연상훈
   * @created 2024-11-04 오후 3:16
   * @info true면 admin / false면 user
   */
  public boolean setUserRoles(boolean isRoleSetForAdmin){
    this.authorities = isRoleSetForAdmin ?
        Set.of(UserRole.ROLE_ADMIN.name(), UserRole.ROLE_USER.name())
        : Set.of(UserRole.ROLE_USER.name());
    return isRoleSetForAdmin;
  }

  public boolean addPost(Post post) {
    return this.postSet.add(post);
  }

  public boolean removePost(Post post) {
    return this.postSet.remove(post);
  }

  public boolean addComment(Comment comment) {
    return this.commentSet.add(comment);
  }

  /**
   * @author 연상훈
   * @created 2024-10-06 오후 12:17
   * @info comment는 user와 post를 모두 부모로 가지기 때문에 제거할 때 더 조심해야 함. comment 객체의 삭제 과정 설명
   * @info 1. 부모 객체에서 지움
   * @info 2. orphanRemoval = true, cascade = {CascadeType.ALL}로, 부모와의 연결 끊어지면 지워짐
   * @info 3. 근데 지워진다는 신호를 받으면 지워지기 전에 @EntityListeners(CommentListener.class) 내부의 comment 객체 제거 로직이 발동
   * @info 4. 양쪽 부모에서 안전하게 지워짐.
   */
  public boolean removeComment(Comment comment) {
    return this.commentSet.remove(comment);
  }

  public boolean addFavorite(Favorite favorite) {
    return this.favoriteSet.add(favorite);
  }

  public boolean removeFavorite(Favorite favorite) {
    return this.favoriteSet.remove(favorite);
  }
}
