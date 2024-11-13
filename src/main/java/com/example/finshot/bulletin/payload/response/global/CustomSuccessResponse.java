package com.example.finshot.bulletin.payload.response.global;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class CustomSuccessResponse<T> {

    @JsonProperty("statusCode")
    private String statusCode;

    @JsonProperty("message")
    private String message;

    @JsonProperty("content")
    private T content;

    public CustomSuccessResponse(String statusCode, String message, T content) {
        this.statusCode = statusCode;
        this.message = message;
        this.content = content;
    }
}

