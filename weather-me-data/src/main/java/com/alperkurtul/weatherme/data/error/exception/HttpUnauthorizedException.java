package com.alperkurtul.weatherme.data.error.exception;

import com.alperkurtul.weatherme.data.error.ErrorContants;
import com.sun.istack.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class HttpUnauthorizedException extends RuntimeException {
    public HttpUnauthorizedException(@NotNull Exception e) {
        super(String.valueOf(ErrorContants.REASON_CODE_HTTP_UNAUTHORIZED), new Throwable(e.getMessage()));
    }
}
