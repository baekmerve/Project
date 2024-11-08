package com.example.actionprice.exception;

/**
 * 존재하지 않는 카테고리를 부정한 방법으로 조회 시
 * @author 연상훈
 * @created 2024-11-08 오전 11:03
 */
public class InvalidCategoryException extends RuntimeException {
    public InvalidCategoryException(String message) {super(message);}
}
