package com.example.actionprice.security.filter;

import com.example.actionprice.exception.AccessTokenException;
import com.example.actionprice.security.CustomUserDetailService;
import com.example.actionprice.security.jwt.accessToken.AccessTokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * 토큰을 검사하는 필터
 * @value tokenCheckPath : 토큰 검사가 필요한 url 경로들의 배열.
 * @value userDetailService
 * @value refreshTokenService
 * @author : 연상훈
 * @created : 2024-10-06 오후 2:43
 * @updated 2024-10-17 오후 7:23 : 토큰체크 경로 수정
 * @updated 2024-10-19 오후 5:33 : jwtUtil을 refreshTokenService로 교체
 */
@Log4j2
@RequiredArgsConstructor
public class TokenCheckFilter extends OncePerRequestFilter {

  private final CustomUserDetailService userDetailService;
  private final AccessTokenService accessTokenService;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
    log.info("토큰 체크 필터 실행");

    String headerStr = request.getHeader("Authorization");
    log.info("url : {} | headerStr : {}", request.getRequestURI(), headerStr);

    // 토큰이 없거나 Bearer로 시작하지 않으면
    if(headerStr == null || headerStr.contains("undefined") || !headerStr.startsWith("Bearer ")) {
      log.info("익명 사용자로 처리");
      // 익명 사용자로 처리
      filterChain.doFilter(request, response);
      return;
    }

    try{
      String tokenStr = accessTokenService.extractTokenInHeaderStr(headerStr);
      String username = accessTokenService.validateAccessTokenAndExtractUsername_strictly(tokenStr);
      log.info("username : " + username);

      UserDetails userDetails = userDetailService.loadUserByUsername(username);
      log.info("UserDetails : " + userDetails);

      UsernamePasswordAuthenticationToken authenticationToken =
          new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
      authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request)); // 추가됨
      log.info("Authentication Token : " + authenticationToken);

      SecurityContextHolder.getContext().setAuthentication(authenticationToken);
      log.info("Security Context : " + SecurityContextHolder.getContext());

    } catch(AccessTokenException e){
      e.sendResponseError(response);
      return;
    } catch (Exception e) {
      log.error("TokenCheckFilter 처리 중 예외 발생: {}", e.getMessage());
      response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      response.getWriter().println("인증 처리 실패");
      return;
    }

    filterChain.doFilter(request, response);
  }

}
