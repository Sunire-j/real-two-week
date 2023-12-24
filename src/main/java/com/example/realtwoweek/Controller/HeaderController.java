package com.example.realtwoweek.Controller;

import com.example.realtwoweek.domain.Member;
import com.example.realtwoweek.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")

public class HeaderController {

    @Autowired
    private MemberRepository memberRepository;

    @GetMapping("/nickname")
    public String getNickname() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();

        if (principal instanceof OAuth2User) {  // OAuth2.0 로그인 사용자
            return (String) ((OAuth2User) principal).getAttributes().get("name");
        } else if (principal instanceof User) {  // 일반 로그인 사용자
            String username = ((User) principal).getUsername();
            Member member = memberRepository.findByEmailAndProvider(username, "none")
                    .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));
            return member.getName();
        }

        throw new IllegalArgumentException("사용자를 찾을 수 없습니다.");
    }
}
