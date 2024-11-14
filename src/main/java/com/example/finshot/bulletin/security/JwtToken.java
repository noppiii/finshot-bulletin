package com.example.finshot.bulletin.security;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class JwtToken {

    private final String accessToken;
    private final String refreshToken;

}
