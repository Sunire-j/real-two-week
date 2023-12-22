package com.example.realtwoweek.Mapper;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BasketMapper {
    int addBasket(int id, int num, Long userid);
    int checkBasket(int id, Long userid);
    int increaseAmount(int id, int num, Long userid);
}
