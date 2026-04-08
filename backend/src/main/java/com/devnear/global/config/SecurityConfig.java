package com.devnear.global.config;

import com.devnear.global.auth.JwtAuthenticationFilter;
import com.devnear.global.auth.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.http.HttpMethod;

/**
 * [보고] 애플리케이션 보안 계층 설정을 위한 전역 Configuration 클래스.
 * JWT 기반 인증 로직 및 엔드포인트 접근 권한을 관리함.
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    // [보고] JWT 토큰 발급 및 검증을 담당하는 Provider 빈 주입.
    private final JwtTokenProvider jwtTokenProvider;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // [보고] JWT 방식을 사용하므로 기본 CSRF 방어 기능은 비활성화 조치함.
                .csrf(AbstractHttpConfigurer::disable)

                // [보고] 세션 정책 설정. JWT 인증 방식을 채택하였으므로, 
                // 서버 측에서 세션을 생성하지 않도록 STATELESS 정책을 강제함.
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // [보고] 엔드포인트별 인가(Authorization) 정책 정의.
                .authorizeHttpRequests(auth -> auth
                        // 0. [필수 복구] 회원가입, 로그인 및 Swagger API는 누구나 접근 가능해야 함 (이전 설정 복구)
                        .requestMatchers("/api/users/register", "/api/users/login", "/swagger-ui/**", "/v3/api-docs/**").permitAll()

                        // 1. [인증 ❌] 명세서상 누구나 볼 수 있는 '조회' API들
                        .requestMatchers(HttpMethod.GET, "/api/freelancers/**", "/api/projects/**", "/api/portfolios/**").permitAll()

                        // 2. [인증 ⭕] 명세서상 반드시 로그인이 필요한 '공통' API
                        .requestMatchers("/api/users/me", "/api/auth/logout").authenticated()

                        // 3. [권한 ⭕] 프리랜서 전용 (등록, 수정, 삭제, 지원)
                        // 명세서: 포트폴리오 등록(POST), 삭제(DELETE), 프로젝트 지원(POST)
                        .requestMatchers(HttpMethod.POST, "/api/portfolios", "/api/applications").hasAnyRole("FREELANCER", "BOTH")
                        .requestMatchers(HttpMethod.DELETE, "/api/portfolios/**").hasAnyRole("FREELANCER", "BOTH")
                        .requestMatchers(HttpMethod.PATCH, "/api/freelancers/status").hasAnyRole("FREELANCER", "BOTH")

                        // 4. [권한 ⭕] 클라이언트 전용 (등록, 관리)
                        // 명세서: 프로젝트 등록(POST), 지원 수락(PATCH)
                        .requestMatchers(HttpMethod.POST, "/api/projects").hasAnyRole("CLIENT", "BOTH")
                        .requestMatchers("/api/projects/*/applications", "/api/applications/*/accept").hasAnyRole("CLIENT", "BOTH")

                        // 5. 나머지는 일단 로그인 필수
                        .anyRequest().authenticated()
                )
                // [보고] 커스텀 JWT 인증 필터를 UsernamePasswordAuthenticationFilter 이전에 배치하여,
                // 기본 로그인 처리 전 토큰 유효성 검증을 선행하도록 구성함.
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider),
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * [보고] 사용자의 비밀번호 단방향 암호화를 위한 PasswordEncoder 빈 등록.
     * BCrypt 해싱 알고리즘을 사용함.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
