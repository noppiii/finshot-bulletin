package com.example.finshot.bulletin.service.post;

import org.springframework.web.multipart.MultipartFile;

public interface PostFileService {

    String upload(MultipartFile multipartFile, String email);
}
