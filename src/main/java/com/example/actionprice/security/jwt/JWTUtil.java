package com.example.actionprice.security.jwt;

import com.example.actionprice.user.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

// TODO exception 처리를 구체화할 필요가 있음
/**
 * @author : 연상훈
 * @created : 2024-10-06 오후 2:12
 * @info : jwt 토큰을 위한 설정
 */
@Log4j2
@Component
public class JWTUtil {

  @Value("${jwt.secretKey}")
  private String secretKey;

  private final SignatureAlgorithm SIGNATURE_ALGORITHM = SignatureAlgorithm.HS256;

  /**
   * 토큰 생성
   * @param user
   * @param time 토큰의 유효시간(분).
   * @author : 연상훈
   * @created : 2024-10-06 오후 2:12
   * @info 일단위로 하고 싶으면 60*24*days 또는 plusDays(time).
   */
  public String generateToken(User user, int time) {

    // 헤더
    Map<String, Object> headers = new HashMap<>();
    headers.put("typ", "JWT");
    headers.put("alg", SIGNATURE_ALGORITHM.getValue());

    String jwtStr = Jwts.builder()
        .setHeader(headers)
        .setSubject(user.getUsername())
        .setClaims(Map.of("username", user.getUsername(), "role", user.getAuthorities()))
        .setIssuedAt(Date.from(ZonedDateTime.now().toInstant()))
        .setExpiration(Date.from(ZonedDateTime.now().plusMinutes(time).toInstant()))
        .signWith(SIGNATURE_ALGORITHM, secretKey.getBytes(StandardCharsets.UTF_8))
        .compact();

    return jwtStr;
  }

  /**
   * 토큰 검증
   * @param token : 토큰 내용 [String]
   * @author : 연상훈
   * @created : 2024-10-06 오후 2:35
   */
  public Map<String, Object> validateToken(String token) throws JwtException {
    log.info("token : " + token);
    Map<String, Object> claims = Jwts.parserBuilder()
        .setSigningKey(secretKey.getBytes(StandardCharsets.UTF_8)) // 설정한 secret key
        .build() // 아무래도 parseClaimsJws가 이미 객체로서 존재하는 Jwts에 설정하는 거라서 중간에 빌드를 하는 듯?
        .parseClaimsJws(token) // 파싱 및 검증, 실패 시 에러
        .getBody(); // jwt(json web token)인 만큼 결국 json 형태라서 중요한 내용은 body에 있음

    return claims;
  }

  /**
   * 토큰 내부에 있는 유저네임 읽기
   * @param token : 토큰 내용 [String]
   * @author : 연상훈
   * @created : 2024-10-06 오후 2:35
   */
  public String getUsernameFromToken(String token) {
    try {
      Claims claims = Jwts.parserBuilder()
          .setSigningKey(secretKey.getBytes(StandardCharsets.UTF_8))
          .build()
          .parseClaimsJws(token)
          .getBody();
      return claims.getSubject();
    }
    catch (JwtException e) {
      log.error("해당 토큰 파싱에 실패했습니다", e);
      return null;
    }
  }

}
