package com.example.realtwoweek.Controller;

import com.example.realtwoweek.Mapper.BasketMapper;
import com.example.realtwoweek.Mapper.MemberMapper;
import com.example.realtwoweek.Util.AuthenticationUtil;
import com.example.realtwoweek.Util.UserIdentity;
import com.example.realtwoweek.vo.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.OptionalInt;

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
    public int addBasket(@RequestParam int items_id, @RequestParam int amount, Authentication authentication) {
        if (authentication == null) {
            return -1;
        }

        System.out.println(authentication.toString());

        UserIdentity userIdentity = AuthenticationUtil.getUserIdentity(authentication);
        System.out.println(userIdentity.getProvider() + " : provider");
        System.out.println(userIdentity.getEmail()+" : email");
        Long userid = memberMapper.getUserid(userIdentity.getProvider(), userIdentity.getEmail());



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
    public String loadPopup(Authentication authentication){
        return "popUp/basket-success";
    }

    @GetMapping("/basket")
    public String mybasket(Authentication authentication, Model model){
        UserIdentity userIdentity = AuthenticationUtil.getUserIdentity(authentication);
        Long userid = memberMapper.getUserid(userIdentity.getProvider(), userIdentity.getEmail());
        List<BasketVO> bList = basketMapper.getAllBasket(userid);

        int maxDelivery = bList.stream()
                .mapToInt(BasketVO::getDelivery)
                .max()
                .orElse(0);

        int total = (bList.stream()
                .mapToInt(basket -> basket.getPrice() * basket.getAmount())
                .sum());

        System.out.println(maxDelivery);
        System.out.println(total);

        model.addAttribute("maxDelivery", maxDelivery);
        model.addAttribute("sumPrice",total);

        model.addAttribute("blist",bList);


        return "basket/my-basket";
    }

    @PostMapping("/basket/setAmount")
    @ResponseBody
    public int setAmount(int itemid, int amount, Authentication authentication){
        UserIdentity userIdentity = AuthenticationUtil.getUserIdentity(authentication);
        Long userid = memberMapper.getUserid(userIdentity.getProvider(), userIdentity.getEmail());

        int result = basketMapper.SetAmount(userid, itemid, amount);
        return result;
    }

    @PostMapping("/basket/dropItem")
    @ResponseBody
    public int dropItem(int itemid, Authentication authentication){
        UserIdentity userIdentity = AuthenticationUtil.getUserIdentity(authentication);
        Long userid = memberMapper.getUserid(userIdentity.getProvider(), userIdentity.getEmail());

        int result = basketMapper.dropItem(userid, itemid);
        return result;
    }

    @GetMapping("/basket/purchase")
    public String purchase(Authentication authentication, Model model){
        UserIdentity userIdentity = AuthenticationUtil.getUserIdentity(authentication);
        Long userid = memberMapper.getUserid(userIdentity.getProvider(), userIdentity.getEmail());

        List<BasketVO> bList = basketMapper.getAllBasket(userid);

        int maxDelivery = bList.stream()
                .mapToInt(BasketVO::getDelivery)
                .max()
                .orElse(0);

        int total = (bList.stream()
                .mapToInt(basket -> basket.getPrice() * basket.getAmount())
                .sum());

        model.addAttribute("maxDelivery", maxDelivery);
        model.addAttribute("sumPrice",total);

        model.addAttribute("blist",bList);

        //유저정보도 모델에 넣어서 가져와야함
        //그런데 이름이랑 이메일은 이미 가지고 있음
        //그런데 이메일을 그냥 못보냄 잘라서 보내줘야함

        //폰번호는 가입때 11자리로 입력받았을거임
        //그냥 3,4,4로 잘라서 따로 보내줘야함

        String phone1="";
        String phone2="";
        String phone3="";

        String phonenum = memberMapper.getPhonenum(userid);
        if(phonenum==null){
            System.out.println("폰번호 비어있음");
        }else{
            System.out.println("폰번호 이거임" + phonenum);
            phone1 = phonenum.substring(0,3);
            phone2 = phonenum.substring(3,7);
            phone3 = phonenum.substring(7);
        }

        model.addAttribute("phone1", phone1);
        model.addAttribute("phone2", phone2);
        model.addAttribute("phone3", phone3);

        String emailid = userIdentity.getEmail().substring(0,userIdentity.getEmail().indexOf('@'));
        model.addAttribute("emailid", emailid);
        String emaildomain = userIdentity.getEmail().substring(userIdentity.getEmail().indexOf('@')+1);
        model.addAttribute("emaildomain",emaildomain);
        String username = memberMapper.getUsername(userid);
        model.addAttribute("username", username);

        //결제수단 불러와야함
        List<MethodVO> methodVOList = basketMapper.getAllMethod();

        List<MethodDetailVO> methodDetailVOList = basketMapper.getAllMethodDetail();

        model.addAttribute("methodDetailList", methodDetailVOList);


        return "/items/purchase";
    }

    @PostMapping("/basket/order")
    @ResponseBody
    public int orderBasket(OrderVO ovo, Authentication authentication){
        System.out.println(ovo.toString());
        UserIdentity userIdentity = AuthenticationUtil.getUserIdentity(authentication);
        Long userid = memberMapper.getUserid(userIdentity.getProvider(), userIdentity.getEmail());
        ovo.setMember_id(userid);
        if(ovo.getMethod()!=1){
            ovo.setMethodDetails(0);
        }
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyMMddHHmmss");
        String ordernum = now.format(formatter)+userid.toString();
        ovo.setOrderNum(ordernum);
        int result = basketMapper.addNewOrder(ovo);
        return ovo.getIdorder();
    }

    @PostMapping("/basket/addItemToOrder")
    @ResponseBody
    public int addItemToOrder(Authentication authentication, int orderid){
        UserIdentity userIdentity = AuthenticationUtil.getUserIdentity(authentication);
        Long userid = memberMapper.getUserid(userIdentity.getProvider(), userIdentity.getEmail());
        List<BasketVO> list = basketMapper.getAllBasket(userid);
        for(BasketVO basket : list){
            basketMapper.addItemToOrder(basket.getItems_id(), basket.getAmount(), orderid);
        }
        //여기서 해야하는거? 상품별 가격 구하고, 배송비 구해서  order안에 넣어줘야함
        int sum = list.stream()
                .mapToInt(BasketVO::getPrice)
                .sum();

        OptionalInt maxDelivery = list.stream()
                .mapToInt(BasketVO::getDelivery)
                .max();

        int maxDeliveryValue = maxDelivery.orElse(0);

        basketMapper.setPrice(orderid, sum, maxDeliveryValue);
        return orderid;
    }

    @GetMapping("/basket/complete")
    public String orderComplete(Model model, int orderid, Authentication authentication){

        OrderVO ovo = basketMapper.getOrder(orderid);

        //여기서 장바구니 비워줘야함
        UserIdentity userIdentity = AuthenticationUtil.getUserIdentity(authentication);
        Long userid = memberMapper.getUserid(userIdentity.getProvider(), userIdentity.getEmail());
        basketMapper.basketEmpty(userid);

        //모델에 들어갈 것
        //주문번호, 주문일자, 주문자 정보(orderVO로 다 받아오면 됨)
        //결제정보(ordervo안에 들어있음)
        //배송정보(ordervo안에 들어있음)
        List<BasketVO> itemslist = basketMapper.getOrderItemList(orderid);
        System.out.println(itemslist.toString());
        model.addAttribute("blist", itemslist);
        model.addAttribute("ovo", ovo);
        String method = basketMapper.getMethodName(ovo.getMethod());
        model.addAttribute("method", method);
        if(ovo.getMethod()==1){
            String methodDetail = basketMapper.getMethodDetailName(ovo.getMethodDetails());
            String bank = methodDetail.substring(0, methodDetail.indexOf("("));
            String account = methodDetail.substring(methodDetail.indexOf("(")+1, methodDetail.indexOf(")"));
            model.addAttribute("bank", bank);
            model.addAttribute("account", account);
        }else{
            //여기에서 주문진행상황 1로 바꿔줘야함
            basketMapper.statusIncrease(orderid);
        }
        return "items/order-complete";
    }
}
