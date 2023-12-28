package com.example.realtwoweek.vo;

import lombok.Data;

@Data
public class PurchaseReturnParam {
    private String SERVICE_ID;
    private String SERVICE_CODE;
    private String ORDER_ID;
    private String ORDER_DATE;
    private String RESPONSE_CODE;
    private String RESPONSE_MESSAGE;
    private String DETAIL_RESPONSE_CODE;
    private String DETAIL_RESPONSE_MESSAGE;
}
