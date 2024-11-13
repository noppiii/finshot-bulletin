package com.example.finshot.bulletin.constant;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    ALREADY_REGISTER("User already exists. Please log in or use a different email.", HttpStatus.CONFLICT),
    INVALID_IMAGE_TYPE("The provided image type is not supported. Please upload a valid image format.", HttpStatus.UNSUPPORTED_MEDIA_TYPE),
    INVALID_TYPE_TOKEN("The provided token type is invalid. Please use the correct token type.", HttpStatus.UNAUTHORIZED),
    EMPTY_AUTHORITY("User has no assigned authority. Access is not permitted.", HttpStatus.FORBIDDEN),
    EXPIRED_PERIOD_ACCESS_TOKEN("The access token has expired. Please refresh your session or log in again.", HttpStatus.UNAUTHORIZED),
    INVALID_ACCESS_TOKEN("The access token provided is invalid. Please provide a valid token.", HttpStatus.UNAUTHORIZED),
    EXPIRED_PERIOD_REFRESH_TOKEN("The refresh token has expired. Please log in again to continue.", HttpStatus.UNAUTHORIZED),
    INVALID_REFRESH_TOKEN("The refresh token provided is invalid. Please provide a valid token.", HttpStatus.UNAUTHORIZED),
    LOGOUTED_TOKEN("The token has been logged out. Please log in to obtain a new token.", HttpStatus.UNAUTHORIZED),

    NOT_FOUND_PROVIDER("Authentication provider not found. Please use a valid provider.", HttpStatus.BAD_REQUEST),
    NOT_FOUND_USER("User not found. Please check the credentials and try again.", HttpStatus.NOT_FOUND);;

    private final String message;
    private final HttpStatus status;

    ErrorCode(String message, HttpStatus status) {
        this.message = message;
        this.status = status;
    }
}

