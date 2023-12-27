package com.example.realtwoweek.Controller;

import com.example.realtwoweek.Mapper.BasketMapper;
import com.example.realtwoweek.Mapper.MemberMapper;
import com.example.realtwoweek.Util.AuthenticationUtil;
import com.example.realtwoweek.Util.UserIdentity;
import com.example.realtwoweek.domain.Member;
import com.example.realtwoweek.repository.MemberRepository;
import com.example.realtwoweek.service.MemberService;
import com.example.realtwoweek.vo.BasketVO;
import com.example.realtwoweek.vo.MemberVO;
import com.example.realtwoweek.vo.OrderVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Controller
public class UserController {

    private final MemberService memberService;
    private final MemberMapper memberMapper;
    private final BasketMapper basketMapper;

    @Autowired
    public UserController(MemberService memberService, MemberMapper memberMapper, BasketMapper basketMapper) {
        this.memberService = memberService;
        this.memberMapper = memberMapper;
        this.basketMapper = basketMapper;
    }

    @GetMapping("/signin")
    public String Login() {
        return "user/sign-in";
    }

    @GetMapping("/signup/checkemail")
    private ModelAndView EmailCheck(String email) {

        ModelAndView mav = new ModelAndView();
        if (email == null) {
            mav.addObject("msg", "이메일을 입력 후 시도해주세요");
            mav.addObject("isCheck", 0);
            mav.setViewName("user/popUp/idCheck");
            return mav;
        }
        int result = memberMapper.CheckEmail(email);
        if (result == 0) {
            mav.addObject("msg", email + "는 사용가능한 이메일입니다.");
            mav.addObject("isCheck", 1);
        } else {
            mav.addObject("msg", email + "는 이미 사용중인 이메일입니다.");
            mav.addObject("isCheck", 0);
        }
        mav.setViewName("user/popUp/emailCheck");
        return mav;
    }


    @PostMapping("/signup")
    @ResponseBody
    public Long signup(@RequestBody Member member) {
        return memberService.join(member);
    }

    @GetMapping("/signup")
    private String signup() {
        return "/user/sign-up";
    }

    @GetMapping("/signup/info")
    private String inputInfo() {
        return "/user/sign-up-info";
    }

    @GetMapping("/signup/end")
    private String signupEnd() {
        return "/user/sign-up-end";
    }

    @GetMapping("/user/address")
    @ResponseBody
    private List<Object> getAddress(Authentication authentication) {
        UserIdentity userIdentity = AuthenticationUtil.getUserIdentity(authentication);
        Long userid = memberMapper.getUserid(userIdentity.getProvider(), userIdentity.getEmail());

        List<Object> address = new ArrayList<Object>();
        if (memberMapper.getZipno(userid) != 0) {
            address.add(memberMapper.getZipno(userid));
            address.add(memberMapper.getAddress1(userid));
            address.add(memberMapper.getAddress2(userid));
            address.add(memberMapper.getAddress3(userid));
        }
        return address;
    }

    @GetMapping("/mypage")
//    홈이긴 한데 주문내역이랑 다를바가 없음
    private String myPage(Authentication authentication, Model model){
        UserIdentity userIdentity = AuthenticationUtil.getUserIdentity(authentication);
        Long userid = memberMapper.getUserid(userIdentity.getProvider(), userIdentity.getEmail());
        List<OrderVO> orderVOList = memberMapper.getOrderList(userid);
        System.out.println(orderVOList);
        model.addAttribute(orderVOList);
        return "/user/myPageHome";
    }

    @GetMapping("/mypage/order")
    private String orderDetail(Long no, Model model, Authentication authentication){
        UserIdentity userIdentity = AuthenticationUtil.getUserIdentity(authentication);
        Long userid = memberMapper.getUserid(userIdentity.getProvider(), userIdentity.getEmail());
        OrderVO order = memberMapper.getOrderDetail(no);
        if(!Objects.equals(userid, order.getMember_id())){
            return "404";
        }
        List<BasketVO> items = basketMapper.getOrderItemList(order.getIdorder());
        System.out.println(items);
        model.addAttribute("ovo",order);
        model.addAttribute("blist",items);
        String method = basketMapper.getMethodName(order.getMethod());
        model.addAttribute("method", method);
        if(order.getMethod()==1){
            String methodDetail = basketMapper.getMethodDetailName(order.getMethodDetails());
            String bank = methodDetail.substring(0, methodDetail.indexOf("("));
            String account = methodDetail.substring(methodDetail.indexOf("(")+1, methodDetail.indexOf(")"));
            model.addAttribute("bank", bank);
            model.addAttribute("account", account);
        }
        return "/user/myPage-orderDetail";
    }

    @GetMapping("/mypage/delete")
    private String deleteAccount(){
        return "user/DeleteAccount";
    }

    @GetMapping("/byebye")
    private String realDelete(Authentication auth){
        UserIdentity userIdentity = AuthenticationUtil.getUserIdentity(auth);
        Long userid = memberMapper.getUserid(userIdentity.getProvider(), userIdentity.getEmail());

        //탈퇴하기 전에 유저에 대한 주문정보같은건 분리를 해야함
        //멤버아이디만 null로 바꿔버림
        basketMapper.deleteAccount(userid);
        //바스켓같은건 날려도 괜찮

        memberMapper.deleteAccount(userid);
        return "redirect:/logout";
    }
    @GetMapping("/mypage/edit")
    private String editInfo(Authentication auth, Model model){
        UserIdentity userIdentity = AuthenticationUtil.getUserIdentity(auth);
        Long userid = memberMapper.getUserid(userIdentity.getProvider(), userIdentity.getEmail());
        MemberVO memberVO = memberMapper.getUserInfo(userid);
        if(!memberVO.getProvider().equals("none") && memberVO.getPassword()==null){
            return "/user/set-password";
        }
        System.out.println(memberVO);
        model.addAttribute("mvo",memberVO);
        String phone1="none";
        String phone2="";
        String phone3="";
        if(memberVO.getPhone()!=null){
            phone1 = memberVO.getPhone().substring(0,3);
            phone2 = memberVO.getPhone().substring(3,7);
            phone3 = memberVO.getPhone().substring(7);
        }
        model.addAttribute("phone1", phone1);
        model.addAttribute("phone2", phone2);
        model.addAttribute("phone3", phone3);


        //수정가능범위
        //이름, 비번, 휴대폰번호, 주소, 어느아이디인지 띄워주기
        return "/user/myPage-Edit" +
                "";
    }
}
