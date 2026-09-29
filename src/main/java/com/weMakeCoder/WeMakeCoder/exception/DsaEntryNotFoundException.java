package com.weMakeCoder.WeMakeCoder.exception;

import org.springframework.http.HttpStatus;

public class DsaEntryNotFoundException extends AppException{
    public DsaEntryNotFoundException(String message){
        super(message, HttpStatus.NOT_FOUND,"DSA_ENTRY_NOT_FOUND");
    }
}
