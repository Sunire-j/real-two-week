package com.example.realtwoweek.jwt;

import com.example.realtwoweek.config.auth.MemberProfile;
import com.example.realtwoweek.config.auth.OAuthAttributes;
import com.example.realtwoweek.domain.Member;
import com.example.realtwoweek.dto.TokenDto;
import com.example.realtwoweek.repository.MemberRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class TokenProvider {

    private static final String AUTHORITIES_KEY = "auth";
    private static final String BEARER_TYPE = "Bearer";
    private static final long ACCESS_TOKEN_EXPIRE_TIME = 1000 * 60 * 30;            // 30분
    private static final long REFRESH_TOKEN_EXPIRE_TIME = 1000 * 60 * 60 * 24 * 7;  // 7일

    private final Key key;
    private MemberRepository memberRepository;

    public TokenProvider(@Value("${jwt.secret}") String secretKey, MemberRepository memberRepository) {
        System.out.println("TokenProvider 생성자 매개변수 secretKey : " + secretKey);
        this.memberRepository = memberRepository;
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public TokenDto generateTokenDto(Authentication authentication) {

        System.out.println(authentication);
        System.out.println("여기바로위임");
        // 권한들 가져오기
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        long now = (new Date()).getTime();

        // Provider 정보 가져오기
        String provider = "none";
        String email = authentication.getName();
        if (authentication instanceof OAuth2AuthenticationToken) {
            OAuth2AuthenticationToken oauth2Token = (OAuth2AuthenticationToken) authentication;
            provider = oauth2Token.getAuthorizedClientRegistrationId();
            Map<String, Object> attributes = oauth2Token.getPrincipal().getAttributes();

            // OAuthAttributes를 사용하여 이메일 추출
            MemberProfile memberProfile = OAuthAttributes.extract(provider, attributes);
            email = memberProfile.getEmail(); // 이메일을 추출하여 사용
        }

        // Access Token 생성
        Date accessTokenExpiresIn = new Date(now + ACCESS_TOKEN_EXPIRE_TIME);
        String accessToken = Jwts.builder()
                .setSubject(email)       // payload "sub": "name"
                .claim(AUTHORITIES_KEY, authorities)
                .claim("provider", provider) // payload "auth": "ROLE_USER"
                .setExpiration(accessTokenExpiresIn)        // payload "exp": 151621022 (ex)
                .signWith(key, SignatureAlgorithm.HS512)    // header "alg": "HS512"
                .compact();

        // Refresh Token 생성
        String refreshToken = Jwts.builder()
                .setExpiration(new Date(now + REFRESH_TOKEN_EXPIRE_TIME))
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();


        return TokenDto.builder()
                .grantType(BEARER_TYPE)
                .accessToken(accessToken)
                .accessTokenExpiresIn(accessTokenExpiresIn.getTime())
                .refreshToken(refreshToken)
                .build();
    }

    public Authentication getAuthentication(String accessToken) {
        // 토큰 복호화
        Claims claims = parseClaims(accessToken);

        // 클레임에서 유저 정보 가져오기
        String email = claims.getSubject();
        String provider = claims.get("provider", String.class); // provider 정보를 가져옵니다.

        Member member = memberRepository.findByEmailAndProvider(email, provider)
                .orElseThrow(() -> new UsernameNotFoundException("해당하는 유저가 없습니다."));

        // 권한 설정
        List<GrantedAuthority> authorities = new ArrayList<>();
        if (member.isAdmin()) {
            authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        } else {
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        }

        return new UsernamePasswordAuthenticationToken(member, "", authorities);
    }


    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info("잘못된 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {
            log.info("만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            log.info("지원되지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException e) {
            log.info("JWT 토큰이 잘못되었습니다.");
        }
        return false;
    }

    private Claims parseClaims(String accessToken) {
        try {
            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken).getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }
}
