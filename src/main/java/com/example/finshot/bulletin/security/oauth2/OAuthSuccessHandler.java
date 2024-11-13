package com.example.finshot.bulletin.security.oauth2;

import com.example.finshot.bulletin.constant.ErrorCode;
import com.example.finshot.bulletin.entity.SocialCode;
import com.example.finshot.bulletin.entity.User;
import com.example.finshot.bulletin.exception.NotFoundException;
import com.example.finshot.bulletin.repository.UserRepository;
import com.example.finshot.bulletin.security.jwt.JwtToken;
import com.example.finshot.bulletin.security.jwt.JwtTokenProvider;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuthSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private UserRepository userRepository;
    private JwtTokenProvider jwtTokenProvider;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        String email = oAuth2User.getAttribute("email");
        SocialCode socialCode = oAuth2User.getAttribute("socialCode");
        Optional<User> findUser = userRepository.findByEmailAndSocialCode(email, socialCode);

        String name = oAuth2User.getAttribute("name");
        String picture = oAuth2User.getAttribute("picture");
        String socialAccessToken = oAuth2User.getAttribute("accessToken");

        User user = null;

        if (findUser.isEmpty()) {
            user = User.of(email, "", name, picture, socialCode, socialAccessToken);
            userRepository.save(user);
        } else {
            user = findUser.orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_USER));
            user.updateSocialMember(email, name, picture, socialAccessToken);
            userRepository.save(user);
            JwtToken jwtToken = jwtTokenProvider.createJwtToken(user.getEmail(), user.getRole().getValue());

            String targetUrl = UriComponentsBuilder.fromUriString("https://triptrip.site/home")
                    .queryParam("accessToken", jwtToken.getAccessToken())
                    .queryParam("refreshToken", jwtToken.getRefreshToken())
                    .queryParam("memberId", String.valueOf(user.getId()))
                    .queryParam("profileImgUrl", user.getProfileImg())
                    .build().toUriString();

            getRedirectStrategy().sendRedirect(request, response, targetUrl);

        }
    }

    private static String createCookie(String name, String value) {
        return ResponseCookie.from(name, value)
                .path("/")
                .httpOnly(true)
                .sameSite("None")
                .secure(true)
                .maxAge(60 * 60 * 6)
                .build()
                .toString();
    }
}

