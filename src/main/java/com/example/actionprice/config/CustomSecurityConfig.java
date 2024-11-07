package com.example.actionprice.config;

import com.example.actionprice.handler.LoginSuccessHandler;
import com.example.actionprice.security.CustomUserDetailService;
import com.example.actionprice.security.filter.LoginFilter;
import com.example.actionprice.security.filter.RefreshTokenFilter;
import com.example.actionprice.security.filter.TokenCheckFilter;
import com.example.actionprice.security.jwt.accessToken.AccessTokenService;
import com.example.actionprice.user.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

/**
 * 보안 관련된 거의 모든 설정이 있는 곳
 * @info 토큰관련 로직이 추가로 더 필요하긴 함. 필요한 것만 보안을 열어뒀으니 상황에 따라 추가해야 함
 * @author 연상훈
 * @created 24/10/01 13:46
 * @updated 24/10/14 05:26 LoginSuccessHandler가 rememberMe 토큰 생성을 막고 있어서 제거. 그것을 대체하는 LoginFilter - successfulAuthentication 생성함
 * @updated 2024-10-14 오후 12:05 LoginSuccessHandler가 없으면 또 로그인에 문제 생겨서 다시 생성함. 그리고 대부분의 객체를 Bean으로 관리하도록 수정
 * @updated 2024-10-17 오후 7:15 : 리멤버미 삭제
 * @updated 2024-10-19 오후 5:19 : logoutSuccessHandler 추가. jwtUtil을 RefreshTokenService로 통합. RefreshTokenService 안에 jwtUtil 있음
 */
@SuppressWarnings("ALL")
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true)
@RequiredArgsConstructor
@Log4j2
public class CustomSecurityConfig {

    private final CustomUserDetailService userDetailsService;
    private final UserRepository userRepository;
    private final AccessTokenService accessTokenService;

    // method - @Bean
    /**
     * @author : 연상훈
     * @created : 2024-10-05 오후 9:27
     * @updated 2024-10-14 오전 5:38 : 리멤버미 사용을 위해 일부 수정.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

      log.info("------------ security configuration --------------");

      //유저 권한 password 검증
      AuthenticationManager authenticationManager = authenticationManager(http);

      http.sessionManagement(sessionPolicy -> sessionPolicy.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
          .cors(httpSecurityCorsConfigurer -> httpSecurityCorsConfigurer.configurationSource(corsConfigurationSource())) // corsConfigurationSource
          .csrf((csrfconfig) -> csrfconfig.disable()).exceptionHandling(exceptionHandler -> {
              exceptionHandler.accessDeniedHandler(((request, response, accessDeniedException) -> {
                  response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied");
              })).accessDeniedPage("/");
              })
          .authorizeHttpRequests((authz) -> authz
                      .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                      .requestMatchers(
                          "/api/admin/**", // 어드민페이지
                          "/api/post/*/comment/admin/*" // 어드민 코멘트
                      ).hasRole("ADMIN")
                      .requestMatchers(
                          "/api/user/login" // 로그인 요청
                      ).anonymous() // 로그인을 안 한 사람만 이동 가능
                      .requestMatchers(
                          "/api/user/logout",
                          "/api/post/create",
                          "/api/post/*/update",
                          "/api/post/*/delete",
                          "/api/post/*/update/*",// 게시글 생성, 수정, 삭제
                          "/api/mypage/**", // 마이페이지(개인정보 열람, 내 게시글 목록, 내 즐겨찾기 목록, 사용자 삭제)
                          "/api/post/*/detail/**", // 게시글 내 댓글 생성, 수정, 삭제
                          "/api/category/favorite/**", // 즐겨찾기 삭제
                          "/api/category/*/*/*/*/favorite" // 즐겨찾기 생성
                      ).authenticated() // 로그인을 한 사람만 이동 가능
                      .requestMatchers(
                              "/swagger-ui/**", // 스웨거
                              "/v3/api-docs/**", // 스웨거
                              "/", // 홈
                              "/api/user/**", // 사용자 관련 기능들
                              "/api/post/list",
                              "/api/post/list*",// 게시글 목록 열람 가능
                              "/api/post/*/detail",
                              "/api/post/*/detail*", // 게시글 내용 열람 가능
                              "/api/post/comments",
                              "/api/post/comments*", // 게시글 내 댓글 목록 열람 가능
                              "/api/category/**" // 카테고리
                      ).permitAll()
                      .anyRequest().authenticated())
          .authenticationManager(authenticationManager)
          .addFilterBefore(tokenCheckFilter(), UsernamePasswordAuthenticationFilter.class)
          .addFilterBefore(refreshTokenFilter(), TokenCheckFilter.class)
          .addFilterBefore(loginFilter(authenticationManager), UsernamePasswordAuthenticationFilter.class)
          .addFilterBefore(logoutFilter(), UsernamePasswordAuthenticationFilter.class) // 필터 순서에 주의
          .formLogin((formLogin) -> formLogin.disable());
      return http.build();

    }

    @Bean
    AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
      AuthenticationManagerBuilder authenticationManagerBuilder =
              http.getSharedObject(AuthenticationManagerBuilder.class);
      authenticationManagerBuilder.userDetailsService(userDetailsService)
              .passwordEncoder(passwordEncoder());

      return authenticationManagerBuilder.build();
    }

    @Bean
    LoginFilter loginFilter(AuthenticationManager authenticationManager) throws Exception {
      return new LoginFilter(
              "/api/user/login",
              userDetailsService,
              loginSuccessHandler(),
              userRepository,
              authenticationManager
      );
    }

    @Bean
    public LoginSuccessHandler loginSuccessHandler(){
      LoginSuccessHandler loginSuccessHandler = new LoginSuccessHandler(accessTokenService);
      return loginSuccessHandler;
    }

    @Bean
    public RefreshTokenFilter refreshTokenFilter() {
      return new RefreshTokenFilter(accessTokenService);
    }

    /**
     * @author 연상훈
     * @created 24/10/04
     * @updated 24/10/04
     * @see : 리액트-스프링부트 연결을 위한 cors config
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() { // corsConfigurationSource
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000", "http://localhost:8080")); // 허용할 도메인 설정
        configuration.setAllowedMethods(Arrays.asList("*")); // 허용할 HTTP 메서드
        configuration.setAllowedHeaders(Arrays.asList("*")); // 허용할 헤더
        configuration.setAllowCredentials(true); // 자격 증명 허용

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);  // 모든 경로에 대해 CORS 설정 적용
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // TokenCheckFilter 설정을 위한 메서드
    @Bean
    public TokenCheckFilter tokenCheckFilter(){
        return new TokenCheckFilter(userDetailsService, accessTokenService);
    }

  @Bean
  public LogoutFilter logoutFilter() {
    SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
    LogoutSuccessHandler logoutSuccessHandler = new LogoutSuccessHandler() {
      @Override
      public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
          SecurityContextHolder.clearContext();
          log.info("----------------- 로그아웃 ----------------------");
          response.setStatus(HttpServletResponse.SC_OK);  // 200 OK 응답
          response.getWriter().write("logout success");  // JSON 응답
      }
    };

    LogoutFilter logoutFilter = new LogoutFilter(logoutSuccessHandler, logoutHandler);
    logoutFilter.setFilterProcessesUrl("/api/user/logout");

    return logoutFilter;
  }
}
