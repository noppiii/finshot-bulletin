package com.example.finshot.bulletin.exception;

import com.example.finshot.bulletin.constant.ErrorCode;

public class NotFoundException extends CustomException {
    public NotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }
}

