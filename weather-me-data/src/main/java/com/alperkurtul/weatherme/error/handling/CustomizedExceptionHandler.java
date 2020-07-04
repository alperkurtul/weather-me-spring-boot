package com.alperkurtul.weatherme.error.handling;

import com.alperkurtul.weatherme.error.ErrorContants;
import com.alperkurtul.weatherme.error.exception.*;
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

    @ExceptionHandler(UnexpectedExceptionN99999.class)
    public final ResponseEntity<Object> handleUnexpectedException(UnexpectedExceptionN99999 ex, WebRequest request) {

        ExceptionResponse exceptionResponse = createExceptionResponse(
                ErrorContants.RETURN_CODE_UNEXPECTED_EXCEPTION,
                Integer.valueOf(ex.getMessage()), ex, request);

        return new ResponseEntity(exceptionResponse, HttpStatus.BAD_REQUEST);

    }

    @ExceptionHandler(MandatoryInputMissingExceptionN20.class)
    public final ResponseEntity<Object> handleMandatoryInputMissingException(MandatoryInputMissingExceptionN20 ex, WebRequest request) {

        ExceptionResponse exceptionResponse = createExceptionResponse(
                ErrorContants.RETURN_CODE_MANDATORY_INPUT_MISSING,
                Integer.valueOf(ex.getMessage()), ex, request);

        return new ResponseEntity(exceptionResponse, HttpStatus.BAD_REQUEST);

    }

    @ExceptionHandler(MandatoryValidationFailedExceptionN21.class)
    public final ResponseEntity<Object> handleMandatoryValidationFailedException(MandatoryValidationFailedExceptionN21 ex, WebRequest request) {

        ExceptionResponse exceptionResponse = createExceptionResponse(
                ErrorContants.RETURN_CODE_MANDATORY_VALIDATION_FAILED,
                Integer.valueOf(ex.getMessage()), ex, request);

        return new ResponseEntity(exceptionResponse, HttpStatus.BAD_REQUEST);

    }

    @ExceptionHandler(EntityAlreadyExistExceptionN43.class)
    public final ResponseEntity<Object> handleEntityAlreadyExistException(EntityAlreadyExistExceptionN43 ex, WebRequest request) {

        ExceptionResponse exceptionResponse = createExceptionResponse(
                ErrorContants.RETURN_CODE_ENTITY_ALREADY_EXIST,
                Integer.valueOf(ex.getMessage()), ex, request);

        return new ResponseEntity(exceptionResponse, HttpStatus.BAD_REQUEST);

    }

    @ExceptionHandler(EntityCreationFailedExceptionN40.class)
    public final ResponseEntity<Object> handleEntityCreationFailedException(EntityCreationFailedExceptionN40 ex, WebRequest request) {

        ExceptionResponse exceptionResponse = createExceptionResponse(
                ErrorContants.RETURN_CODE_ENTITY_CREATION_FAILED,
                Integer.valueOf(ex.getMessage()), ex, request);

        return new ResponseEntity(exceptionResponse, HttpStatus.BAD_REQUEST);

    }

    @ExceptionHandler(EntityDeletionFailedExceptionN42.class)
    public final ResponseEntity<Object> handleEntityDeletionFailedException(EntityDeletionFailedExceptionN42 ex, WebRequest request) {

        ExceptionResponse exceptionResponse = createExceptionResponse(
                ErrorContants.RETURN_CODE_ENTITY_DELETION_FAILED,
                Integer.valueOf(ex.getMessage()), ex, request);

        return new ResponseEntity(exceptionResponse, HttpStatus.BAD_REQUEST);

    }

    @ExceptionHandler(EntityNotFoundExceptionN10.class)
    public final ResponseEntity<Object> handleEntityNotFoundException(EntityNotFoundExceptionN10 ex, WebRequest request) {

        ExceptionResponse exceptionResponse = createExceptionResponse(
                ErrorContants.RETURN_CODE_ENTITY_NOT_FOUND,
                Integer.valueOf(ex.getMessage()), ex, request);

        return new ResponseEntity(exceptionResponse, HttpStatus.NOT_FOUND);

    }

    @ExceptionHandler(EntityUpdateFailedExceptionN41.class)
    public final ResponseEntity<Object> handleEntityUpdateFailedException(EntityUpdateFailedExceptionN41 ex, WebRequest request) {

        ExceptionResponse exceptionResponse = createExceptionResponse(
                ErrorContants.RETURN_CODE_ENTITY_UPDATE_FAILED,
                Integer.valueOf(ex.getMessage()), ex, request);

        return new ResponseEntity(exceptionResponse, HttpStatus.BAD_REQUEST);

    }

    @ExceptionHandler(ServiceValidationFailedExceptionN50.class)
    public final ResponseEntity<Object> handleServiceValidationFailedException(ServiceValidationFailedExceptionN50 ex, WebRequest request) {

        ExceptionResponse exceptionResponse = createExceptionResponse(
                ErrorContants.RETURN_CODE_SERVICE_VALIDATION_FAILED,
                Integer.valueOf(ex.getMessage()), ex, request);

        return new ResponseEntity(exceptionResponse, HttpStatus.BAD_REQUEST);

    }

    @ExceptionHandler(HttpNotFoundExceptionN404.class)
    public final ResponseEntity<Object> handleHttpNotFoundException(HttpNotFoundExceptionN404 ex, WebRequest request) {

        ExceptionResponse exceptionResponse = createExceptionResponse(
                ErrorContants.RETURN_CODE_HTTP_NOT_FOUND,
                Integer.valueOf(ex.getMessage()), ex, request);

        return new ResponseEntity(exceptionResponse, HttpStatus.NOT_FOUND);

    }

    @ExceptionHandler(HttpBadRequestExceptionN400.class)
    public final ResponseEntity<Object> handleHttpBadRequestException(HttpBadRequestExceptionN400 ex, WebRequest request) {

        ExceptionResponse exceptionResponse = createExceptionResponse(
                ErrorContants.RETURN_CODE_HTTP_BAD_REQUEST,
                Integer.valueOf(ex.getMessage()), ex, request);

        return new ResponseEntity(exceptionResponse, HttpStatus.BAD_REQUEST);

    }

    @ExceptionHandler(HttpUnauthorizedExceptionN401.class)
    public final ResponseEntity<Object> handleHttpUnauthorizedException(HttpUnauthorizedExceptionN401 ex, WebRequest request) {

        ExceptionResponse exceptionResponse = createExceptionResponse(
                ErrorContants.RETURN_CODE_HTTP_UNAUTHORIZED,
                Integer.valueOf(ex.getMessage()), ex, request);

        return new ResponseEntity(exceptionResponse, HttpStatus.UNAUTHORIZED);

    }

    @ExceptionHandler(HttpInternalServerErrorExceptionN500.class)
    public final ResponseEntity<Object> handleHttpInternalServerErrorException(HttpInternalServerErrorExceptionN500 ex, WebRequest request) {

        ExceptionResponse exceptionResponse = createExceptionResponse(
                ErrorContants.RETURN_CODE_HTTP_INTERNAL_SERVER_ERROR,
                Integer.valueOf(ex.getMessage()), ex, request);

        return new ResponseEntity(exceptionResponse, HttpStatus.INTERNAL_SERVER_ERROR);

    }

    @ExceptionHandler(HttpForbiddenExceptionN403.class)
    public final ResponseEntity<Object> handleHttpForbiddenException(HttpForbiddenExceptionN403 ex, WebRequest request) {

        ExceptionResponse exceptionResponse = createExceptionResponse(
                ErrorContants.RETURN_CODE_HTTP_FORBIDDEN,
                Integer.valueOf(ex.getMessage()), ex, request);

        return new ResponseEntity(exceptionResponse, HttpStatus.FORBIDDEN);

    }





    private ExceptionResponse createExceptionResponse(int returnCode, int reasonCode, Exception ex, WebRequest request) {

        String message = messageAccessor.getMessage(returnCode, reasonCode);
        String details = "";
        if (ex.getCause() != null) {
            if (ex.getCause().getMessage() != null) {
                details = ex.getCause().getMessage();
            }
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

