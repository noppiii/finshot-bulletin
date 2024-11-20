package com.example.finshot.bulletin.controller;

import com.example.finshot.bulletin.payload.request.auth.LoginRequest;
import com.example.finshot.bulletin.payload.request.auth.RegisterRequest;
import com.example.finshot.bulletin.payload.response.CustomSuccessResponse;
import com.example.finshot.bulletin.payload.response.auth.LoginResponse;
import com.example.finshot.bulletin.service.auth.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;

@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("registerRequest", new RegisterRequest());
        return "register";
    }

    @PostMapping("/register/post")
    public String register(
            @ModelAttribute("registerRequest") @Valid RegisterRequest registerRequest,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("registerRequest", registerRequest);
            return "register";
        }

        try {
            CustomSuccessResponse<String> response = authService.register(registerRequest);
            redirectAttributes.addFlashAttribute("successMessage", response.getMessage());
            return "redirect:/auth/login";
        } catch (IOException e) {
            model.addAttribute("errorMessage", "An error occurred during registration. Please repeat again.");
            return "register";
        }
    }



    @GetMapping("/login")
    public String showLoginForm(Model model) {

        if (!model.containsAttribute("successMessage")) {
            model.addAttribute("successMessage", "");
        }

        model.addAttribute("loginRequest", new LoginRequest());
        return "login";
    }


    @PostMapping("/login/post")
    public String login(@ModelAttribute("loginRequest") @Valid LoginRequest loginRequest, BindingResult bindingResult, Model model, HttpSession session, HttpServletResponse response) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("loginRequest", loginRequest);
            return "login";
        }

        try {
            LoginResponse loginResponse = authService.login(loginRequest);
            session.setAttribute("Authorization", loginResponse.getAccessToken());
            session.setAttribute("user", loginResponse.getUser());
            response.setHeader("Authorization", loginResponse.getTokenType() + " " + loginResponse.getAccessToken());
            return "redirect:/";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Invalid login credentials");
            return "login";
        }
    }


}
