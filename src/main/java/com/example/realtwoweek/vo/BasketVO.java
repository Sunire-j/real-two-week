package com.example.realtwoweek.vo;

import lombok.Data;

@Data
public class BasketVO {
    private Long member_id;
    private int items_id;
    private int amount;
    private String img;
    private String name;
    private int price;
    private int delivery;
}
