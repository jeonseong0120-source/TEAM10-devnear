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
                // SecurityConfig.java 의 authorizeHttpRequests 부분 수정

                .authorizeHttpRequests(auth -> auth
                        // 0. [공통] 누구나 접근 가능
                        .requestMatchers("/api/auth/**", "/swagger-ui/**", "/v3/api-docs/**").permitAll()

                        // 1. [조회] GET 요청은 비로그인도 가능 (명세서 준수)
                        .requestMatchers(HttpMethod.GET, "/api/freelancers/**", "/api/projects/**", "/api/portfolios/**").permitAll()

                        // 2. [인증] 내 정보 관련 (로그인 필수)
                        .requestMatchers("/api/users/me").authenticated()

                        // 3. [권한] 프리랜서 전용 구역 (중요: /test 포함 모든 하위 경로 잠금)
                        .requestMatchers(HttpMethod.POST, "/api/portfolios", "/api/applications").hasAnyRole("FREELANCER", "BOTH")
                        .requestMatchers(HttpMethod.DELETE, "/api/portfolios/**").hasAnyRole("FREELANCER", "BOTH")
                        .requestMatchers(HttpMethod.PATCH, "/api/freelancers/status").hasAnyRole("FREELANCER", "BOTH")
                        // [추가] 프리랜서 도메인 전체에 대한 계급 검사 (이게 있어야 CLIENT를 입구 컷 함)
                        .requestMatchers("/api/freelancer/**").hasAnyRole("FREELANCER", "BOTH")

                        // 4. [권한] 클라이언트 전용 구역 (리뷰 피드백 반영: HttpMethod.PATCH 명시)
                        .requestMatchers(HttpMethod.POST, "/api/projects").hasAnyRole("CLIENT", "BOTH")
                        // [수정] 지원 수락(PATCH) 메서드를 명시하여 모호성 제거
                        .requestMatchers(HttpMethod.PATCH, "/api/projects/*/applications", "/api/applications/*/accept")
                        .hasAnyRole("CLIENT", "BOTH")
                        // [추가] 클라이언트 도메인 전체에 대한 계급 검사
                        .requestMatchers("/api/client/**").hasAnyRole("CLIENT", "BOTH")

                        // 5. [나머지] 위 규칙에 해당 안 되는 모든 요청은 로그인 필수
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
