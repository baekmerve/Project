package com.example.actionprice.security.filter;

import com.example.actionprice.exception.RefreshTokenException;
import com.example.actionprice.security.jwt.accessToken.AccessTokenService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
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
 * @updated : 2024-10-06 오후 3:05
 * @updated 2024-10-19 오후 5:34 : jwtUtil을 refreshTokenService로 교체하면서 로직 간략화
 */
@Log4j2
@RequiredArgsConstructor
public class RefreshTokenFilter extends OncePerRequestFilter {

  private final AccessTokenService accessTokenService;

  /**
   * 필터링 로직
   * @author : 연상훈
   * @created : 2024-10-06 오후 3:10
   * @updated : 2024-10-06 오후 3:10
   * @see : 책대로 함
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

    try {
      String tokenStr = accessTokenService.extractTokenInHeaderStr(headerStr);

      try {
        String username = accessTokenService.validateAccessTokenAndExtractUsername_leniently(tokenStr);
        log.info("username : " + username);

        filterChain.doFilter(request, response);
      } catch (ExpiredJwtException e) {
        String username = e.getClaims().getSubject();
        String jsonStr = accessTokenService.issueAccessToken(username);

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
