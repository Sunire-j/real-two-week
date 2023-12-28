package com.example.realtwoweek.Mapper;

import com.example.realtwoweek.vo.*;
import org.apache.ibatis.annotations.Mapper;
import org.aspectj.weaver.ast.Or;

import java.util.List;

@Mapper
public interface AdminMapper {
    void deleteItem(int iid);

    void InsertItem(ItemVO ivo);

    void UpdateItem(ItemVO ivo);

    int getOrderTotalRecord();

    List<OrderVO> getOrderList(PagingVO pvo);

    int orderNextStep(String orderNum);

    OrderVO getOrderDetail(String no);
    int getOrderCompleteTotalRecord();
    List<OrderVO> getOrderListComplete(PagingVO pvo);
    List<MethodDetailVO> getAllMethodDetail(PagingVO pvo);

    int getMethodDetailCount();

    void addMethodDetail(MethodDetailVO mdvo);

    int deleteMethodDetail(int no);
    MethodDetailVO getMethodDetailDetail(int id);

    void editMethodDetail(MethodDetailVO mdvo);

    int getTotalUserCount();

    List<MemberVO> getUser(PagingVO pvo);

    int getTotalBuy(Long uid);

    int deleteMember(Long uid);

}
