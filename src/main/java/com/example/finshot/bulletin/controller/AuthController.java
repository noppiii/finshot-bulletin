package com.example.finshot.bulletin.controller;

import com.example.finshot.bulletin.payload.request.auth.RegisterRequest;
import com.example.finshot.bulletin.payload.response.global.CustomSuccessResponse;
import com.example.finshot.bulletin.service.auth.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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

    @PostMapping("/register")
    public String register(@ModelAttribute("registerRequest") @Valid RegisterRequest registerRequest, BindingResult bindingResult, Model model) throws IOException {

        if (bindingResult.hasErrors()) {
            return "register";
        }

        CustomSuccessResponse<String> response = authService.register(registerRequest);
        model.addAttribute("successMessage", response.getMessage());

        return "registrationSuccess";
    }
}
