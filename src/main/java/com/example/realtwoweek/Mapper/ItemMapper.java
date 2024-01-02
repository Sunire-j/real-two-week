package com.example.realtwoweek.Mapper;


import com.example.realtwoweek.vo.ItemVO;
import com.example.realtwoweek.vo.PagingVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ItemMapper {
    List<ItemVO> getNewItems();
    List<ItemVO> getRecommendItems();

    ItemVO getItemDetail(int no);
    int getNeedOptionCount(int no);

    List<ItemVO> getGoodsList(int category);

    List<ItemVO> getAllItems();

    List<ItemVO> getAllItemsPaging(PagingVO pvo);

    List<ItemVO> getGoodsListPaging(PagingVO pvo);

    String getOrderNum(int orderid);

    int getOrderId(String ordernum);

}
