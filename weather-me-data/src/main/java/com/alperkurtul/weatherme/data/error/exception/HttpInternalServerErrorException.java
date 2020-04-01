package com.alperkurtul.weatherme.data.error.exception;

import com.alperkurtul.weatherme.data.error.ErrorContants;
import com.sun.istack.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class HttpInternalServerErrorException extends RuntimeException {
    public HttpInternalServerErrorException(@NotNull Exception e) {
        super(String.valueOf(ErrorContants.REASON_CODE_HTTP_INTERNAL_SERVER_ERROR), new Throwable(e.getMessage()));
    }
}
