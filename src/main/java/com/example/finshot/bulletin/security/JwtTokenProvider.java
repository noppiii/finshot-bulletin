package com.example.finshot.bulletin.security;

import com.example.finshot.bulletin.constant.ErrorCode;
import com.example.finshot.bulletin.exception.InvalidException;
import com.example.finshot.bulletin.util.JwtTokenUtils;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtTokenProvider {

    private final JwtTokenUtils jwtTokenUtils;

    @Value("${spring.security.jwt.token.access-expiration-time}")
    private long accessExpirationTime;

    @Value("${spring.security.jwt.token.refresh-expiration-time}")
    private long refreshExpirationTime;

    private final Key key;

    @Autowired
    public JwtTokenProvider(@Value("${spring.security.jwt.token.secret}") String secretKey, JwtTokenUtils jwtTokenUtils) {
        this.jwtTokenUtils = jwtTokenUtils;
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public JwtToken createJwtToken(String email, String authorities) {

        Date now = new Date();
        Claims claims = Jwts.claims().setSubject(email);
        claims.put("roles", authorities);


        String accessToken = createAccessToken(claims, new Date(now.getTime() + accessExpirationTime));
        String refreshToken = createRefreshToken(claims, new Date(now.getTime() + refreshExpirationTime));


        return new JwtToken(accessToken, refreshToken);
    }


    private String createAccessToken(Claims claims, Date expiredDate) {
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(expiredDate)
                .signWith(SignatureAlgorithm.HS256, key)
                .compact();
    }

    private String createRefreshToken(Claims claims, Date expiredDate) {
        String refreshToken = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(expiredDate)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
        jwtTokenUtils.setRefreshToken(claims.getSubject(), refreshToken);

        return refreshToken;
    }

    public Authentication getAuthenticationByAccessToken(String accessToken) {

        Claims claims = validateAccessToken(accessToken);

        if (claims.get("roles") == null) {
            throw new InvalidException(ErrorCode.EMPTY_AUTHORITY);
        }

        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get("roles").toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        UserDetails principal = new User(claims.getSubject(), "", authorities);
        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }

    private Claims validateAccessToken(String accessToken) {
        try {
            return parseToken(accessToken);

        } catch (ExpiredJwtException e) {
            throw new InvalidException(ErrorCode.EXPIRED_PERIOD_ACCESS_TOKEN);
        } catch (final JwtException | IllegalArgumentException e) {
            throw new InvalidException(ErrorCode.INVALID_ACCESS_TOKEN);
        }
    }

    private Claims parseToken(final String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}

