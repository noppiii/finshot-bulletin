package com.example.finshot.bulletin.payload.response.global;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ErrorResponse{
    private String errorMessage;
}