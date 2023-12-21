package com.example.realtwoweek.vo;

import lombok.Data;

@Data
public class ItemVO {
    private int items_id;
    private String name;
    private int price;
    private int delivery;
    private String img;
    private String seller;
    private String category;
    private int userprice;
    private String detail;
}
