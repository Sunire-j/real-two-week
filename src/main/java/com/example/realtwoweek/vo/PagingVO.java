package com.example.realtwoweek.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PagingVO {
    private int onePageRecord = 5;
    private int onePageCount = 5;
    private int page = 1;
    private int totalRecord;
    private int totalPage;
    private int offsetPoint = (page - 1) * onePageRecord;
    private int startPage = 1;
    private int category;


    public void setPage(int page) {
        this.page = page;
        offsetPoint = (page - 1) * onePageRecord;
        startPage = (page - 1) / onePageCount * onePageCount + 1;
    }

    public void setTotalRecord(int totalRecord) {
        this.totalRecord = totalRecord;
        this.setTotalPage((int) Math.ceil(totalRecord / (double) onePageRecord));
    }

}
