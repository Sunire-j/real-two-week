package com.example.realtwoweek;

import com.example.realtwoweek.config.auth.OAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
public class SecurityConfig {

    private final OAuthService oAuthService;

    public SecurityConfig(OAuthService oAuthService) {
        this.oAuthService = oAuthService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/css/**", "/img/**", "/upload/**").permitAll()
                .and()
                .headers().frameOptions().disable()
                .and()
                .logout().logoutSuccessUrl("/") // logout 요청시 홈으로 이동 - 기본 logout url = "/logout"
                .and()
                .oauth2Login() // OAuth2 로그인 설정 시작점
                .defaultSuccessUrl("/oauth/loginInfo", true) // OAuth2 성공시 redirect
                .userInfoEndpoint() // OAuth2 로그인 성공 이후 사용자 정보를 가져올 때 설정 담당
                .userService(oAuthService) // OAuth2 로그인 성공 시, 작업을 진행할 MemberService
                .and()
                .and()
                .authorizeRequests() // URL 별 접근 권한 설정
                .antMatchers("/", "/signin").permitAll() // 모든 권한에서 접근 가능
                .antMatchers("/loginInfo").hasAnyRole("USER", "ADMIN") // USER 권한에서 접근 가능
                .antMatchers("/admin/**").hasRole("ADMIN") // 관리자 권한에서 접근 가능
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .loginPage("/signin");

        return http.build();
    }
}