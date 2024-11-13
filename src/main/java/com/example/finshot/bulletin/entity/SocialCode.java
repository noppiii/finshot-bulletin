package com.example.finshot.bulletin.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SocialCode {

    GOOGLE("google"),
    NORMAL("normal");

    private final String type;
}

