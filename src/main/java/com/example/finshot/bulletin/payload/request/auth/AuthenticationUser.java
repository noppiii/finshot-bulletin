package com.example.finshot.bulletin.payload.request.auth;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@Getter
public class AuthenticationUser extends org.springframework.security.core.userdetails.User {

    private final com.example.finshot.bulletin.entity.User userDetails;

    public AuthenticationUser(com.example.finshot.bulletin.entity.User user, Collection<? extends GrantedAuthority> authorities) {
        super(user.getEmail(), user.getPassword(), authorities);
        this.userDetails = user;
    }
}
