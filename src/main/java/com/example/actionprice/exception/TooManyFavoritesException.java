package com.example.actionprice.exception;

// favorite 갯수가 너무 많을 때(10개 이상)
public class TooManyFavoritesException extends RuntimeException {
  public TooManyFavoritesException(String message) {
    super(message);
  }
}
