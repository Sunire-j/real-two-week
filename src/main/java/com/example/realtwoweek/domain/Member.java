package com.example.realtwoweek.domain;

import lombok.*;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicUpdate
@Entity
@Data
@Table(name = "member")
public class Member {
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

    @Builder
    public Member(String password, Long id, String name, String email, String provider, String nickname, boolean isAdmin) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.provider = provider;
        this.nickname = nickname;
        this.isadmin = isAdmin;
        this.password=password;
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