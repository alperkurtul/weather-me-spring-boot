package com.alperkurtul.weatherme.error.handling;

import com.alperkurtul.weatherme.data.error.exception.*;
import com.alperkurtul.weatherme.error.exception.*;
import com.sun.istack.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

public class HttpExceptionDispatcher {

    public void dispatchToException(@NotNull Exception e) {

        HttpStatus httpStatus = ((HttpClientErrorException) e).getStatusCode();

        if ( httpStatus.equals(HttpStatus.NOT_FOUND)) {
            throw new HttpNotFoundException(e);
        } else if ( httpStatus.equals(HttpStatus.BAD_REQUEST)) {
            throw new HttpBadRequestException(e);
        } else if ( httpStatus.equals(HttpStatus.FORBIDDEN)) {
            throw new HttpForbiddenException(e);
        } else if ( httpStatus.equals(HttpStatus.INTERNAL_SERVER_ERROR)) {
            throw new HttpInternalServerErrorException(e);
        } else if ( httpStatus.equals(HttpStatus.UNAUTHORIZED)) {
            throw new HttpUnauthorizedException(e);
        } else {
            throw new HttpBadRequestException(e);
        }

    }

}
