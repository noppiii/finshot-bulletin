package com.example.finshot.bulletin.controller;

import com.example.finshot.bulletin.constant.ErrorCode;
import com.example.finshot.bulletin.entity.Tag;
import com.example.finshot.bulletin.entity.User;
import com.example.finshot.bulletin.exception.CustomException;
import com.example.finshot.bulletin.exception.InvalidException;
import com.example.finshot.bulletin.payload.request.post.CreatePostRequest;
import com.example.finshot.bulletin.payload.response.CustomSuccessResponse;
import com.example.finshot.bulletin.payload.response.post.GetMyPostsResponse;
import com.example.finshot.bulletin.repository.TagRepository;
import com.example.finshot.bulletin.repository.UserRepository;
import com.example.finshot.bulletin.security.JwtTokenProvider;
import com.example.finshot.bulletin.service.post.PostService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@Slf4j
public class PostController {

    private final PostService postService;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final TagRepository tagRepository;

    @GetMapping("/post")
    public String showPostIndex(HttpSession session, Model model) {
        String accessToken = (String) session.getAttribute("Authorization");

        if (accessToken == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        Authentication authentication = jwtTokenProvider.getAuthenticationByAccessToken(accessToken);
        String email = authentication.getName();
        CustomSuccessResponse<GetMyPostsResponse> response = postService.getMyPosts(email);
        model.addAttribute("posts", response.getContent().getPosts());
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.UNAUTHORIZED));
        model.addAttribute("nameUser", user.getNickname());

        return "post-index";
    }

    @GetMapping("/post/create")
    public String showPostCreate(HttpSession session, Model model) {
        String accessToken = (String) session.getAttribute("Authorization");

        if (accessToken == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        Authentication authentication = jwtTokenProvider.getAuthenticationByAccessToken(accessToken);
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.UNAUTHORIZED));
        List<String> tags = tagRepository.findAll()
                .stream()
                .map(Tag::getName)
                .collect(Collectors.toList());
        System.out.println("List tags: " + tags);
        model.addAttribute("availableTags", tags);
        model.addAttribute("nameUser", user.getNickname());
        model.addAttribute("createPostRequest", new CreatePostRequest());

        return "post-create";
    }



    @PostMapping("/post/create/submit")
    public String createPost(@ModelAttribute("createPostRequest") @Valid CreatePostRequest request, BindingResult bindingResult, HttpSession session, Model model, RedirectAttributes redirectAttributes) {

        String accessToken = (String) session.getAttribute("Authorization");
        if (accessToken == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        Authentication authentication = jwtTokenProvider.getAuthenticationByAccessToken(accessToken);
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.UNAUTHORIZED));
        model.addAttribute("nameUser", user.getNickname());

        if (bindingResult.hasErrors()) {
            return "post-create";
        }

        MultipartFile postFile = request.getFile();
        CustomSuccessResponse<String> response = postService.createPost(request, postFile, email);
        redirectAttributes.addFlashAttribute("successMessage", response.getMessage());
        return "redirect:/post";
    }


}
