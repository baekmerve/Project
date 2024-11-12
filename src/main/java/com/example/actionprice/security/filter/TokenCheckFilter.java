package com.example.actionprice.security.filter;

import com.example.actionprice.exception.AccessTokenException;
import com.example.actionprice.exception.RefreshTokenException;
import com.example.actionprice.security.CustomUserDetailService;
import com.example.actionprice.security.jwt.accessToken.AccessTokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * 토큰을 검사하는 필터
 * @value userDetailService
 * @value accessTokenService
 * @author : 연상훈
 * @created : 2024-10-06 오후 2:43
 * @updated 2024-10-17 오후 7:23 : 토큰체크 경로 수정
 * @updated 2024-10-19 오후 5:33 : jwtUtil을 refreshTokenService로 교체
 * @updated 2024-11-08 오후 12:39 : accessTokenService와 refreshTokenService를 분리하여 관심사 분리 및 로직 구체화
 * @info 거의 모든 요청에 대해 인증 정보를 제공하는 필터이기 때문에 인증 정보 생성을 위해 userDetailService 사용
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

    // 정상적인 형태의 토큰이 존재한다면
    try{
      // 토큰에서 토큰의 내용을 추출함
      String tokenStr = accessTokenService.extractTokenInHeaderStr(headerStr);

      // 토큰 내용에서 username을 추출하면서 유효성(엄격한 검사) 검사 진행
      // 만약 이것저것 변조한 토큰이었다면 여기서 걸림
      String username = accessTokenService.validateAccessTokenAndExtractUsername_strictly(tokenStr);
      log.info("username : " + username);

      // 인증 정보 생성
      UserDetails userDetails = userDetailService.loadUserByUsername(username);
      log.info("UserDetails : " + userDetails);

      UsernamePasswordAuthenticationToken authenticationToken =
          new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
      authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request)); // 추가됨
      log.info("Authentication Token : " + authenticationToken);

      // 인증 정보를 저장
      SecurityContextHolder.getContext().setAuthentication(authenticationToken);
      log.info("Security Context : " + SecurityContextHolder.getContext());
    } catch(RefreshTokenException e){
      request.setAttribute("filter.exception", e);
    } catch(AccessTokenException e){
      request.setAttribute("filter.exception", e);
    } catch (Exception e) {
      log.error("TokenCheckFilter 처리 중 예외 발생: {}", e.getMessage());
      request.setAttribute("filter.exception", e);
    }

    // 뭐가 어찌됐든 필터는 이어줌. 그래야 인터셉터에서 넘겨 받아서 어드바이스로 처리가 됨
    filterChain.doFilter(request, response);
  }

}
