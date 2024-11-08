package com.example.actionprice.exception;

// 엑셀 파일로 다운 받을 때, 지정한 조건에 맞는 데이터가 없을 시
public class TransactionDataNotFoundException extends RuntimeException {
    public TransactionDataNotFoundException(String message) {super(message);}
}
