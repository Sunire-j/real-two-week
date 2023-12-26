package com.example.realtwoweek.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;

public class DelegatingUserDetailsService implements UserDetailsService {
    private final List<UserDetailsService> userDetailServices;

    public DelegatingUserDetailsService(List<UserDetailsService> userDetailServices) {
        this.userDetailServices = userDetailServices;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        for (UserDetailsService userDetailsService : userDetailServices) {
            try {
                return userDetailsService.loadUserByUsername(username);
            } catch (UsernameNotFoundException ignored) {
            }
        }
        throw new UsernameNotFoundException("User not found: " + username);
    }
}