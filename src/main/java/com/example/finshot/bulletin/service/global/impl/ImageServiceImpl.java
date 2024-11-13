package com.example.finshot.bulletin.service.global.impl;

import com.example.finshot.bulletin.service.global.ImageService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {

    @Getter
    @Value("${user.profile.image.store-directory}")
    private String profileImgStoreDirectory;

    @Override
    public String upload(MultipartFile profileImg) {
        String ext = Optional.ofNullable(profileImg.getOriginalFilename())
                .filter(f -> f.contains("."))
                .map(f -> f.split("\\.")[1])
                .orElse("");

        String fileName = "%s.%s".formatted(UUID.randomUUID(), ext);
        String formatDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy_MM_dd"));
        String filePath = profileImgStoreDirectory + "/users/" + formatDate + "/" + fileName;

        File directory = new File(profileImgStoreDirectory + "/users/" + formatDate);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        try {
            File localFile = new File(filePath);
            profileImg.transferTo(localFile);
            log.debug("Image uploaded successfully to: {}", filePath);
        } catch (IOException e) {
            throw new RuntimeException("Failed to store image", e);
        }

        return filePath;
    }

    @Override
    public void delete(String filePath) {
        File file = new File(filePath);
        if (file.exists() && file.delete()) {
            log.debug("Deleted file successfully: {}", filePath);
        } else {
            log.warn("Failed to delete file: {}", filePath);
            throw new RuntimeException("Failed to delete file");
        }
    }
}
