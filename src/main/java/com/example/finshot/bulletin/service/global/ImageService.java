package com.example.finshot.bulletin.service.global;

import org.springframework.web.multipart.MultipartFile;

public interface ImageService {
    String upload(MultipartFile profileImg);
    void delete(String filePath);
}
