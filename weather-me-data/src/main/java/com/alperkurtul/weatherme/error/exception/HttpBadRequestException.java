package com.alperkurtul.weatherme.error.exception;

import com.alperkurtul.weatherme.error.ErrorContants;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class HttpBadRequestException extends RuntimeException {
    public HttpBadRequestException(Exception e) {
        super(String.valueOf(ErrorContants.REASON_CODE_HTTP_BAD_REQUEST), ( e == null ? null : new Throwable(e.getMessage()) ) );
    }
}
