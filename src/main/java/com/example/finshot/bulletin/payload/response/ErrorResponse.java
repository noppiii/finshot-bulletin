package com.example.finshot.bulletin.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ErrorResponse{
    private String errorMessage;
}