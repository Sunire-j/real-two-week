package com.example.realtwoweek.Mapper;

import com.example.realtwoweek.vo.ItemVO;
import com.example.realtwoweek.vo.OrderVO;
import com.example.realtwoweek.vo.PagingVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AdminMapper {
    void deleteItem(int iid);

    void InsertItem(ItemVO ivo);

    void UpdateItem(ItemVO ivo);

    int getOrderTotalRecord();

    List<OrderVO> getOrderList(PagingVO pvo);

}
