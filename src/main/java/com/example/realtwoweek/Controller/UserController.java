package com.example.realtwoweek.Controller;

import com.example.realtwoweek.Mapper.MemberMapper;
import com.example.realtwoweek.Util.AuthenticationUtil;
import com.example.realtwoweek.Util.UserIdentity;
import com.example.realtwoweek.domain.Member;
import com.example.realtwoweek.repository.MemberRepository;
import com.example.realtwoweek.service.MemberService;
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

@Controller
public class UserController {

    private final MemberService memberService;
    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;
    private final MemberMapper memberMapper;

    @Autowired
    public UserController(MemberService memberService, PasswordEncoder passwordEncoder, MemberRepository memberRepository, MemberMapper memberMapper) {
        this.memberService = memberService;
        this.passwordEncoder = passwordEncoder;
        this.memberRepository = memberRepository;
        this.memberMapper = memberMapper;
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
}
