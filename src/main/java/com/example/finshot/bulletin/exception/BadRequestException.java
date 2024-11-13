package com.example.finshot.bulletin.exception;

import com.example.finshot.bulletin.constant.ErrorCode;

public class BadRequestException extends CustomException {
    public BadRequestException(ErrorCode errorCode) {
        super(errorCode);
    }
}