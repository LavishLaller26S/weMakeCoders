package com.weMakeCoder.WeMakeCoder.exception;

import org.springframework.http.HttpStatus;

public class UrlAlreadyExistException extends AppException {
    public UrlAlreadyExistException(String message) {
        super(message, HttpStatus.CONFLICT,"URL_ALREADY_EXIST");
    }
}
