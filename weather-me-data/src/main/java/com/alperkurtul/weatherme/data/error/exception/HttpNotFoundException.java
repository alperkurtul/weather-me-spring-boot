package com.alperkurtul.weatherme.data.error.exception;

import com.alperkurtul.weatherme.data.error.ErrorContants;
import com.sun.istack.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class HttpNotFoundException extends RuntimeException {
    public HttpNotFoundException(@NotNull Exception e) {
        super(String.valueOf(ErrorContants.REASON_CODE_HTTP_NOT_FOUND), new Throwable(e.getMessage()));
    }
}
