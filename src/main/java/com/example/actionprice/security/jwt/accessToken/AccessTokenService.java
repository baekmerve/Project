package com.example.actionprice.security.jwt.accessToken;

public interface AccessTokenService {
  String issueJwt(String username);
  String issueAccessToken(String username);
  String extractTokenInHeaderStr(String headerStr);

  String validateAccessTokenAndExtractUsername_strictly(String accessToken);
  String validateAccessTokenAndExtractUsername_leniently(String accessToken);
}
