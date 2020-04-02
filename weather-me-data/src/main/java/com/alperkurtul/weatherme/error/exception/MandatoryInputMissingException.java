package com.alperkurtul.weatherme.error.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class MandatoryInputMissingException extends RuntimeException {
    public MandatoryInputMissingException(Exception e, int reasonCode) {
        super(String.valueOf(reasonCode), ( e == null ? null : new Throwable(e.getMessage()) ) );
    }
}
