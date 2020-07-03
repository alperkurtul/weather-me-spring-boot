package com.alperkurtul.weatherme.error.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class EntityNotFoundExceptionN10 extends Exception {
    public EntityNotFoundExceptionN10(Exception e, int reasonCode) {
        super(String.valueOf(reasonCode), ( e == null ? null : new Throwable(e.getMessage()) ) );
    }
}
