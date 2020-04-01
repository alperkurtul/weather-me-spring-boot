package com.alperkurtul.weatherme.error.handling;

import java.util.Date;

public class ExceptionResponse {
    private int returnCode;
    private int reasonCode;
    private String message;
    private String path;
    private String details;
    private Date timestamp;

    public ExceptionResponse(int returnCode, int reasonCode, String message, String path, String details, Date timestamp) {
        this.returnCode = returnCode;
        this.reasonCode = reasonCode;
        this.message = message;
        this.details = details;
        this.path = path;
        this.timestamp = timestamp;
    }

    public int getReturnCode() {
        return returnCode;
    }

    public int getReasonCode() {
        return reasonCode;
    }

    public String getMessage() {
        return message;
    }

    public String getDetails() {
        return details;
    }

    public String getPath() {
        return path;
    }

    public Date getTimestamp() {
        return timestamp;
    }

}
