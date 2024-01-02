package com.example.realtwoweek.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OrderVO {
    private int idorder;
    private Long member_id;
    private String date;
    private int status;
    private String ordername;
    private String orderphonenum;
    private String orderemail;
    private String receivename;
    private String receivephonenum;
    private String receiveaddress1;
    private String receiveaddress2;
    private String receiveaddress3;
    private String request;
    private int zipno;
    private int method;
    private int methodDetails;
    private String orderNum;
    private int totalprice;
    private int delivery;
    private String cardNum;
}
