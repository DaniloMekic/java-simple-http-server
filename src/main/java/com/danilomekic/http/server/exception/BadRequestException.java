package com.danilomekic.http.server.exception;

public class BadRequestException extends HttpException {
    public BadRequestException(String message) {
        super(400, "Bad Request", message);
    }

    public BadRequestException(String message, Throwable cause) {
        super(400, "Bad Request", message, cause);
    }
}
