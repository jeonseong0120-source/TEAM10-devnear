package com.devnear.web.controller;

import com.devnear.web.domain.user.User; // 마스터의 User 엔티티
import org.springframework.security.core.annotation.AuthenticationPrincipal; // 추가
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.Map;

@RestController
public class TestController {

    @GetMapping("/api/hello")
    public String hello(@AuthenticationPrincipal User user) { // 매개변수 추가
        if (user == null) return "로그인이 필요한 서비스입니다.";
        return user.getNickname() + " 서버가 아주 건강하게 작동 중";
    }

    @GetMapping("/api/test-data")
    public Map<String, Object> testData(@AuthenticationPrincipal User user) {
        Map<String, Object> data = new HashMap<>();
        data.put("status", "success");
        data.put("loginUserEmail", user.getEmail()); // 로그인한 유저 이메일 노출 테스트
        data.put("message", "보안 확인");
        return data;
    }
}