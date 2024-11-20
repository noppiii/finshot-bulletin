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

    @NotBlank(message = "Please enter a title.")
    @Size(min = 5, max = 200, message = "The title must be between 5 and 30 characters.")
    private String title;

    @Size(max = 3, message = "You can register up to three tags.")
    private List<String> tags;

    @NotBlank(message = "Please enter content.")
    @Size(max = 15000, message = "Text content is too large.")
    private String content;

    private MultipartFile file;

}
