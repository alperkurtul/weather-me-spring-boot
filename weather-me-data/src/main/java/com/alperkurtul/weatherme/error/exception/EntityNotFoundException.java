package com.alperkurtul.weatherme.error.exception;

import com.sun.istack.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class EntityNotFoundException extends RuntimeException {
    public EntityNotFoundException(@NotNull Exception e, int reasonCode) {
        super(String.valueOf(reasonCode), new Throwable(e.getMessage()));
    }
}
