package com.example.actionprice.advice;

import com.example.actionprice.exception.CommentNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

import com.example.actionprice.exception.InvalidEmailAddressException;
import com.example.actionprice.exception.PostNotFoundException;
import com.example.actionprice.exception.UserNotFoundException;
import lombok.extern.log4j.Log4j2;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 모든 컨트롤러에서 발생하는 예외를 처리함
 * @author : 연상훈
 * @created : 2024-10-06 오후 6:46
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
  @ResponseStatus(HttpStatus.EXPECTATION_FAILED) // 상태코드 417 : 의도된 에러임을 의미한다고 함
  public ResponseEntity<Map<String, String>> handlerBindException(BindException e) {

    log.error(e);

    Map<String, String> errorMap = new HashMap<>();

    if(e.hasErrors()){
      BindingResult bindingResult = e.getBindingResult();

      bindingResult.getFieldErrors()
          .forEach(fieldError -> {errorMap.put(fieldError.getField(), fieldError.getCode());});
    }

    return ResponseEntity.badRequest().body(errorMap);
  }

  @ExceptionHandler(DataIntegrityViolationException.class)
  @ResponseStatus(HttpStatus.EXPECTATION_FAILED)
  public ResponseEntity<Map<String, String>> handlerFKException(Exception e) {

    log.error(e);

    Map<String, String> errorMap = new HashMap<>();

    errorMap.put("time", "" + System.currentTimeMillis());
    errorMap.put("message", "constraint fails");

    return ResponseEntity.badRequest().body(errorMap);

  }

  // 존재하지 않는 값을 가져올 때와 삭제할 때
  @ExceptionHandler({NoSuchElementException.class, EmptyResultDataAccessException.class})
  @ResponseStatus(HttpStatus.EXPECTATION_FAILED)
  public ResponseEntity<Map<String, String>> handlerNoSuchElementException(Exception e) {

    log.error(e);

    Map<String, String> errorMap = new HashMap<>();

    errorMap.put("time", "" + System.currentTimeMillis());
    errorMap.put("message", "No Such element Exception");

    return ResponseEntity.badRequest().body(errorMap);
  }

  // 인증코드 발송에 실패했을 때
  @ExceptionHandler(InvalidEmailAddressException.class)
  @ResponseStatus(HttpStatus.EXPECTATION_FAILED)
  public ResponseEntity<String> handlerInvalidEmailAddressException(InvalidEmailAddressException e) {
    log.error(e);
    return ResponseEntity.badRequest().body(e.getMessage());
  }

  //user 가 없을 시
  @ExceptionHandler(UserNotFoundException.class)
  @ResponseStatus(HttpStatus.EXPECTATION_FAILED)
  public ResponseEntity<String> handlerUserNotFoundException(UserNotFoundException e) {
    return ResponseEntity.badRequest().body(e.getMessage());
  }

  //post 가 없을 시
  @ExceptionHandler(PostNotFoundException.class)
  @ResponseStatus(HttpStatus.EXPECTATION_FAILED)
  public ResponseEntity<String> handlerPostNotFoundException(PostNotFoundException e) {
    return ResponseEntity.badRequest().body(e.getMessage());
  }

  @ExceptionHandler(CommentNotFoundException.class)
  @ResponseStatus(HttpStatus.EXPECTATION_FAILED)
  public ResponseEntity<String> handlerCommentNotFoundException(CommentNotFoundException e) {
    return ResponseEntity.badRequest().body(e.getMessage());
  }

  // 부정 접근 시(post/comment 작성자가 아닌 사람이 삭제하려고 시도할 경우 등)
  @ExceptionHandler(IllegalAccessError.class)
  @ResponseStatus(HttpStatus.EXPECTATION_FAILED)
  public ResponseEntity<String> handlerIllegalAccessException(IllegalAccessError e) {
    return ResponseEntity.badRequest().body(e.getMessage());
  }

}