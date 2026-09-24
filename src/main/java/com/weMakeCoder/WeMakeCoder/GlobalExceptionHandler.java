package com.weMakeCoder.WeMakeCoder;

import com.weMakeCoder.WeMakeCoder.exception.AppException;
import org.springframework.http.ResponseEntity;

import com.weMakeCoder.WeMakeCoder.dto.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AppException.class)
    public ResponseEntity<ErrorResponse> handleException(AppException ex){
        ErrorResponse body = new ErrorResponse(
                ex.getMessage(), ex.getStatus().value(), ex.getCode()
        );

        return ResponseEntity.status(ex.getStatus()).body(body);
    }
}
