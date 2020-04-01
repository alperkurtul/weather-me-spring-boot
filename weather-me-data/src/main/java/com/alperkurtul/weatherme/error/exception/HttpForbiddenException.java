package com.alperkurtul.weatherme.error.exception;

import com.alperkurtul.weatherme.error.ErrorContants;
import com.sun.istack.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class HttpForbiddenException extends RuntimeException {
    public HttpForbiddenException(@NotNull Exception e) {
        super(String.valueOf(ErrorContants.REASON_CODE_HTTP_FORBIDDEN), new Throwable(e.getMessage()));
    }
}
