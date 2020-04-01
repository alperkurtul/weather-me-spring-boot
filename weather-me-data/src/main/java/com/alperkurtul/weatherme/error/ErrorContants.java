package com.alperkurtul.weatherme.error;

public final class ErrorContants {

    // ALL RETURN CODES
    public static final int RETURN_CODE_OK = 1;
    // Return Codes for Http Status
    public static final int RETURN_CODE_HTTP_BAD_REQUEST = -400;
    public static final int RETURN_CODE_HTTP_UNAUTHORIZED = -401;
    public static final int RETURN_CODE_HTTP_FORBIDDEN = -403;
    public static final int RETURN_CODE_HTTP_NOT_FOUND = -404;
    public static final int RETURN_CODE_HTTP_INTERNAL_SERVER_ERROR = -500;
    // Other Return Codes
    public static final int RETURN_CODE_MANDATORY_INPUT_MISSING = -20;
    public static final int RETURN_CODE_MANDATORY_VALIDATION_FAILED = -1200;
    public static final int RETURN_CODE_ENTITY_NOT_FOUND = -10;
    public static final int RETURN_CODE_ENTITY_CREATION_FAILED = -40;
    public static final int RETURN_CODE_ENTITY_UPDATE_FAILED = -41;
    public static final int RETURN_CODE_ENTITY_DELETION_FAILED = -42;
    public static final int RETURN_CODE_SERVICE_VALIDATION_FAILED = -50;
    public static final int RETURN_CODE_UNEXPECTED_EXCEPTION = -999;
    // Add your Return Codes below. ( Return codes' value must be negative )



    // REASON CODES
    public static final int REASON_CODE_OK = 1;
    // Reason Codes for Http Status
    public static final int REASON_CODE_HTTP_BAD_REQUEST = 400;
    public static final int REASON_CODE_HTTP_UNAUTHORIZED = 401;
    public static final int REASON_CODE_HTTP_FORBIDDEN = 403;
    public static final int REASON_CODE_HTTP_NOT_FOUND = 404;
    public static final int REASON_CODE_HTTP_INTERNAL_SERVER_ERROR = 500;
    // Other Return Codes
    public static final int REASON_CODE_MANDATORY_VALIDATION_FAILED = 1200;
    public static final int REASON_CODE_ENTITY_NOT_FOUND = 10;
    public static final int REASON_CODE_ENTITY_CREATION_FAILED = 40;
    public static final int REASON_CODE_ENTITY_UPDATE_FAILED = 41;
    public static final int REASON_CODE_ENTITY_DELETION_FAILED = 42;
    public static final int REASON_CODE_SERVICE_VALIDATION_FAILED = 50;
    public static final int REASON_CODE_UNEXPECTED_EXCEPTION = 999;
    // Add your Reason Codes below. ( Reason codes' value must be negative )

}
