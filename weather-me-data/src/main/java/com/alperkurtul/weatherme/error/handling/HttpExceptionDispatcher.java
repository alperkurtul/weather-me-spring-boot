package com.alperkurtul.weatherme.error.handling;

import com.alperkurtul.weatherme.error.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

public class HttpExceptionDispatcher {

    public void dispatchToException(Exception e) {

        HttpStatus httpStatus = ((HttpClientErrorException) e).getStatusCode();

        if ( httpStatus.equals(HttpStatus.NOT_FOUND)) {
            throw new HttpNotFoundExceptionN404(e);
        } else if ( httpStatus.equals(HttpStatus.BAD_REQUEST)) {
            throw new HttpBadRequestExceptionN400(e);
        } else if ( httpStatus.equals(HttpStatus.FORBIDDEN)) {
            throw new HttpForbiddenExceptionN403(e);
        } else if ( httpStatus.equals(HttpStatus.INTERNAL_SERVER_ERROR)) {
            throw new HttpInternalServerErrorExceptionN500(e);
        } else if ( httpStatus.equals(HttpStatus.UNAUTHORIZED)) {
            throw new HttpUnauthorizedExceptionN401(e);
        } else {
            throw new HttpBadRequestExceptionN400(e);
        }

    }

}
