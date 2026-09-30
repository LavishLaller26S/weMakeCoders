package com.weMakeCoder.WeMakeCoder.exception;

import org.springframework.http.HttpStatus;

public class UserAlreadyInTheGroupException extends AppException{
    public UserAlreadyInTheGroupException(String message){
        super(message, HttpStatus.CONFLICT,"USER_ALREADY_IN_GROUP");
    }
}
