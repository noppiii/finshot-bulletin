package com.example.finshot.bulletin.payload.request.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class LoginRequest {

    @NotBlank(message = "Email is required.")
    @Email(message = "Enter your email in the correct format.")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 20, message = "The password must consist of a minimum of 8 and a maximum of 20 characters.")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*]).+$", message = "Passwords must use uppercase letters, lowercase letters, numbers, and special characters.")
    private String password;
}
