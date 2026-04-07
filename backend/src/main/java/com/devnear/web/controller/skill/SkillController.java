package com.devnear.web.controller.skill;

import com.devnear.web.dto.skill.SkillResponse;
import com.devnear.web.service.skill.SkillService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/skills")
@RequiredArgsConstructor
public class SkillController {

    private final SkillService skillService;

    // 전체 스킬 목록 조회
    @GetMapping
    public ResponseEntity<List<SkillResponse>> getAllSkills() {
        return ResponseEntity.ok(skillService.getAllSkills());
    }

    // 기본 제공 스킬 목록 조회
    @GetMapping("/default")
    public ResponseEntity<List<SkillResponse>> getDefaultSkills() {
        return ResponseEntity.ok(skillService.getDefaultSkills());
    }

    // 카테고리별 스킬 조회
    @GetMapping("/category")
    public ResponseEntity<List<SkillResponse>> getSkillsByCategory(
            @RequestParam String name) {
        return ResponseEntity.ok(skillService.getSkillsByCategory(name));
    }

    // 스킬 이름 검색 (통합 검색 - 스킬 태그 #)
    @GetMapping("/search")
    public ResponseEntity<List<SkillResponse>> searchSkills(
            @RequestParam String keyword) {
        return ResponseEntity.ok(skillService.searchSkills(keyword));
    }
}
