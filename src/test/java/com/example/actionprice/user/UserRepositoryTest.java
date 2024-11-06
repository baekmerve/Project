package com.example.actionprice.user;

import com.example.actionprice.security.jwt.refreshToken.RefreshTokenEntity;
import com.example.actionprice.security.jwt.refreshToken.RefreshTokenRepository;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootTest
@Log4j2
public class UserRepositoryTest {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Autowired
  private RefreshTokenRepository refreshTokenRepository;

  /**
   * user 삭제하기
   * @author : 연상훈
   * @created : 2024-10-14 오후 12:29
   * @updated : 2024-10-14 오후 12:29
   * @info : 귀찮더라도 이걸로 삭제해야 정상적으로 삭제됨. DB에서 삭제하는 건 되도록 자제
   */
  @Test
  @Disabled
  public void deleteUserTest(){
    String username = "userbbbb";

    User user = userRepository.findById(username).orElse(null);
    if(user != null){
      System.out.println("the user exists. and we delete the user");
      RefreshTokenEntity refreshToken = user.getRefreshToken();
      if(refreshToken != null){
        refreshToken.setUser(null);
        refreshTokenRepository.delete(refreshToken);
      }
      userRepository.delete(user);
      return;
    }
    System.out.println("the user doesn't exists. and we can't delete the user");
  }

  @Test
  @Disabled
  public void userCreateTest() {
    for (int i = 0; i < 50; i++) {
      String number = String.format("%02d", i);
      String username = String.format("user%s", number);
      String email = String.format("abc%s@gmail.com", number);

      User user = User.builder()
          .username(username)
          .password(passwordEncoder.encode("11111111"))
          .email(email)
          .build();

      user.setUserRoles(false);

      userRepository.save(user);
    }
  }

  @Test
  @Disabled
  public void adminCreateTest() {
    String username = "adminadmin";
    String email = String.format("admin%s@gmail.com", 001);

    User user = User.builder()
        .username(username)
        .password(passwordEncoder.encode("11111111"))
        .email(email)
        .build();

    user.setUserRoles(true);

    userRepository.save(user);
  }

}
