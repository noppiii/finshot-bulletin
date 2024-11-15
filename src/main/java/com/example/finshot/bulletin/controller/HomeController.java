package com.example.finshot.bulletin.controller;

import com.example.finshot.bulletin.constant.ErrorCode;
import com.example.finshot.bulletin.entity.User;
import com.example.finshot.bulletin.exception.CustomException;
import com.example.finshot.bulletin.repository.UserRepository;
import com.example.finshot.bulletin.security.JwtTokenProvider;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    @GetMapping("/")
    public String showHomePage(Model model, HttpSession session) {
        String accessToken = (String) session.getAttribute("Authorization");
        if (accessToken == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        Authentication authentication = jwtTokenProvider.getAuthenticationByAccessToken(accessToken);
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.UNAUTHORIZED));
        model.addAttribute("nameUser", user.getNickname());
        return "home";
    }
}
