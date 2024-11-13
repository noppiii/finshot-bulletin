package com.example.finshot.bulletin.service.auth;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface SecurityService {
    UserDetails loadUserByUsername(String email) throws UsernameNotFoundException;
}
