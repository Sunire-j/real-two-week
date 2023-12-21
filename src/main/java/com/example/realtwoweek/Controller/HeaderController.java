package com.example.realtwoweek.Controller;

import com.example.realtwoweek.domain.Member;
import com.example.realtwoweek.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
//    @GetMapping("/nickname")
//    public ResponseEntity<?> getNickname(OAuth2AuthenticationToken authentication) {
//        if (authentication != null) {
//            String name = "";
//            Map<String, Object> attributes = authentication.getPrincipal().getAttributes();
//            String provider = authentication.getAuthorizedClientRegistrationId();
//
//            if ("google".equals(provider)) {
//                name = (String) attributes.get("name");
//            } else if ("naver".equals(provider)) {
//                Map<String, Object> response = (Map<String, Object>) attributes.get("response");
//                name = (String) response.get("name");
//            }
//
//            Optional<Member> optionalMember = memberRepository.findByEmailAndProvider(authentication.getName(), provider);
//            if(optionalMember.isPresent()){
//                Member member = optionalMember.get();
//                member.setNickname(name);  // Set the nickname to the retrieved name
//                System.out.println();
//                return ResponseEntity.ok().body(member.getNickname());
//            }
//        }
//        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
//    }

    @GetMapping("/nickname")
    public String getNickname() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        OAuth2User principal = (OAuth2User) authentication.getPrincipal();
        String name = (String) principal.getAttributes().get("name");
        System.out.println(name);
        return name;

    }
}
