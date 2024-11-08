package com.example.actionprice.exception;

/**
 * 부정한 방법으로 존재하지 않는 user 조회 시 발생하는 에러
 * @author 연상훈
 * @created 2024-11-08 오전 11:02
 * @info
 */
public class UserNotFoundException extends RuntimeException{
  public UserNotFoundException(String message){
    super(message);
  }
}
