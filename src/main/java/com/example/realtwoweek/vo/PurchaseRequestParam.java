package com.example.realtwoweek.vo;

import lombok.Data;

@Data
public class PurchaseRequestParam {
    private String userId;
    private String itemName;
    private String itemCode;
    private String amount;
    private String userName;
    private String userEmail;
}
