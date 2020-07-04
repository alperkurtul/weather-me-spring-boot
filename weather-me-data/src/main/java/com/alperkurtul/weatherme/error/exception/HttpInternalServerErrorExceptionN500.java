package com.alperkurtul.weatherme.error.exception;

import com.alperkurtul.weatherme.error.ErrorContants;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class HttpInternalServerErrorExceptionN500 extends RuntimeException {
    public HttpInternalServerErrorExceptionN500(Exception e) {
        super(String.valueOf(ErrorContants.REASON_CODE_HTTP_INTERNAL_SERVER_ERROR), ( e == null ? null : new Throwable(e.getMessage()) ) );
    }
}
