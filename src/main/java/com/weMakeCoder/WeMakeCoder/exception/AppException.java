package com.weMakeCoder.WeMakeCoder.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class AppException extends RuntimeException{
    private final String code;
    private final HttpStatus status;
    public AppException(String message, HttpStatus status,String code){
        super(message);
        this.status=status;
        this.code=code;
    }
}
