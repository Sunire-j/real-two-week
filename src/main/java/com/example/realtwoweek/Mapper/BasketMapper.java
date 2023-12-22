package com.example.realtwoweek.Mapper;

import com.example.realtwoweek.vo.BasketVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface BasketMapper {
    int addBasket(int id, int num, Long userid);
    int checkBasket(int id, Long userid);
    int increaseAmount(int id, int num, Long userid);

    List<BasketVO> getAllBasket(Long userid);

    int SetAmount(Long userid, int id, int num);
    int dropItem(Long userid, int id);
}
