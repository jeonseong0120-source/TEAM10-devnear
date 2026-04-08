package com.devnear.web.domain.user;

import com.devnear.web.domain.common.BaseTimeEntity;
import com.devnear.web.domain.enums.Role;
import com.devnear.web.domain.enums.UserStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * [보고] 애플리케이션의 핵심 도메인인 회원(User) 엔티티.
 * Spring Security의 UserDetails 인터페이스를 구현하여 인증 및 인가 주체로 사용됨.
 */
@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseTimeEntity implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, unique = true, length = 50)
    private String nickname;

    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    @Column(name = "profile_image_url", length = 500)
    private String profileImageUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UserStatus status;

    @Builder
    public User(String email, String password, String name, String nickname, String phoneNumber, String profileImageUrl, Role role) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.nickname = nickname;
        this.phoneNumber = phoneNumber;
        this.profileImageUrl = profileImageUrl;
        this.role = role;
        // [보고] 신규 유저 생성 시 기본 상태를 ACTIVE로 강제 지정함.
        this.status = UserStatus.ACTIVE;
    }

    // ================= [보고] UserDetails 필수 구현 영역 =================

    /**
     * [보고] 사용자의 권한(Role) 목록을 반환함.
     * Spring Security의 권한 검증(hasRole)이 정상 작동하기 위해 "ROLE_" 접두사를 포함하여 반환 조치함.
     */
    @Override
    @Transient
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + this.role.name()));
    }

    /**
     * [보고] 사용자의 고유 식별자(Username)를 반환함.
     * 본 프로젝트에서는 이메일(email) 필드를 고유 식별자로 채택함.
     */
    @Override
    public String getUsername() {
        return this.email;
    }

    /**
     * [보고] 사용자의 암호화된 비밀번호를 반환함.
     */
    @Override
    public String getPassword() {
        return this.password;
    }

    /**
     * [보고] 사용자 계정의 만료 여부를 반환함.
     * 현행 비즈니스 로직상 계정 만료 정책이 없으므로 항상 true를 반환함.
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * [보고] 사용자 계정의 잠금 여부를 반환함.
     * 추후 정지(BANNED) 상태 관리 시 본 메서드를 연동하여 확장할 수 있음.
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * [보고] 사용자 자격 증명(비밀번호)의 만료 여부를 반환함.
     * 항상 true를 반환하여 만료되지 않았음을 명시함.
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * [보고] 사용자 계정의 활성화 여부를 반환함.
     * 회원 상태(status) 필드가 ACTIVE(활성) 상태인 경우에만 인증을 허용하도록 정합성을 확보함.
     */
    @Override
    public boolean isEnabled() {
        return this.status == UserStatus.ACTIVE;
    }
}
