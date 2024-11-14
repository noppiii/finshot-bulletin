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
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
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
    public String uploadProfileUser(MultipartFile profileImg) {
        String ext = Optional.ofNullable(profileImg.getOriginalFilename())
                .filter(f -> f.contains("."))
                .map(f -> f.substring(f.lastIndexOf(".") + 1))
                .orElse("");

        String fileName = "%s.%s".formatted(UUID.randomUUID(), ext);
        String formatDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy_MM_dd"));

        String tempFilePath = System.getProperty("java.io.tmpdir") + "/" + fileName;
        File tempFile = new File(tempFilePath);

        try {
            profileImg.transferTo(tempFile);
            log.debug("Temporary upload location: {}", tempFile.getAbsolutePath());

            String targetDirPath = profileImgStoreDirectory + "/users/" + formatDate;
            File targetDir = new File(targetDirPath);

            if (!targetDir.exists() && !targetDir.mkdirs()) {
                throw new RuntimeException("Failed to create directory: " + targetDir.getAbsolutePath());
            }

            File targetFile = new File(targetDir, fileName);
            Files.copy(tempFile.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            tempFile.delete();
            String relativePath = "users/" + formatDate + "/" + fileName;
            return relativePath;
        } catch (IOException e) {
            throw new RuntimeException("Gagal store gambar", e);
        }
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
