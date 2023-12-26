package com.example.realtwoweek.Util;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;

import java.util.Map;

public class AuthenticationUtil {

    public static UserIdentity getUserIdentity(Authentication authentication) {
        String u_email = null;
        String provider = null;
        if(authentication instanceof OAuth2AuthenticationToken) {
            OAuth2AuthenticationToken authToken = (OAuth2AuthenticationToken) authentication;
            Map<String, Object> attributes = authToken.getPrincipal().getAttributes();
            provider = authToken.getAuthorizedClientRegistrationId();
            if ("naver".equals(provider)) {
                Map<String, Object> response = (Map<String, Object>) attributes.get("response");
                u_email = (String) response.get("email");
            } else {
                u_email = (String) attributes.getOrDefault("email", "default_email");
            }
        } else if(authentication instanceof UsernamePasswordAuthenticationToken) {
            UsernamePasswordAuthenticationToken authToken = (UsernamePasswordAuthenticationToken) authentication;
            User principal = (User) authToken.getPrincipal();
            u_email = principal.getUsername();
            provider = "none"; // local 로그인이라고 가정
        }
        return new UserIdentity(u_email, provider);
    }
}