package com.example.realtwoweek.config.auth;

import com.example.realtwoweek.domain.Member;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberProfile {
    private String name;
    private String email;
    private String provider;
    private String nickname;

    public Member toMember() {
        return Member.builder()
                .name(name)
                .email(email)
                .provider(provider)
                .isAdmin(false) // isAdmin 필드를 false로 설정
                .build();
    }

}