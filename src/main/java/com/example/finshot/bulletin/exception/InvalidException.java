package com.example.finshot.bulletin.exception;

import com.example.finshot.bulletin.constant.ErrorCode;

public class InvalidException extends CustomException {
    public InvalidException(ErrorCode errorCode) {
        super(errorCode);
    }
}
