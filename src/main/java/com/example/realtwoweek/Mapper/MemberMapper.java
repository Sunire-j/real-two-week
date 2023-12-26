package com.example.realtwoweek.Mapper;

import com.example.realtwoweek.vo.OrderVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MemberMapper {
    Long getUserid(String provider, String email);
    String getUsername(Long userid);

    int CheckEmail(String email);

    String getPhonenum(Long userid);

    int getZipno(Long uid);

    String getAddress1(Long uid);
    String getAddress2(Long uid);
    String getAddress3(Long uid);

    List<OrderVO> getOrderList(Long uid);
}
