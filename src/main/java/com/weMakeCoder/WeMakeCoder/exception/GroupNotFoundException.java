package com.weMakeCoder.WeMakeCoder.exception;

import org.springframework.http.HttpStatus;

public class GroupNotFoundException extends AppException{
    public GroupNotFoundException(String message){
        super(message, HttpStatus.NOT_FOUND,"GROUP_NOT_FOUND");
    }
}
