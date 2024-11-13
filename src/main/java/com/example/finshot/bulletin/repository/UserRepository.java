package com.example.finshot.bulletin.repository;

import com.example.finshot.bulletin.entity.SocialCode;
import com.example.finshot.bulletin.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByEmailAndSocialCode(String email, SocialCode socialCode);
}
