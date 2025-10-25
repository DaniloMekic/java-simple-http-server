package com.danilomekic.http.server.exception;

import com.danilomekic.http.server.model.StatusCode;

/**
 * Base Exception class for HTTP-related errors. Encapsulates HTTP status code and error details.
 */
public class HttpException extends RuntimeException {
    private final StatusCode statusCode;
    private final String errorCode;
    private final Object exceptionDetails;

    public HttpException(StatusCode statusCode, String message) {
        this(statusCode, message, null, null);
    }

    public HttpException(StatusCode statusCode, String message, String errorCode) {
        this(statusCode, message, errorCode, null);
    }

    public HttpException(
            StatusCode statusCode, String message, String errorCode, Object exceptionDetails) {
        super(message);
        this.statusCode = statusCode;
        this.errorCode = errorCode;
        this.exceptionDetails = exceptionDetails;
    }

    public HttpException(StatusCode statusCode, String message, Throwable cause) {
        super(message, cause);
        this.statusCode = statusCode;
        this.errorCode = null;
        this.exceptionDetails = null;
    }

    public StatusCode getStatusCode() {
        return statusCode;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public Object getExceptionDetails() {
        return exceptionDetails;
    }

    public int getStatusCodeValue() {
        return statusCode.getStatusCode();
    }
}
