package com.devnear.web.service.skill;

import com.devnear.web.domain.skill.Skill;
import com.devnear.web.domain.skill.SkillRepository;
import com.devnear.web.dto.skill.SkillResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SkillService {

    private final SkillRepository skillRepository;

    // 전체 스킬 목록 조회
    public List<SkillResponse> getAllSkills() {
        return skillRepository.findAll()
                .stream()
                .map(SkillResponse::from)
                .collect(Collectors.toList());
    }

    // 기본 제공 스킬 목록 조회 (is_default = true)
    public List<SkillResponse> getDefaultSkills() {
        return skillRepository.findByIsDefaultTrue()
                .stream()
                .map(SkillResponse::from)
                .collect(Collectors.toList());
    }

    // 카테고리별 스킬 조회
    public List<SkillResponse> getSkillsByCategory(String category) {
        return skillRepository.findByCategory(category)
                .stream()
                .map(SkillResponse::from)
                .collect(Collectors.toList());
    }

    // 스킬 이름 검색 (통합 검색 - 스킬 태그 #)
    public List<SkillResponse> searchSkills(String keyword) {
        return skillRepository.findByNameContainingIgnoreCase(keyword)
                .stream()
                .map(SkillResponse::from)
                .collect(Collectors.toList());
    }
}
