package com.example.realtwoweek.Mapper;

import com.example.realtwoweek.vo.ItemVO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AdminMapper {
    void deleteItem(int iid);

    void InsertItem(ItemVO ivo);

    void UpdateItem(ItemVO ivo);


}
