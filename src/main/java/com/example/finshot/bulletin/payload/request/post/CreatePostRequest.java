package com.example.finshot.bulletin.payload.request.post;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
@ToString
public class CreatePostRequest {

    @NotBlank(message = "Silakan masukkan judul.")
    @Size(min = 5, max = 200, message = "Judulnya bisa minimal 5 karakter dan maksimal 30 karakter.")
    private String title;

    @Size(max = 3, message = "Anda dapat mendaftarkan hingga tiga tag.")
    private List<String> tags;

    @NotBlank(message = "Silakan masukkan content.")
    @Size(max = 15000, message = "Text content terlalu besar")
    private String content;

    private MultipartFile file;
}
