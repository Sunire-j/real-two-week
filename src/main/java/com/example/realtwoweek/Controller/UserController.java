package com.example.realtwoweek.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.persistence.Column;

@Controller
public class UserController {

    @GetMapping("/signin")
    public String Login(){
        return "user/sign-in";
    }
}
