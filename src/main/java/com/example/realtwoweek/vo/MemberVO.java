package com.example.realtwoweek.vo;

import lombok.Data;

@Data
public class MemberVO {
    private Long member_id;
    private String name;
    private String email;
    private String provider;
    private String nickname;
    private boolean isadmin;
    private String password;
    private String phone;
    private int zipno;
    private String address1;
    private String address2;
    private String address3;
}
