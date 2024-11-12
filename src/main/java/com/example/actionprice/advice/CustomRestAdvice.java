package com.example.actionprice.advice;

import com.example.actionprice.exception.*;

import com.example.actionprice.exception.AccessTokenException.TOKEN_ERROR;
import com.example.actionprice.exception.RefreshTokenException.ErrorCase;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

import javax.naming.AuthenticationException;
import lombok.extern.log4j.Log4j2;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 모든 컨트롤러에서 발생하는 예외를 처리함
 * @author : 연상훈
 * @created : 2024-10-06 오후 6:46
 * @updated 2024-11-11 오전 5:14 : 토큰 관련 에러 등록, 금지된 요청에 대한 에러 등록
 * @info RestControllerAdvice는 ControllerAdvice를 기본적으로 상속하기 때문에 RestController뿐만 아니라 Controller도 처리 가능. 선언만 하면 spring이 알아서 가져다 사용함.
 */
@RestControllerAdvice
@Log4j2
public class CustomRestAdvice {

  /**
   * BindException(컨트롤러에서의 유효성 검사)를 커스텀하는 handler
   * @author : 연상훈
   * @created : 2024-10-12 오전 12:54
   * @updated : 2024-10-12 오전 12:54
   */
  @ExceptionHandler(BindException.class)
  public ResponseEntity<Map<String, String>> handlerBindException(BindException e) {

    log.error(e);

    Map<String, String> errorMap = new HashMap<>();

    if(e.hasErrors()){
      BindingResult bindingResult = e.getBindingResult();

      bindingResult.getFieldErrors()
          .forEach(fieldError -> errorMap.put(fieldError.getField(), fieldError.getCode()));
    }

    return ResponseEntity.unprocessableEntity()
        .body(errorMap);
  }

  @ExceptionHandler(DataIntegrityViolationException.class)
  public ResponseEntity<Map<String, String>> handlerFKException(Exception e) {

    log.error(e);

    Map<String, String> errorMap = new HashMap<>();

    errorMap.put("time", "" + System.currentTimeMillis());
    errorMap.put("message", "constraint fails");

    return ResponseEntity.status(HttpStatus.CONFLICT)
        .body(errorMap);
  }

  // 존재하지 않는 값을 가져올 때와 삭제할 때
  @ExceptionHandler({NoSuchElementException.class, EmptyResultDataAccessException.class})
  public ResponseEntity<Map<String, String>> handlerNoSuchElementException(Exception e) {

    log.error(e);

    Map<String, String> errorMap = new HashMap<>();

    errorMap.put("time", "" + System.currentTimeMillis());
    errorMap.put("message", "No Such element Exception");

    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(errorMap);
  }

  // 인증코드 발송에 실패했을 때
  @ExceptionHandler(InvalidEmailAddressException.class)
  public ResponseEntity<String> handlerInvalidEmailAddressException(InvalidEmailAddressException e) {
    log.error(e);
    return ResponseEntity.unprocessableEntity()
        .body(e.getMessage());
  }

  // 부정한 방법으로 존재하지 않는 user 조회 시 발생하는 에러
  @ExceptionHandler(UserNotFoundException.class)
  public ResponseEntity<String> handlerUserNotFoundException(UserNotFoundException e) {
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
        .body(e.getMessage());
  }

  // 부정한 방법으로 존재하지 않는 post 조회 시 발생하는 에러
  @ExceptionHandler(PostNotFoundException.class)
  public ResponseEntity<String> handlerPostNotFoundException(PostNotFoundException e) {
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
        .body(e.getMessage());
  }

  // 부정한 방법으로 존재하지 않는 댓글 조회 시 발생하는 에러
  @ExceptionHandler(CommentNotFoundException.class)
  public ResponseEntity<String> handlerCommentNotFoundException(CommentNotFoundException e) {
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
        .body(e.getMessage());
  }

  // 존재하지 않는 카테고리를 부정한 방법으로 조회 시
  @ExceptionHandler(InvalidCategoryException.class)
  public ResponseEntity<String> handlerInvalidCategoryException(InvalidCategoryException e) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(e.getMessage());
  }

  // 부정 접근(권한 없는 접근) 시
  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<String> handlerAccessDeniedException(AccessDeniedException e) {
    return ResponseEntity.status(HttpStatus.FORBIDDEN)
        .body(e.getMessage());
  }

  // 기타 보안 인증에 걸렸을 때
  // 어지간한 건 메서드에 붙은 로직 선에서 알아서 처리됨
  @ExceptionHandler({AuthenticationException.class, InsufficientAuthenticationException.class})
  public ResponseEntity<String> handlerAuthenticationError(Exception e) {
    return ResponseEntity.status(HttpStatus.FORBIDDEN)
        .body(e.getMessage());
  }

  // 더이상 즐겨찾기를 추가할 수 없을 때
  @ExceptionHandler(TooManyFavoritesException.class)
  public ResponseEntity<String> handlerTooMuchFavoritesError(Exception e) {
    return ResponseEntity.status(HttpStatus.CONFLICT)
        .body(e.getMessage());
  }


  // 엑세스 토큰 에러
  @ExceptionHandler(AccessTokenException.class)
  public ResponseEntity<String> handlerAccessTokenException(AccessTokenException e) {
    TOKEN_ERROR token_error = e.getToken_error();
    return ResponseEntity.status(token_error.getStatus())
        .body(token_error.getMessage());
  }

  // 리프레시 토큰 에러
  @ExceptionHandler(RefreshTokenException.class)
  public ResponseEntity<String> handlerRefreshTokenException(RefreshTokenException e) {
    ErrorCase errorCase = e.getErrorCase();
    return ResponseEntity.status(errorCase.getStatus())
        .body(errorCase.getMessage());
  }
}