package com.example.finshot.bulletin.exception;

import com.example.finshot.bulletin.constant.ErrorCode;

public class DuplicateException extends CustomException {
    public DuplicateException(ErrorCode errorCode) {
        super(errorCode);
    }
}
