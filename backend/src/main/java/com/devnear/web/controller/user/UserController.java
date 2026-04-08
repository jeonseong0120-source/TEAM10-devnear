package com.devnear.web.controller.user;

import com.devnear.web.service.user.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "User", description = "회원 관련 API")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // [보고] 인증(로그인, 회원가입) 기능은 AuthController(/api/auth)로 바꿨습니다!
    // 저희 팀 API 명세서 보고 수정했어용
    // 추후 회원 정보 조회, 수정 기능(예: /api/users/me)을 이 곳에 추가하시면 됩니다.

}
