package com.example.realtwoweek.Controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller

public class PurchaseController {

    @PostMapping("/purchase/confirm")
    @ResponseBody
    public String returnURL(){
        System.out.println("!!!!!!!!!!!!!!!!!!!!!!");
        return "th/index";
    }

    @GetMapping("/test")
    public String testtest(){
        return "PayReturn";
    }
}
