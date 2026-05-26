package com.digitaltravel.erp.exception;

import org.springframework.http.HttpStatus;

public class AppException extends RuntimeException {

    private final HttpStatus httpStatus;
    private final String errorCode;

    public AppException(HttpStatus httpStatus, String errorCode, String message) {
        super(message);
        this.httpStatus = httpStatus;
        this.errorCode = errorCode;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getErrorCode() {
        return errorCode;
    }

    // Factory methods tiện dùng
    public static AppException notFound(String message) {
        return new AppException(HttpStatus.NOT_FOUND, "NOT_FOUND", message);
    }

    public static AppException badRequest(String message) {
        return new AppException(HttpStatus.BAD_REQUEST, "BAD_REQUEST", message);
    }

    public static AppException forbidden(String message) {
        return new AppException(HttpStatus.FORBIDDEN, "FORBIDDEN", message);
    }

    public static AppException unauthorized(String message) {
        return new AppException(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", message);
    }
}
