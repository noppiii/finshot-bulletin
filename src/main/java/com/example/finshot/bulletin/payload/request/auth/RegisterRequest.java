package com.example.finshot.bulletin.payload.request.auth;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@ToString
public class RegisterRequest {

    @NotBlank(message = "Email is required.")
    @Email(message = "Enter a valid email format.")
    private String email;

    @NotBlank(message = "Password is required.")
    @Size(min = 8, max = 20, message = "Password must be between 8 and 20 characters.")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*]).+$", message = "Password must include uppercase, lowercase letters, numbers, and special characters.")
    private String password;

    @NotBlank(message = "Nickname is required.")
    @Size(min = 2, max = 20, message = "Nickname must be between 2 and 20 characters.")
    private String nickname;

    @NotNull(message = "Profile is required.")
    private MultipartFile profileImg;
}