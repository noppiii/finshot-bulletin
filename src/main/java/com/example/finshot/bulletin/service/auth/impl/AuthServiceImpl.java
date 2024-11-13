package com.example.finshot.bulletin.service.auth.impl;

import com.example.finshot.bulletin.constant.ErrorCode;
import com.example.finshot.bulletin.entity.User;
import com.example.finshot.bulletin.exception.DuplicateException;
import com.example.finshot.bulletin.exception.InvalidException;
import com.example.finshot.bulletin.payload.request.auth.RegisterRequest;
import com.example.finshot.bulletin.payload.response.global.CustomSuccessResponse;
import com.example.finshot.bulletin.repository.UserRepository;
import com.example.finshot.bulletin.service.auth.AuthService;
import com.example.finshot.bulletin.service.global.ImageService;
import com.example.finshot.bulletin.util.MultipartFileUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RequiredArgsConstructor
@Service
public class AuthServiceImpl implements AuthService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final ImageService imageService;

    @Value("${user.profile.image.default-directory}")
    private String profileImgDefaultDirectory;

    @Override
    public CustomSuccessResponse<String> register(RegisterRequest registerRequest) throws IOException {
        userRepository.findByEmail(registerRequest.getEmail()).ifPresent(it -> {
            throw new DuplicateException(ErrorCode.ALREADY_REGISTER);
        });

        MultipartFile profileImg = registerRequest.getProfileImg();
        String profileImgUrl = "";

        if (profileImg == null || profileImg.isEmpty()) {
            profileImgUrl = profileImgDefaultDirectory + "/default-profile.png";
        } else {
            if (!MultipartFileUtils.isPermission(profileImg.getInputStream())) {
                throw new InvalidException(ErrorCode.INVALID_IMAGE_TYPE);
            }

            profileImgUrl = imageService.upload(profileImg);
        }

        User user = User.of(
                registerRequest.getEmail(),
                passwordEncoder.encode(registerRequest.getPassword()),
                registerRequest.getNickname(),
                profileImgUrl
        );

        userRepository.save(user);
        return new CustomSuccessResponse<>("200", "Registration Successfully", null);
    }
}
