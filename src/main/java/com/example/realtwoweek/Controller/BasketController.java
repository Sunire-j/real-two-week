package com.example.realtwoweek.Controller;

import com.example.realtwoweek.Mapper.BasketMapper;
import com.example.realtwoweek.Mapper.ItemMapper;
import com.example.realtwoweek.Mapper.MemberMapper;
import com.example.realtwoweek.Util.AuthenticationUtil;
import com.example.realtwoweek.Util.UserIdentity;
import com.example.realtwoweek.domain.Member;
import com.example.realtwoweek.vo.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
    private final ItemMapper itemMapper;


    public BasketController(BasketMapper basketMapper, MemberMapper memberMapper, ItemMapper itemMapper) {
        this.basketMapper = basketMapper;
        this.memberMapper = memberMapper;
        this.itemMapper = itemMapper;
    }

    @GetMapping("/goods/check") //회원
    @ResponseBody
    public int addBasket(@RequestParam int items_id, @RequestParam int amount) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();

        Long userid = null;
        if (principal instanceof Member) {
            userid = ((Member) principal).getId();
        }

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
        return "th/popUp/basket-success";
    }

    @GetMapping("/basket")
    public String mybasket(Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();

        Long userid = null;
        if (principal instanceof Member) {
            userid = ((Member) principal).getId();
        }
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


        return "th/basket/my-basket";
    }

    @PostMapping("/basket/setAmount")
    @ResponseBody
    public int setAmount(int itemid, int amount){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();

        Long userid = null;
        if (principal instanceof Member) {
            userid = ((Member) principal).getId();
        }

        int result = basketMapper.SetAmount(userid, itemid, amount);
        return result;
    }

    @PostMapping("/basket/dropItem")
    @ResponseBody
    public int dropItem(int itemid){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();

        Long userid = null;
        if (principal instanceof Member) {
            userid = ((Member) principal).getId();
        }

        int result = basketMapper.dropItem(userid, itemid);
        return result;
    }

    @GetMapping("/basket/purchase")
    public String purchase(Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();

        Long userid = null;
        if (principal instanceof Member) {
            userid = ((Member) principal).getId();
        }
        model.addAttribute("userid", userid);

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
        String itemname = bList.get(0).getName();
        itemname += " 외 "+String.valueOf(bList.size()-1) +"건";
        model.addAttribute("itemname",itemname);



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

        String emailid = ((Member) principal).getEmail().substring(0,((Member) principal).getEmail().indexOf('@'));
        model.addAttribute("emailid", emailid);
        String emaildomain = ((Member) principal).getEmail().substring(((Member) principal).getEmail().indexOf('@')+1);
        model.addAttribute("emaildomain",emaildomain);
        String username = memberMapper.getUsername(userid);
        model.addAttribute("username", username);

        //결제수단 불러와야함
        List<MethodVO> methodVOList = basketMapper.getAllMethod();

        List<MethodDetailVO> methodDetailVOList = basketMapper.getAllMethodDetail();

        model.addAttribute("methodDetailList", methodDetailVOList);


        return "th/items/purchase";
    }

    @PostMapping("/basket/order")
    @ResponseBody
    public int orderBasket(OrderVO ovo){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();

        Long userid = null;
        if (principal instanceof Member) {
            userid = ((Member) principal).getId();
        }
        ovo.setMember_id(userid);
        if(ovo.getMethod()!=1){
            ovo.setMethodDetails(0);
        }
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyMMddHHmmss");
        String ordernum = now.format(formatter)+userid.toString();
        ovo.setOrderNum(ordernum);
        int result = basketMapper.addNewOrder(ovo);
        if(ovo.getMethod()==2){
            //여기에서 -3으로 바꿔줘야함
            ovo.setStatus(-3);
            memberMapper.setStatus(ovo);
        }
        return ovo.getIdorder();
    }

    @PostMapping("/basket/addItemToOrder")
    @ResponseBody
    public int addItemToOrder(int orderid){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();

        Long userid = null;
        if (principal instanceof Member) {
            userid = ((Member) principal).getId();
        }
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
    public String orderComplete(Model model, int orderid){

        OrderVO ovo = basketMapper.getOrder(orderid);

        //여기서 장바구니 비워줘야함
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();

        Long userid = null;
        if (principal instanceof Member) {
            userid = ((Member) principal).getId();
        }
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
        return "th/items/order-complete";
    }

    @GetMapping("/goods/buy")
    public String oneitemOrder(int items_id, int amount, Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();

        Long userid = null;
        if (principal instanceof Member) {
            userid = ((Member) principal).getId();
        }

        ItemVO ivo = itemMapper.getItemDetail(items_id);
        model.addAttribute("maxDelivery", ivo.getDelivery());
        model.addAttribute("sumPrice",ivo.getPrice()*amount);
        model.addAttribute("ivo", ivo);

        model.addAttribute("userid", userid);

        String phone1="";
        String phone2="";
        String phone3="";

        String phonenum = memberMapper.getPhonenum(userid);
        if(phonenum==null){
            System.out.println("폰번호 비어있음");
        }else{
            phone1 = phonenum.substring(0,3);
            phone2 = phonenum.substring(3,7);
            phone3 = phonenum.substring(7);
        }
        model.addAttribute("amount", amount);

        model.addAttribute("phone1", phone1);
        model.addAttribute("phone2", phone2);
        model.addAttribute("phone3", phone3);

        String emailid = ((Member) principal).getEmail().substring(0,((Member) principal).getEmail().indexOf('@'));
        model.addAttribute("emailid", emailid);
        String emaildomain = ((Member) principal).getEmail().substring(((Member) principal).getEmail().indexOf('@')+1);
        model.addAttribute("emaildomain",emaildomain);
        String username = memberMapper.getUsername(userid);
        model.addAttribute("username", username);

        //결제수단 불러와야함
        List<MethodVO> methodVOList = basketMapper.getAllMethod();

        List<MethodDetailVO> methodDetailVOList = basketMapper.getAllMethodDetail();

        model.addAttribute("methodDetailList", methodDetailVOList);


        return "th/items/purchase-one-items";
    }

    @PostMapping("/goods/addItemToOrder")
    @ResponseBody
    private int oneItemPurchase(int orderid, int items_id, int amount){
        System.out.println(orderid);
        System.out.println(items_id);
        System.out.println(amount);
        basketMapper.addItemToOrder(items_id, amount, orderid);
        ItemVO ivo = itemMapper.getItemDetail(items_id);
        basketMapper.setPrice(orderid, ivo.getPrice()*amount, ivo.getDelivery());
        return orderid;
    }

    @PostMapping("/order/cancle")
    @ResponseBody
    private int orderCancle(String orderNum){
        return basketMapper.deleteOrder(orderNum);
    }
}
