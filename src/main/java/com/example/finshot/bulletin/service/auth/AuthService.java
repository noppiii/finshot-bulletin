package com.example.finshot.bulletin.service.auth;

import com.example.finshot.bulletin.payload.request.auth.LoginRequest;
import com.example.finshot.bulletin.payload.request.auth.RegisterRequest;
import com.example.finshot.bulletin.payload.response.CustomSuccessResponse;
import com.example.finshot.bulletin.payload.response.auth.LoginResponse;

import java.io.IOException;

public interface AuthService {

    CustomSuccessResponse<String> register(RegisterRequest registerRequest) throws IOException;
    LoginResponse login(LoginRequest loginRequest);
}
