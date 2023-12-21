package com.example.realtwoweek.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.persistence.Column;

@Controller
@RequestMapping("/admin")
public class TestController {

    @GetMapping("/home")
    public String adminhome(){
        return "/admin/test";
    }

}
