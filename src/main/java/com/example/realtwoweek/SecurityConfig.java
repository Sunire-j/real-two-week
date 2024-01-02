package com.example.realtwoweek;

import com.example.realtwoweek.config.auth.OAuthService;
import com.example.realtwoweek.config.jwt.JwtAuthenticationFilter;
import com.example.realtwoweek.config.jwt.JwtAuthenticationSuccessHandler;
import com.example.realtwoweek.config.jwt.JwtTokenProvider;
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
    private JwtTokenProvider jwtTokenProvider;

    public SecurityConfig(OAuthService oAuthService, MemberService memberService, PasswordEncoder passwordEncoder, JwtTokenProvider jwtTokenProvider) {
        this.oAuthService = oAuthService;
        this.memberService = memberService;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(jwtTokenProvider);



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
                .antMatchers("/", "/signin", "/signup/**", "/signup", "/LoginOk", "/goods/view","/purchase/confirm", "/js/**", "/css/**", "/img/**",
                        "/upload/**").permitAll()
                .antMatchers("/loginInfo", "/basket/**", "/mypage").hasAnyRole("USER", "ADMIN")
                .antMatchers("/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .loginPage("/signin")
                .loginProcessingUrl("/LoginOk")
                .successHandler(new JwtAuthenticationSuccessHandler(jwtTokenProvider))
                .usernameParameter("userid")
                .passwordParameter("userpwd")
                .defaultSuccessUrl("/")
                .and()
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        http.sessionManagement(httpSecuritySessionManagementConfigurer -> httpSecuritySessionManagementConfigurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

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