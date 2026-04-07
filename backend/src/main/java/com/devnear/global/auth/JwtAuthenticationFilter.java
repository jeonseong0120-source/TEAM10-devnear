package com.devnear.global.auth;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * 모든 HTTP 요청에서 JWT 토큰의 유효성을 검증하는 보안 필터(Security Filter)
 * FilterChain의 전두에 위치하여 인증되지 않은 사용자의 접근을 1차적으로 차단
 */
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        // 1. 요청 헤더(Authorization)에서 JWT 토큰을 추출합니다.
        String token = resolveToken(request);

        try {
            // 2. 토큰이 존재하고 서명/만료일 등 기본 유효성 검사를 통과하는지 확인합니다.
            if (token != null && jwtTokenProvider.validateToken(token)) {

                /*
                 * [보안 강화 지점]
                 * 토큰은 유효하지만 DB에 유저가 없는 경우(탈퇴 등)를 대비합니다.
                 * getAuthentication 내부의 loadUserByUsername 호출 시 UsernameNotFoundException이 발생할 수 있습니다.
                 */
                Authentication authentication = jwtTokenProvider.getAuthentication(token);

                // 유효한 유저임이 확인되면 SecurityContext에 인증 정보를 등록합니다.
                SecurityContextHolder.getContext().setAuthentication(authentication);
                log.debug("인증 성공: {}", authentication.getName());
            }
        } catch (UsernameNotFoundException e) {
            // 토큰은 유효하나 DB에 해당 유저 정보가 없는 경우 (탈퇴 유저 등)
            log.warn("삭제된 사용자 혹은 존재하지 않는 계정의 접근 시도입니다.");
            SecurityContextHolder.clearContext(); // 보안 컨텍스트 초기화 (미인증 처리)
        } catch (JwtException | IllegalArgumentException e) {
            // 토큰 자체가 위조되었거나 손상된 경우
            log.warn("유효하지 않은 JWT 토큰입니다: {}", e.getMessage());
            SecurityContextHolder.clearContext();
        } catch (Exception e) {
            // 기타 예상치 못한 예외 발생 시 서버 다운 방지
            log.error("인증 처리 중 예기치 않은 오류가 발생했습니다.", e);
            SecurityContextHolder.clearContext();
        }

        // 3. 다음 필터로 요청을 넘깁니다. (인증 실패 시 시큐리티 설정에 따라 401 등이 반환됩니다.)
        filterChain.doFilter(request, response);
    }

    /**
     * HTTP 요청 헤더에서 "Authorization: Bearer <Token>" 접두사를 제거하고 순수 토큰 값만 추출합니다.
     */
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7).trim();
        }
        return null;
    }
}