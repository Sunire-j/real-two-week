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
                .logout().logoutSuccessUrl("/")
                .and()
                .oauth2Login()
                .defaultSuccessUrl("/", true)
                .userInfoEndpoint()
                .userService(oAuthService)
                .and()
                .and()
                .authorizeRequests()
                .antMatchers("/", "/signin").permitAll()
                .antMatchers("/loginInfo", "/basket/**").hasAnyRole("USER", "ADMIN")
                .antMatchers("/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .loginPage("/signin");

        return http.build();
    }
}