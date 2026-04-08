package com.devnear.global.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        Map<String, Object> attributes = oAuth2User.getAttributes();

        // [보고] JwtTokenProvider.createToken 규격에 맞춰 데이터 준비
        Long userId = (Long) attributes.get("id");
        String email = (String) attributes.get("email");
        String role = authentication.getAuthorities().iterator().next().getAuthority();

        // [보고] 메서드명 createToken으로 수정 및 파라미터 3개 전달
        String accessToken = jwtTokenProvider.createToken(userId, email, role);

        String targetUrl = UriComponentsBuilder.fromUriString("http://localhost:3000/oauth/redirect")
                .queryParam("token", accessToken)
                .build().toUriString();

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}