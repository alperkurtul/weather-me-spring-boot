package com.alperkurtul.weatherme.data.error.handling;

import com.alperkurtul.weatherme.data.error.ErrorContants;
import com.alperkurtul.weatherme.data.error.exception.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Date;

@ControllerAdvice
public class CustomizedExceptionHandler extends ResponseEntityExceptionHandler {

    @Autowired
    MessageAccessor messageAccessor;

    @ExceptionHandler(UnexpectedException.class)
    public final ResponseEntity<Object> handleUnexpectedException(UnexpectedException ex, WebRequest request) {

        ExceptionResponse exceptionResponse = createExceptionResponse(
                ErrorContants.RETURN_CODE_UNEXPECTED_EXCEPTION,
                Integer.valueOf(ex.getMessage()), ex, request);

        return new ResponseEntity(exceptionResponse, HttpStatus.BAD_REQUEST);

    }

    @ExceptionHandler(MandatoryInputMissingException.class)
    public final ResponseEntity<Object> handleMandatoryInputMissingException(MandatoryInputMissingException ex, WebRequest request) {

        ExceptionResponse exceptionResponse = createExceptionResponse(
                ErrorContants.RETURN_CODE_MANDATORY_INPUT_MISSING,
                Integer.valueOf(ex.getMessage()), ex, request);

        return new ResponseEntity(exceptionResponse, HttpStatus.BAD_REQUEST);

    }

    @ExceptionHandler(MandatoryValidationFailedException.class)
    public final ResponseEntity<Object> handleMandatoryValidationFailedException(MandatoryValidationFailedException ex, WebRequest request) {

        ExceptionResponse exceptionResponse = createExceptionResponse(
                ErrorContants.RETURN_CODE_MANDATORY_VALIDATION_FAILED,
                Integer.valueOf(ex.getMessage()), ex, request);

        return new ResponseEntity(exceptionResponse, HttpStatus.BAD_REQUEST);

    }

    @ExceptionHandler(EntityCreationFailedException.class)
    public final ResponseEntity<Object> handleEntityCreationFailedException(EntityCreationFailedException ex, WebRequest request) {

        ExceptionResponse exceptionResponse = createExceptionResponse(
                ErrorContants.RETURN_CODE_ENTITY_CREATION_FAILED,
                Integer.valueOf(ex.getMessage()), ex, request);

        return new ResponseEntity(exceptionResponse, HttpStatus.BAD_REQUEST);

    }

    @ExceptionHandler(EntityDeletionFailedException.class)
    public final ResponseEntity<Object> handleEntityDeletionFailedException(EntityDeletionFailedException ex, WebRequest request) {

        ExceptionResponse exceptionResponse = createExceptionResponse(
                ErrorContants.RETURN_CODE_ENTITY_DELETION_FAILED,
                Integer.valueOf(ex.getMessage()), ex, request);

        return new ResponseEntity(exceptionResponse, HttpStatus.BAD_REQUEST);

    }

    @ExceptionHandler(EntityNotFoundException.class)
    public final ResponseEntity<Object> handleEntityNotFoundException(EntityNotFoundException ex, WebRequest request) {

        ExceptionResponse exceptionResponse = createExceptionResponse(
                ErrorContants.RETURN_CODE_ENTITY_NOT_FOUND,
                Integer.valueOf(ex.getMessage()), ex, request);

        return new ResponseEntity(exceptionResponse, HttpStatus.BAD_REQUEST);

    }

    @ExceptionHandler(EntityUpdateFailedException.class)
    public final ResponseEntity<Object> handleEntityUpdateFailedException(EntityUpdateFailedException ex, WebRequest request) {

        ExceptionResponse exceptionResponse = createExceptionResponse(
                ErrorContants.RETURN_CODE_ENTITY_UPDATE_FAILED,
                Integer.valueOf(ex.getMessage()), ex, request);

        return new ResponseEntity(exceptionResponse, HttpStatus.BAD_REQUEST);

    }

    @ExceptionHandler(HttpNotFoundException.class)
    public final ResponseEntity<Object> handleHttpNotFoundException(HttpNotFoundException ex, WebRequest request) {

        ExceptionResponse exceptionResponse = createExceptionResponse(
                ErrorContants.RETURN_CODE_HTTP_NOT_FOUND,
                Integer.valueOf(ex.getMessage()), ex, request);

        return new ResponseEntity(exceptionResponse, HttpStatus.NOT_FOUND);

    }

    @ExceptionHandler(HttpBadRequestException.class)
    public final ResponseEntity<Object> handleHttpBadRequestException(HttpBadRequestException ex, WebRequest request) {

        ExceptionResponse exceptionResponse = createExceptionResponse(
                ErrorContants.RETURN_CODE_HTTP_BAD_REQUEST,
                Integer.valueOf(ex.getMessage()), ex, request);

        return new ResponseEntity(exceptionResponse, HttpStatus.BAD_REQUEST);

    }

    @ExceptionHandler(HttpUnauthorizedException.class)
    public final ResponseEntity<Object> handleHttpUnauthorizedException(HttpUnauthorizedException ex, WebRequest request) {

        ExceptionResponse exceptionResponse = createExceptionResponse(
                ErrorContants.RETURN_CODE_HTTP_UNAUTHORIZED,
                Integer.valueOf(ex.getMessage()), ex, request);

        return new ResponseEntity(exceptionResponse, HttpStatus.UNAUTHORIZED);

    }

    @ExceptionHandler(HttpInternalServerErrorException.class)
    public final ResponseEntity<Object> handleHttpInternalServerErrorException(HttpInternalServerErrorException ex, WebRequest request) {

        ExceptionResponse exceptionResponse = createExceptionResponse(
                ErrorContants.RETURN_CODE_HTTP_INTERNAL_SERVER_ERROR,
                Integer.valueOf(ex.getMessage()), ex, request);

        return new ResponseEntity(exceptionResponse, HttpStatus.INTERNAL_SERVER_ERROR);

    }

    @ExceptionHandler(HttpForbiddenException.class)
    public final ResponseEntity<Object> handleHttpForbiddenException(HttpForbiddenException ex, WebRequest request) {

        ExceptionResponse exceptionResponse = createExceptionResponse(
                ErrorContants.RETURN_CODE_HTTP_FORBIDDEN,
                Integer.valueOf(ex.getMessage()), ex, request);

        return new ResponseEntity(exceptionResponse, HttpStatus.FORBIDDEN);

    }





    private ExceptionResponse createExceptionResponse(int returnCode, int reasonCode, RuntimeException ex, WebRequest request) {

        String message = messageAccessor.getMessage(returnCode, reasonCode);
        String details = "";
        if (ex.getCause() != null) {
            details = ex.getCause().getMessage();
        }

        ExceptionResponse exceptionResponse = new ExceptionResponse(
                returnCode,
                reasonCode,
                message,
                request.getDescription(false),
                details,
                new Date()
        );

        return exceptionResponse;

    }

}

