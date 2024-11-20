package com.example.finshot.bulletin.controller;

import com.example.finshot.bulletin.constant.ErrorCode;
import com.example.finshot.bulletin.entity.Post;
import com.example.finshot.bulletin.entity.User;
import com.example.finshot.bulletin.exception.CustomException;
import com.example.finshot.bulletin.payload.response.CustomSuccessResponse;
import com.example.finshot.bulletin.payload.response.post.GetPostsResponse;
import com.example.finshot.bulletin.payload.response.post.ReadPostResponse;
import com.example.finshot.bulletin.repository.PostRepository;
import com.example.finshot.bulletin.repository.UserRepository;
import com.example.finshot.bulletin.security.JwtTokenProvider;
import com.example.finshot.bulletin.service.post.PostService;
import com.example.finshot.bulletin.util.JwtTokenUtils;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final PostService postService;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtTokenUtils jwtTokenUtils;

    @GetMapping("/")
    public String showHomePage(@PageableDefault(size = 10) Pageable pageable, HttpSession session, Model model) {

        String accessToken = (String) session.getAttribute("Authorization");

        if (accessToken != null) {
        Authentication authentication = jwtTokenProvider.getAuthenticationByAccessToken(accessToken);
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.UNAUTHORIZED));
        model.addAttribute("nameUser", user.getNickname());
        } else {
            model.addAttribute("nameUser", null);
        }

        CustomSuccessResponse<GetPostsResponse> response = postService.getAllPosts(pageable);

        GetPostsResponse postsResponse = response.getContent();

        model.addAttribute("posts", postsResponse.getPosts());
        model.addAttribute("currentPage", postsResponse.getCurrentPage());
        model.addAttribute("totalPages", postsResponse.getTotalPages());
        model.addAttribute("totalElements", postsResponse.getTotalElements());

        return "home";
    }

    @GetMapping("/post/{slug}")
    public String showDetailPost(@PathVariable String slug, HttpSession session, Model model) {

        String accessToken = (String) session.getAttribute("Authorization");

        if (accessToken != null) {
            Authentication authentication = jwtTokenProvider.getAuthenticationByAccessToken(accessToken);
            String email = authentication.getName();
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new CustomException(ErrorCode.UNAUTHORIZED));
            model.addAttribute("nameUser", user.getNickname());
        } else {
            model.addAttribute("nameUser", null);
        }

        CustomSuccessResponse<ReadPostResponse> postResponse = postService.readPost(slug);
        ReadPostResponse post = postResponse.getContent();
        model.addAttribute("post", post);


        return "post-detail";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        String accessToken = (String) session.getAttribute("Authorization");

        if (accessToken == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        Authentication authentication = jwtTokenProvider.getAuthenticationByAccessToken(accessToken);
        String email = authentication.getName();
        jwtTokenUtils.deleteRefreshToken(email);
        session.invalidate();

        return "redirect:/";
    }
}
