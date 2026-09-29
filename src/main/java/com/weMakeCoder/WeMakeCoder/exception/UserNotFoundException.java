package com.weMakeCoder.WeMakeCoder.exception;

import org.springframework.http.HttpStatus;

public class UserNotFoundException extends AppException{
    public UserNotFoundException(String message){
        super(message,HttpStatus.NOT_FOUND,"USER_NOT_FOUND");
    }
}
