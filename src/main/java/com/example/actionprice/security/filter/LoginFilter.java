package com.example.actionprice.security.filter;

import com.example.actionprice.security.CustomUserDetailService;
import com.example.actionprice.user.UserRepository;
import com.example.actionprice.user.forms.UserLoginForm;
import com.google.gson.Gson;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;

// Todo 로그인 실패 시의 로직 구체화. 그리고 예외처리도 추가할 필요가 있음
/**
 * 로그인 필터
 * @author 연상훈
 * @created 2024-10-06 오후 3:14
 * @updated 2024-10-14 오전 5:52 : 리멤버미 기능 구현을 위해 대폭 수정
 * @updated 2024-10-14 오후 12:08 : 생성자에서 successHandler와 authenticationManager를 입력 받으면서 set 메서드를 사용.
 * 그리고 로그인 성공 시의 로직을 SuccessHandler로 분리
 * @value userDetailService
 * @value successHandler
 * @info 생성자에 super를 써야 해서 @AllArgs 못 씀
 */
@Log4j2
public class LoginFilter extends AbstractAuthenticationProcessingFilter {

  private final CustomUserDetailService userDetailService;
  private UserRepository userRepository;

  /**
   * LoginFilter 생성자
   * @author : 연상훈
   * @created : 2024-10-14 오전 5:57
   * @updated : 2024-10-14 오전 5:57
   * @info : 받아올 게 많아서 좀 긴 편
   */
  public LoginFilter(
      String defaultFilterProcessesUrl,
      CustomUserDetailService userDetailService,
      UserRepository userRepository,
      AuthenticationManager authenticationManager
  ) {
    super(defaultFilterProcessesUrl);
    this.userDetailService = userDetailService;
    this.userRepository = userRepository;
    setAuthenticationManager(authenticationManager);
  }

  /**
   * 사용자 로그인 시 해당 정보를 받아서 인증 토큰에 입력하는 기능.
   * @author 연상훈
   * @created 2024-10-06 오후 5:50
   * @updated :2024-10-10 오후 5:50
   * @updated 2024-10-17 오후 7:14 : 리멤버미 삭제
   * @info 리액트를 통해 받아오는 request는 request.getParameter 등을 쓸 수 없음.
   * 그래서 귀찮게 일일이 빼오거나 다른 객체로 변환하거나 둘 중 하나이고, 우리는 UserLoginForm 객체 사용함
   */
  @Override
  public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
    log.info("[class] LoginFilter - [method] attemptAuthentication > 시작");

    if(request.getMethod().equalsIgnoreCase("GET")) {

      log.info("GET method doesn't supported");

      return null;
    }
    
    UserLoginForm loginForm = parseRequestJSON(request, UserLoginForm.class);

    log.info(loginForm);

    UserDetails userDetails = userDetailService.loadUserByUsername(loginForm.getUsername());
    log.info("CustomUserDetails : {}", userDetails);

    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, loginForm.getPassword(), userDetails.getAuthorities());
    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

    log.info("[class] LoginFilter - [method] attemptAuthentication > authenticationToken : " + authenticationToken);

    return getAuthenticationManager().authenticate(authenticationToken);
  }

  /**
   * 로그인 성공 시 실행되는 로직
   * @author 연상훈
   * @created 2024-10-14 오전 6:08
   */
  @Override
  protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
      FilterChain chain, Authentication authResult) {
    log.info("----------- LoginFilter - successfulAuthentication -----------");

  }

  /**
   * 로그인 실패 시 실행되는 로직
   * @author : 연상훈
   * @created : 2024-10-14 오전 6:09
   * @info 아직 제대로 구현하진 않았음
   */
  @Override
  protected void unsuccessfulAuthentication(HttpServletRequest request,
      HttpServletResponse response, AuthenticationException failed) {
    log.info("[class] LoginFilter - [method] unsuccessfulAuthentication > 로그인 실패");
  }

  /**
   * 요청의 내용을 json 형태로 변환하는 메서드
   * @author : 연상훈
   * @created : 2024-10-06 오후 5:56
   * @info 책과 달리 UserLoginForm을 사용하기 때문에 데이터를 UserLoginForm 형태에 맞추어 변환하고 반환함.
   */
  private <T> T parseRequestJSON(HttpServletRequest request, Class<T> classObj) {
    try (Reader reader = new InputStreamReader(request.getInputStream())) {
      Gson gson = new Gson();
      return gson.fromJson(reader, classObj);
    } catch (Exception e) {
      log.error(e.getMessage());
    }
    return null;
  }
}
