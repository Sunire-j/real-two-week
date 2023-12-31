package com.example.realtwoweek.Controller;


import com.example.realtwoweek.vo.PurchaseReturnParam;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.xml.ws.Service;

@Controller

public class PurchaseController {

    @PostMapping("/purchase/confirm")
    public String returnURL(PurchaseReturnParam prp, Model model){
        if(prp.getRESPONSE_CODE().equals("0000"))
        model.addAttribute("SERVICE_ID", prp.getSERVICE_ID());
        model.addAttribute("SERVICE_CODE", prp.getSERVICE_CODE());
        model.addAttribute("ORDER_ID", prp.getORDER_ID());
        model.addAttribute("ORDER_DATE", prp.getORDER_DATE());
        model.addAttribute("RESPONSE_CODE", prp.getRESPONSE_CODE());
        model.addAttribute("RESPONSE_MESSAGE", prp.getRESPONSE_MESSAGE());
        model.addAttribute("DETAIL_RESPONSE_CODE", prp.getDETAIL_RESPONSE_CODE());
        model.addAttribute("DETAIL_RESPONSE_MESSAGE", prp.getDETAIL_RESPONSE_MESSAGE());
        System.out.println(prp.toString());
        return "/PayReturn";
    }

}
