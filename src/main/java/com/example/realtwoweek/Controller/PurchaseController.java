package com.example.realtwoweek.Controller;

import com.example.realtwoweek.vo.PurchaseReturnParam;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class PurchaseController {

    @PostMapping("/order/confirm")
    public String returnURL(){
        System.out.println("들어옴");
        return "PayReturn";
    }

}
