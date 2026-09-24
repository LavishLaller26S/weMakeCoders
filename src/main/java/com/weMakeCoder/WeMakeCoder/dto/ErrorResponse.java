package com.weMakeCoder.WeMakeCoder.dto;

import org.springframework.http.HttpStatus;

public record ErrorResponse(
        String message,
        int status,
        String code
) {
}
