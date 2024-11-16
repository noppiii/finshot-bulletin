package com.example.finshot.bulletin.payload.response.post;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class UpdatePostRequest {

    private Long postId;

    @NotBlank(message = "Silakan masukkan judul.")
    @Size(min = 5, max = 200, message = "Judulnya bisa minimal 5 karakter dan maksimal 30 karakter.")
    private String title;

    @Size(max = 3, message = "Anda dapat mendaftarkan hingga tiga tag.")
    private List<String> tags;

    @NotBlank(message = "Silakan masukkan content.")
    @Size(max = 15000, message = "Text content terlalu besar")
    private String content;

    private MultipartFile file;

    @Builder
    public UpdatePostRequest(String title, List<String> tags, String content, MultipartFile file) {
        this.title = title;
        this.tags = tags;
        this.content = content;
        this.file = file;
    }
}

