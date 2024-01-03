package com.example.realtwoweek.config.auth;

import com.example.realtwoweek.dto.TokenDto;
import com.example.realtwoweek.jwt.TokenProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final TokenProvider tokenProvider;

    public CustomAuthenticationSuccessHandler(TokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        TokenDto tokenDto = tokenProvider.generateTokenDto(authentication);
        System.out.println(authentication.toString());

//        // Access Token 쿠키 생성
//        Cookie accessTokenCookie = new Cookie("accessToken", tokenDto.getAccessToken());
//        accessTokenCookie.setHttpOnly(true);
//        accessTokenCookie.setPath("/");
//        long expireIn = (tokenDto.getAccessTokenExpiresIn() - System.currentTimeMillis()) / 1000;
//        accessTokenCookie.setMaxAge((int) expireIn);
//
//
//        System.out.println("여기는 오긴 함?");
//
//        // Refresh Token 쿠키 생성
//        Cookie refreshTokenCookie = new Cookie("refreshToken", tokenDto.getRefreshToken());
//        refreshTokenCookie.setHttpOnly(true);
//        refreshTokenCookie.setPath("/");
//        refreshTokenCookie.setMaxAge(7 * 24 * 60 * 60); // 7일



        String accessTokenCookieString = String.format(
                "accessToken=%s; Max-Age=%d; Expires=%s; Path=/; HttpOnly; Secure; SameSite=None",
                tokenDto.getAccessToken(),
                (int) ((tokenDto.getAccessTokenExpiresIn() - System.currentTimeMillis()) / 1000),
                new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US).format(new Date(tokenDto.getAccessTokenExpiresIn()))
        );

        // Refresh Token 쿠키 생성
        String refreshTokenCookieString = String.format(
                "refreshToken=%s; Max-Age=%d; Expires=%s; Path=/; HttpOnly; Secure; SameSite=None",
                tokenDto.getRefreshToken(),
                7 * 24 * 60 * 60,
                new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US).format(new Date(System.currentTimeMillis() + 7L * 24 * 60 * 60 * 1000))
        );

        // 쿠키를 응답 헤더에 추가
        response.addHeader("Set-Cookie", accessTokenCookieString);
        response.addHeader("Set-Cookie", refreshTokenCookieString);




//
//        // 쿠키를 응답에 추가
//        response.addCookie(accessTokenCookie);
//        response.addCookie(refreshTokenCookie);

        // OAuth2 로그인 성공 후 리디렉션
        response.sendRedirect("/"); // 적절한 리디렉션 URL로 변경
    }
}