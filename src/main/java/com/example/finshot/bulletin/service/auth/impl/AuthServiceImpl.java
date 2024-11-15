package com.example.finshot.bulletin.service.auth.impl;

import com.example.finshot.bulletin.constant.ErrorCode;
import com.example.finshot.bulletin.entity.User;
import com.example.finshot.bulletin.exception.CustomException;
import com.example.finshot.bulletin.exception.DuplicateException;
import com.example.finshot.bulletin.exception.InvalidException;
import com.example.finshot.bulletin.payload.request.auth.LoginRequest;
import com.example.finshot.bulletin.payload.request.auth.RegisterRequest;
import com.example.finshot.bulletin.payload.response.CustomSuccessResponse;
import com.example.finshot.bulletin.payload.response.auth.LoginResponse;
import com.example.finshot.bulletin.repository.UserRepository;
import com.example.finshot.bulletin.security.JwtToken;
import com.example.finshot.bulletin.security.JwtTokenProvider;
import com.example.finshot.bulletin.service.auth.AuthService;
import com.example.finshot.bulletin.service.global.ImageService;
import com.example.finshot.bulletin.util.MultipartFileUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class AuthServiceImpl implements AuthService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final ImageService imageService;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtTokenProvider jwtTokenProvider;

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

            profileImgUrl = imageService.uploadProfileUser(profileImg);
        }

        User user = User.of(
                registerRequest.getEmail(),
                passwordEncoder.encode(registerRequest.getPassword()),
                registerRequest.getNickname(),
                profileImgUrl
        );

        userRepository.save(user);
        return new CustomSuccessResponse<>("200", "Berhasil melakukan registrasi", null);
    }

    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new CustomException(ErrorCode.EMAIL_NOT_FOUND));
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                loginRequest.getEmail(), loginRequest.getPassword());
        Authentication authentication;

        try {
            authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        } catch (BadCredentialsException e) {
            throw new CustomException(ErrorCode.INVALID_CREDENTIALS);
        }

        String authorities = authentication.getAuthorities().stream()
                .map(a -> "ROLE_" + a.getAuthority())
                .collect(Collectors.joining(","));
        JwtToken jwtToken = jwtTokenProvider.createJwtToken(loginRequest.getEmail(), authorities);

        return LoginResponse.of(jwtToken, user);
    }
}
