package com.example.actionprice.exception;

import com.google.gson.Gson;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

/**
 * 리프레시 토큰 에러
 * @author : 연상훈
 * @created : 2024-10-06 오후 2:58
 * @updated 2024-10-19 오후 5:17 : 블랙리스트 기능 구현을 위해 TOKEN_ERROR에 BLOCKED 추가
 */
public class RefreshTokenException extends RuntimeException {

  private ErrorCase errorCase;

  public enum ErrorCase {
    NO_ACCESS(HttpStatus.UNAUTHORIZED, "No access with refresh token"),
    NO_REFRESH(HttpStatus.BAD_REQUEST, "No refresh token"),
    UNEXPECTED_REFRESH(HttpStatus.FORBIDDEN, "Unexpected refresh token"),
    BADSIGN_REFRESH(HttpStatus.FORBIDDEN, "Bad sign refresh token"),
    BLOCKED_REFRESH(HttpStatus.FORBIDDEN, "Blocked refresh token"),
    EXPIRED_REFRESH(HttpStatus.UNAUTHORIZED, "Expired refresh token"),
    INVALID_REFRESH(HttpStatus.FORBIDDEN, "Invalid refresh token format");

    private final HttpStatus status;
    private final String message;

    ErrorCase(HttpStatus status, String message) {
      this.status = status;
      this.message = message;
    }

    public HttpStatus getStatus() {
      return status;
    }

    public String getMessage() {
      return message;
    }
  }

  public RefreshTokenException(ErrorCase errorCase) {
    super(errorCase.name());
    this.errorCase = errorCase;
  }

  public void sendResponseError(HttpServletResponse response){

    response.setStatus(HttpStatus.UNAUTHORIZED.value());
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);

    Gson gson = new Gson();

    String responseStr = gson.toJson(Map.of("msg", errorCase.name(), "time", new Date()));

    try{
      response.getWriter().print(responseStr);
    }
    catch(IOException e){
      throw new RuntimeException(e);
    }
  }

}
