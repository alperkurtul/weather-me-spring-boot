package com.alperkurtul.weatherme.error.exception;

import com.sun.istack.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class MandatoryValidationFailedException extends RuntimeException {
    public MandatoryValidationFailedException(@NotNull Exception e, int reasonCode) {
        super(String.valueOf(reasonCode), new Throwable(e.getMessage()));
    }
}
