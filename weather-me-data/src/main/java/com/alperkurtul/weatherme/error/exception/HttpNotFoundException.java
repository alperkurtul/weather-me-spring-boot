package com.alperkurtul.weatherme.error.exception;

import com.alperkurtul.weatherme.error.ErrorContants;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class HttpNotFoundException extends RuntimeException {
    public HttpNotFoundException(Exception e) {
        super(String.valueOf(ErrorContants.REASON_CODE_HTTP_NOT_FOUND), ( e == null ? null : new Throwable(e.getMessage()) ) );
    }
}
