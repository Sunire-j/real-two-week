package com.example.realtwoweek.Mapper;

import com.example.realtwoweek.vo.*;
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
    List<MethodVO> getAllMethod();
    List<MethodDetailVO> getAllMethodDetail();

    int addNewOrder(OrderVO ovo);

    int addItemToOrder(int itemid, int amount, int orderid);

    List<BasketVO> getOrderItemList(int orderid);

    void setPrice(int orderid, int sum, int maxDeliveryValue);

    OrderVO getOrder(int orderid);

    void basketEmpty(Long userid);
    String getMethodName(int method);

    String getMethodDetailName(int methodDetail);
}
