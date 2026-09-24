package com.weMakeCoder.WeMakeCoder.exception;

import org.springframework.http.HttpStatus;

public class UserNotExistsException extends AppException{
    public UserNotExistsException(String message){
        super(message,HttpStatus.NOT_FOUND,"USER_NOT_FOUND");
    }
}
