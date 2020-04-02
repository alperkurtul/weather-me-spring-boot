package com.alperkurtul.weatherme.error.exception;

import com.alperkurtul.weatherme.error.ErrorContants;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class HttpUnauthorizedException extends RuntimeException {
    public HttpUnauthorizedException(Exception e) {
        super(String.valueOf(ErrorContants.REASON_CODE_HTTP_UNAUTHORIZED), ( e == null ? null : new Throwable(e.getMessage()) ) );
    }
}
