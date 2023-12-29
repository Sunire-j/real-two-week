package com.example.realtwoweek;

import com.example.realtwoweek.config.auth.OAuthService;
import com.example.realtwoweek.service.DelegatingUserDetailsService;
import com.example.realtwoweek.service.MemberService;
import com.example.realtwoweek.service.NormalMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Arrays;

@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final OAuthService oAuthService;
    private final MemberService memberService;
    @Autowired
    private NormalMemberService normalMemberService;

    public SecurityConfig(OAuthService oAuthService, MemberService memberService, PasswordEncoder passwordEncoder) {
        this.oAuthService = oAuthService;
        this.memberService = memberService;
        this.passwordEncoder = passwordEncoder;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .cors().disable()
                .csrf().disable()
                .headers().frameOptions().disable()
                .and()
                .logout().logoutSuccessUrl("/")
                .and()
                .oauth2Login()
                .defaultSuccessUrl("/", true)
                .userInfoEndpoint()
                .userService(oAuthService)
                .and()
                .and()
                .authorizeRequests()
                .antMatchers("/", "/signin", "/signup/**", "/signup", "/LoginOk", "/goods/view","/**","/purchase/confirm").permitAll()
                .antMatchers("/loginInfo", "/basket/**", "/mypage").hasAnyRole("USER", "ADMIN")
                .antMatchers("/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .loginPage("/signin")
                .loginProcessingUrl("/LoginOk")
                .usernameParameter("userid")
                .passwordParameter("userpwd")
                .defaultSuccessUrl("/")
                .and();

        return http.build();
    }

    @Autowired
    public void configureAuthentication(AuthenticationManagerBuilder auth) throws Exception {
        DelegatingUserDetailsService delegatingUserDetailsService =
                new DelegatingUserDetailsService(Arrays.asList(normalMemberService, memberService));
        auth
                .userDetailsService(delegatingUserDetailsService)
                .passwordEncoder(passwordEncoder);
    }

}