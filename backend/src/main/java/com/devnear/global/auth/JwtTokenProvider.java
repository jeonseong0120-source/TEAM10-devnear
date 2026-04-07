package com.devnear.global.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
@RequiredArgsConstructor // UserDetailsService 주입을 위해 추가
public class JwtTokenProvider {

    private final UserDetailsService userDetailsService;
    private final String secretString = "devnear-project-secret-key-for-jwt-token-issuance-2026";
    private final SecretKey key = Keys.hmacShaKeyFor(secretString.getBytes(StandardCharsets.UTF_8));
    private final long validityInMilliseconds = 3600000; // 1시간

    // 1. 토큰 생성 (기존 유지)
    public String createToken(Long userId, String email, String role) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds);

        return Jwts.builder()
                .header().add("typ", "JWT").and()
                .subject(email)
                .claim("userId", userId)
                .claim("role", role)
                .issuedAt(now)
                .expiration(validity)
                .signWith(key)
                .compact();
    }

    // 2. 검문소에서 토큰이 유효한지 검사하는 기능
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(key) // 0.12.x 문법: verifyWith 사용
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            // 토큰이 위조되었거나, 만료되었거나, 잘못된 경우
            return false;
        }
    }

    // 3. 토큰을 통해 유저 신원 정보를 가져와서 '인증 객체'를 만드는 기능
    public Authentication getAuthentication(String token) {
        String email = getEmail(token);
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    // 4. 토큰에서 유저의 이메일(Subject)을 추출하는 보조 메서드
    public String getEmail(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }
}