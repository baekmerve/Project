package com.example.actionprice.exception;

import com.google.gson.Gson;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

/**
 * 엑세스 토큰 에러
 * @author : 연상훈
 * @created : 2024-10-06 오후 2:57
 * @updated 2024-10-17 오후 7:42 : 상태코드 숫자 수정
 * @updated 2024-10-19 오후 5:17 : 블랙리스트 기능 구현을 위해 TOKEN_ERROR에 BLOCKED 추가
 */
public class AccessTokenException extends RuntimeException {

  private final TOKEN_ERROR token_error;

  public enum TOKEN_ERROR {
    UNACCEPT(HttpStatus.UNAUTHORIZED, "Token is null or too short"),
    BADTYPE(HttpStatus.UNAUTHORIZED, "Token type must be Bearer"),
    EXPIRED(HttpStatus.UNAUTHORIZED, "Expired Token"),
    MALFORM(HttpStatus.FORBIDDEN, "Malformed Token"),
    UNEXPECTED(HttpStatus.FORBIDDEN, "Unexpected claim"),
    BADSIGN(HttpStatus.FORBIDDEN, "Bad Signature Token");

    private HttpStatus status;
    private String msg;

    TOKEN_ERROR(HttpStatus status, String msg) {
      this.status = status;
      this.msg = msg;
    }

    public HttpStatus getStatus() {
      return status;
    }

    public String getMsg() {
      return msg;
    }
  }

  public AccessTokenException(TOKEN_ERROR error) {
    super(error.name());
    this.token_error = error;
  }

  public void sendResponseError(HttpServletResponse response) {

    response.setStatus(token_error.getStatus().value());
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);

    Gson gson = new Gson();
    String responseStr = gson.toJson(
        Map.of("msg", token_error.getMsg(),
            "time", new Date())
    );

    try{
      response.getWriter().println(responseStr);
    }
    catch(Exception e){
      throw new RuntimeException(e);
    }
  }

}
