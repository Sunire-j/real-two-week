package com.example.realtwoweek.domain;

import lombok.*;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicUpdate
@Entity
@Data
@Table(name = "member")
public class Member implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;
    @Column(name = "name", nullable = true)
    private String name;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "provider", nullable = false)
    private String provider;

    @Setter
    @Column(name = "nickname", nullable = true, unique = true)
    private String nickname;

    @Column(name = "isadmin", nullable = false)
    private boolean isadmin = false;

    @Column(name = "password", nullable = true)
    private String password;

    @Column(name = "phone", nullable = true, length = 255)
    private String phone;

    @Column(name = "zipno", nullable = true)
    private Integer zipno=0;

    @Column(name="address1")
    private String address1;

    @Column(name="address2")
    private String address2;

    @Column(name="address3")
    private String address3;

    @Transient
    private List<GrantedAuthority> authorities;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (authorities == null) {
            authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
            if (this.isAdmin()) {
                authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
            }
        }
        return authorities;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Builder
    public Member(String address3,Integer zipno,String address2,String address1,String password, Long id, String name, String email, String provider, String nickname, boolean isAdmin) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.provider = provider;
        this.nickname = nickname;
        this.isadmin = isAdmin;
        this.password=password;
        this.zipno=zipno;
        this.address2=address2;
        this.address1=address1;
        this.address3=address3;
    }

    public void encodePassword(PasswordEncoder passwordEncoder) {
        this.password = passwordEncoder.encode(this.password);
    }


    public Member update(String name, String email) {
        this.name = name;
        this.email = email;
        return this;
    }

    public boolean isAdmin() {
        return this.isadmin;
    }
}