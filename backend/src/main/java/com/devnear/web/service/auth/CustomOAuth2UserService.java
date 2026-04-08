package com.devnear.web.service.auth;

import com.devnear.web.domain.enums.Role;
import com.devnear.web.domain.user.User;
import com.devnear.web.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // 1. 구글로부터 유저 기본 정보(Attributes)를 가져옴
        OAuth2User oAuth2User = super.loadUser(userRequest);

        // 2. 현재 로그인 진행 중인 서비스 구분 (google, kakao 등)
        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        // 3. OAuth2 로그인 진행 시 키가 되는 필드값 (구글은 기본적으로 "sub"가 PK 역할)
        String userNameAttributeName = userRequest.getClientRegistration()
                .getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();

        // 4. 구글이 준 유저 정보를 우리 DB에 맞게 저장하거나 업데이트함
        // CustomOAuth2UserService.java의 loadUser 메서드 하단부 수정
        Map<String, Object> attributes = new HashMap<>(oAuth2User.getAttributes()); // 수정 가능하게 복사
        User user = saveOrUpdate(registrationId, attributes);

// [보고] 핸들러에서 꺼내 쓸 수 있게 DB PK(id)를 attributes에 추가함
        attributes.put("id", user.getId());

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("ROLE_" + user.getRole().name())),
                attributes,
                userNameAttributeName
        );
    }

    private User saveOrUpdate(String registrationId, Map<String, Object> attributes) {
        String email = (String) attributes.get("email");
        String name = (String) attributes.get("name");
        String picture = (String) attributes.get("picture");
        String sub = (String) attributes.get("sub");

        // [보고] 이메일로 기존 유저인지 확인하고, 이름이나 사진이 바뀌었으면 업데이트함
        return userRepository.findByEmail(email)
                .map(entity -> entity.update(name, picture))
                .orElseGet(() -> userRepository.save(User.builder()
                        .email(email)
                        .name(name)
                        .nickname(name + "_" + sub.substring(0, 5))
                        .profileImageUrl(picture)
                        .role(Role.CLIENT)
                        .provider(registrationId)
                        .providerId(sub)
                        .build()));
    }
}