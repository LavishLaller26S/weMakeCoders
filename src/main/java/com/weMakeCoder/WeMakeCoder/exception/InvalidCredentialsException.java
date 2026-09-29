package com.weMakeCoder.WeMakeCoder.exception;

import org.springframework.http.HttpStatus;

public class InvalidCredentialsException extends AppException{
    public InvalidCredentialsException(String message){
        super(message, HttpStatus.FORBIDDEN,"INVALID_CREDENTIALS");
    }
}
