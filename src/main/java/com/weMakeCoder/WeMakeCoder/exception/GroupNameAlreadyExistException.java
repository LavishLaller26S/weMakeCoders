package com.weMakeCoder.WeMakeCoder.exception;

import org.springframework.http.HttpStatus;

public class GroupNameAlreadyExistException extends AppException {
    public GroupNameAlreadyExistException(String message) {
        super(message, HttpStatus.CONFLICT,"GROUP_NAME_ALREADY_EXIST");
    }
}
