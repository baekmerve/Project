package com.example.actionprice.admin;

import com.example.actionprice.security.jwt.refreshToken.RefreshTokenService;
import com.example.actionprice.user.UserService;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
@Log4j2
@RequiredArgsConstructor
public class AdminController {

  private final UserService userService;
  private final RefreshTokenService refreshTokenService;

  @GetMapping("/userList")
  public UserListDTO getUserList(
      @RequestParam(name = "pageNum", defaultValue = "1", required = false) Integer pageNum,
      @RequestParam(name = "keyword", required = false) String keyword
  ) {
    log.info("[class] AdminController - [method] getUserList - page : {} | keyword : {}", pageNum, keyword);
    return userService.getUserList(keyword, pageNum);
  }

  @PostMapping("/userList/block/{username}")
  public Map<String, Object> setBlockUser(@PathVariable("username") String selected_username) {

    boolean result = refreshTokenService.setBlockUserByUsername(selected_username);
    String messege = result ? "blocked" : "unblocked";
    return Map.of("message", messege, "isBlocked", result);
  }

}
