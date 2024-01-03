package com.example.realtwoweek;

import com.example.realtwoweek.config.auth.CustomAuthenticationSuccessHandler;
import com.example.realtwoweek.config.auth.OAuthService;
import com.example.realtwoweek.jwt.JwtAccessDeniedHandler;
import com.example.realtwoweek.jwt.JwtAuthenticationEntryPoint;
import com.example.realtwoweek.jwt.JwtSecurityConfig;
import com.example.realtwoweek.jwt.TokenProvider;
import com.example.realtwoweek.service.DelegatingUserDetailsService;
import com.example.realtwoweek.service.MemberService;
import com.example.realtwoweek.service.NormalMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Arrays;

@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final OAuthService oAuthService;
    private final MemberService memberService;
    @Autowired
    private NormalMemberService normalMemberService;
    private CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;


    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private final TokenProvider tokenProvider;

    public SecurityConfig(OAuthService oAuthService, MemberService memberService, PasswordEncoder passwordEncoder, CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler, JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint, JwtAccessDeniedHandler jwtAccessDeniedHandler, TokenProvider tokenProvider) {
        this.oAuthService = oAuthService;
        this.memberService = memberService;
        this.passwordEncoder = passwordEncoder;
        this.customAuthenticationSuccessHandler = customAuthenticationSuccessHandler;
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
        this.jwtAccessDeniedHandler = jwtAccessDeniedHandler;
        this.tokenProvider = tokenProvider;
    }



    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {




        http
                .exceptionHandling()
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                .accessDeniedHandler(jwtAccessDeniedHandler)
                .and()
                .cors().disable()
                .csrf().disable()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .headers().frameOptions().disable()
                .and()
                .logout().logoutSuccessUrl("/")
                .and()
                .oauth2Login()
                .successHandler(customAuthenticationSuccessHandler)
                //.defaultSuccessUrl("/", true)
                .userInfoEndpoint()
                .userService(oAuthService)
                .and()
                .and()
                .authorizeRequests()
                .antMatchers("/", "/signin", "/signup/**", "/signup", "/LoginOk", "/goods/view","/purchase/confirm", "/js/**", "/css/**", "/img/**",
                        "/upload/**", "/auth/**", "/error").permitAll()
                .antMatchers("/loginInfo", "/basket/**", "/mypage", "/api/nickname", "/mypage/**").hasAnyRole("USER", "ADMIN")
                .antMatchers("/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .loginPage("/signin")
                .loginProcessingUrl("/LoginOk")
                .usernameParameter("userid")
                .passwordParameter("userpwd")
                .defaultSuccessUrl("/")
                .and()
                .apply(new JwtSecurityConfig(tokenProvider));

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