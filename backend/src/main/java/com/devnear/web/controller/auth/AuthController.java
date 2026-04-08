package com.devnear.web.controller.auth;

import com.devnear.web.dto.user.TokenResponse;
import com.devnear.web.dto.user.UserLoginRequest;
import com.devnear.web.dto.user.UserRegisterRequest;
import com.devnear.web.service.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Auth", description = "인증(회원가입/로그인) 관련 API")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    // [보고] 기존 UserController에 있던 로그인/회원가입 로직을 AuthController로 분리함.
    // UserService를 주입받아 실제 비즈니스 로직을 처리함.
    private final UserService userService;

    @Operation(summary = "회원가입", description = "이메일, 비밀번호 등을 입력받아 회원가입을 진행합니다.")
    @PostMapping("/signup") // 명세서 요구사항에 맞춰 /register 에서 /signup 으로 변경
    public ResponseEntity<Long> signup(@RequestBody UserRegisterRequest request) {
        Long userId = userService.register(request);
        return ResponseEntity.ok(userId);
    }

    @Operation(summary = "로그인", description = "이메일과 비밀번호로 로그인을 진행하고 토큰을 발급합니다.")
    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody UserLoginRequest request) {
        TokenResponse response = userService.login(request);
        return ResponseEntity.ok(response);
    }
}
