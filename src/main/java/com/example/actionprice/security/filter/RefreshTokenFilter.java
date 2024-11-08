package com.example.actionprice.security.filter;

import com.example.actionprice.exception.RefreshTokenException;
import com.example.actionprice.security.jwt.accessToken.AccessTokenService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * 리프레시 토큰 체크 필터
 * @value refreshPath : freshToken 재발급 요청 url. 생성하면서 입력 받음. 현재 "/api/user/generate/refreshToken"
 * @value refreshTokenService
 * @author : 연상훈
 * @created : 2024-10-06 오후 3:05
 * @updated 2024-10-19 오후 5:34 : jwtUtil을 refreshTokenService로 교체하면서 로직 간략화
 * @updated 2024-11-08 오후 12:39 : accessTokenService와 refreshTokenService를 분리하여 관심사 분리 및 로직 구체화
 * @info 리프레시 토큰은 사용자에게 발급되지 않고, 순수하게 DB 내에서만 관리함. 그럼에도 리프레시 토큰이 탈취되거나 변조되었을 가능성을 고려해서 검사를 진행함
 * @info 리프레시 토큰을 통한 악성 사용자 차단 목적도 있음
 */
@Log4j2
@RequiredArgsConstructor
public class RefreshTokenFilter extends OncePerRequestFilter {

  private final AccessTokenService accessTokenService;

  /**
   * 필터링 로직
   * @author : 연상훈
   * @created : 2024-10-06 오후 3:10
   */
  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
    log.info("리프레시 토큰 필터 실행");
    String headerStr = request.getHeader("Authorization");
    log.info("url : {} | headerStr : {}", request.getRequestURI(), headerStr);

    // 토큰이 없거나 Bearer로 시작하지 않으면
    if(headerStr == null || headerStr.contains("undefined") || !headerStr.startsWith("Bearer ")) {
      log.info("익명 사용자로 처리");
      // 익명 사용자로 처리
      filterChain.doFilter(request, response);
      return;
    }

    // 정상적인 형태의 토큰이 존재한다면
    try {
      // 토큰에서 토큰의 내용을 추출함
      String tokenStr = accessTokenService.extractTokenInHeaderStr(headerStr);

      try {
        // 토큰 내용에서 username을 추출하면서 유효성(느슨한 검사) 검사 진행
        // accessToken을 통해 refreshToken을 간접적으로 검사하기 때문에 엑세스 토큰은 느슨하게, 리프레시 토큰은 엄격하게 검사
        String username = accessTokenService.validateAccessTokenAndExtractUsername_leniently(tokenStr);
        log.info("username : " + username);

        filterChain.doFilter(request, response);
      } catch (ExpiredJwtException e) {
        // 리프레시 토큰의 만료에 대한 검사는 메서드 내부에서 알아서 진행함. 이건 엑세스 토큰 만료에 대한 것
        // 엑세스 토큰 만료 시 재발행. 그 외의 유효성 검사는 모두 진행함
        String username = e.getClaims().getSubject();
        String jsonStr = accessTokenService.issueAccessToken(username);

        // 그리고 토큰 값을 reponse에 담아줌
        response.getWriter().println(jsonStr);
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
      }
    } catch (RefreshTokenException e) {
      e.sendResponseError(response);
    } catch (Exception e) {
      log.error("리프레시 토큰 처리 중 오류 발생: {}", e.getMessage());
      response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      response.getWriter().println("토큰 처리 실패");
    }
  }

}
