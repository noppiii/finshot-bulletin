package com.example.finshot.bulletin.payload.response.global;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class CustomErrorResponse<T> {
    private String statusCode;
    private String message;
    private T content;
    private Map<String, List<String>> errors;

    public CustomErrorResponse(String statusCode, String message, T content, Map<String, List<String>> errors) {
        this.statusCode = statusCode;
        this.message = message;
        this.content = content;
        this.errors = errors;
    }
}

