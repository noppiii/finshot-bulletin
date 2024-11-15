package com.example.finshot.bulletin.service.post.impl;

import com.example.finshot.bulletin.service.post.PostFileService;
import lombok.Getter;
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
public class PostFileServiceImpl implements PostFileService {

    @Getter
    @Value("${post.file.store-directory}")
    private String postFileStoreDirectory;

    @Override
    public String upload(MultipartFile multipartFile, String email) {
        String ext = Optional.ofNullable(multipartFile.getOriginalFilename())
                .filter(f -> f.contains("."))
                .map(f -> f.substring(f.lastIndexOf(".") + 1))
                .orElse("");
        String fileName = "%s.%s".formatted(UUID.randomUUID(), ext);
        String formatDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy_MM_dd"));
        String tempFilePath = System.getProperty("java.io.tmpdir") + "/" + fileName;
        File tempFile = new File(tempFilePath);

        try {
            multipartFile.transferTo(tempFile);
            log.debug("Temporary upload location: {}", tempFile.getAbsolutePath());
            String targetDirPath = postFileStoreDirectory + "/posts/" + formatDate;
            File targetDir = new File(targetDirPath);

            if (!targetDir.exists() && !targetDir.mkdirs()) {
                throw new RuntimeException("Failed to create directory: " + targetDir.getAbsolutePath());
            }

            File targetFile = new File(targetDir, fileName);
            Files.copy(tempFile.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            tempFile.delete();
            String relativePath = "posts/" + formatDate + "/" + fileName;
            return relativePath;
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file", e);
        }
    }
}
