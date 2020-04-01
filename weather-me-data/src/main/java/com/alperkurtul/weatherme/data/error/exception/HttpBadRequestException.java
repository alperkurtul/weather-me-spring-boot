package com.alperkurtul.weatherme.data.error.exception;

import com.alperkurtul.weatherme.data.error.ErrorContants;
import com.sun.istack.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.client.HttpClientErrorException;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class HttpBadRequestException extends RuntimeException {
    public HttpBadRequestException(@NotNull Exception e) {
        super(String.valueOf(ErrorContants.REASON_CODE_HTTP_BAD_REQUEST), new Throwable(e.getMessage()));
    }
}
