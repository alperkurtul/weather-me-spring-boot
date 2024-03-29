package com.alperkurtul.weatherme.error.exception;

import com.alperkurtul.weatherme.error.ErrorContants;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class HttpForbiddenExceptionN403 extends RuntimeException {
    public HttpForbiddenExceptionN403(Exception e) {
        super(String.valueOf(ErrorContants.REASON_CODE_HTTP_FORBIDDEN), ( e == null ? null : new Throwable(e.getMessage()) ) );
    }
}
