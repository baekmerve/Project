package com.example.actionprice.security.jwt;

import com.example.actionprice.user.User;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

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

    Map<String, Object> claims = new HashMap<>();
    claims.put("username", user.getUsername());
    claims.put("role", user.getAuthorities());

    // setClaims가 무조건 setSubject보다 먼저 와야 함
    return Jwts.builder()
        .setHeader(headers)
        .setClaims(claims)
        .setSubject(user.getUsername())
        .setIssuedAt(new Date(System.currentTimeMillis()))
        .setExpiration(new Date(System.currentTimeMillis() + 1000*60*time))
        .signWith(SIGNATURE_ALGORITHM, secretKey.getBytes(StandardCharsets.UTF_8))
        .compact();
  }

  /**
   * 토큰 검증 후 토큰 소유자의 username을 반환
   * @param token : 토큰 내용 [String]
   * @author : 연상훈
   * @created : 2024-10-06 오후 2:35
   */
  public String validateToken(String token) throws JwtException {
    return Jwts.parserBuilder()
        .setSigningKey(secretKey.getBytes(StandardCharsets.UTF_8)) // 설정한 secret key
        .setAllowedClockSkewSeconds(60) // 1분의 오차는 허용
        .build()
        .parseClaimsJws(token)
        .getBody()
        .getSubject();
  }

}