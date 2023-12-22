package com.example.realtwoweek.Controller;

import com.example.realtwoweek.Mapper.BasketMapper;
import com.example.realtwoweek.Mapper.MemberMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal;
import java.util.Map;

@Controller
public class BasketController {

    private final BasketMapper basketMapper;
    private final MemberMapper memberMapper;

    public BasketController(BasketMapper basketMapper, MemberMapper memberMapper) {
        this.basketMapper = basketMapper;
        this.memberMapper = memberMapper;
    }

    @GetMapping("/goods/check") //회원
    @ResponseBody
    public int addBasket(@RequestParam int items_id, @RequestParam int amount, OAuth2AuthenticationToken authentication) {
        if (authentication == null) {
            return -1;
        }

        Map<String, Object> attributes = authentication.getPrincipal().getAttributes();

        String provider = authentication.getAuthorizedClientRegistrationId();

        String u_email;
        if ("naver".equals(provider)) {
            Map<String, Object> response = (Map<String, Object>) attributes.get("response");
            u_email = (String) response.get("email");
        } else {
            u_email = (String) attributes.getOrDefault("email", "default_email");
        }

        System.out.println(provider+"프로바이더");
        System.out.println(u_email+"이메일");
        Long userid = memberMapper.getUserid(provider, u_email);

        //일단 넣기 전에 이미 장바구니에 추가된 아이템인지를 알아야함
        //만약 있다면 amount만 늘리는 방향으로 가야함
        int isHere = basketMapper.checkBasket(items_id, userid);
        int result = 0;
        if(isHere==1){
            result = basketMapper.increaseAmount(items_id, amount, userid);
        }else{
            result = basketMapper.addBasket(items_id, amount, userid);
        }
        return result;
    }

    @GetMapping("/add/success")
    public String loadPopup(){
        return "popUp/basket-success";
    }



}
