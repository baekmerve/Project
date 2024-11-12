package com.example.actionprice.exception;

import org.springframework.http.HttpStatus;

/**
 * 리프레시 토큰 에러
 * @author : 연상훈
 * @created : 2024-10-06 오후 2:58
 * @updated 2024-10-19 오후 5:17 : 블랙리스트 기능 구현을 위해 TOKEN_ERROR에 BLOCKED 추가
 * @updated 2024-11-11 오전 5:16 : 불필요한 응답 방식 제거
 */
public class RefreshTokenException extends RuntimeException {

  private ErrorCase errorCase;

  public enum ErrorCase {
    NO_ACCESS(HttpStatus.UNAUTHORIZED, "No access with refresh token"),
    NO_REFRESH(HttpStatus.BAD_REQUEST, "No refresh token"),
    UNEXPECTED_REFRESH(HttpStatus.FORBIDDEN, "Unexpected refresh token"),
    BADSIGN_REFRESH(HttpStatus.FORBIDDEN, "Bad sign refresh token"),
    BLOCKED_REFRESH(HttpStatus.FORBIDDEN, "Blocked refresh token"),
    EXPIRED_REFRESH(HttpStatus.UNAUTHORIZED, "Expired token"),
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

  public ErrorCase getErrorCase() {
    return errorCase;
  }

}
