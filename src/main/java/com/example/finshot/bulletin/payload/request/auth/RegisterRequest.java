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

    @NotBlank(message = "Email wajib diisi.")
    @Email(message = "Masukkan email sesuai format yang benar.")
    private String email;

    @NotBlank(message = "Kata sandi wajib diisi.")
    @Size(min = 8, max = 20, message = "Kata sandi harus terdiri dari minimal 8 dan maksimal 20 karakter.")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*]).+$", message = "Kata sandi harus menggunakan huruf besar, huruf kecil, angka, dan karakter khusus.")
    private String password;

    @NotBlank(message = "Nama panggilan wajib diisi.")
    @Size(min = 2, max = 20, message = "Nama panggilan harus terdiri dari minimal 2 dan maksimal 20 karakter.")
    private String nickname;

    @NotNull(message = "Profile wajib diisi")
    private MultipartFile profileImg;
}