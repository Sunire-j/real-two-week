package com.example.realtwoweek.Mapper;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MemberMapper {
    Long getUserid(String provider, String email);
}