package com.example.realtwoweek.Controller;

import com.example.realtwoweek.Mapper.MemberMapper;
import com.example.realtwoweek.domain.Member;
import com.example.realtwoweek.repository.MemberRepository;
import com.example.realtwoweek.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.persistence.Column;

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
    public String Login(){
        return "user/sign-in";
    }

    @GetMapping("/signup/checkemail")
    private ModelAndView EmailCheck(String email){

        ModelAndView mav = new ModelAndView();
        if(email==null){
            mav.addObject("msg","이메일을 입력 후 시도해주세요");
            mav.addObject("isCheck",0);
            mav.setViewName("user/popUp/idCheck");
            return mav;
        }
        int result = memberMapper.CheckEmail(email);
        if(result==0){
            mav.addObject("msg", email+"는 사용가능한 이메일입니다.");
            mav.addObject("isCheck",1);
        }else{
            mav.addObject("msg", email+"는 이미 사용중인 이메일입니다.");
            mav.addObject("isCheck",0);
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
    private String signup(){
        return "/user/sign-up";
    }

    @GetMapping("/signup/info")
    private String inputInfo(){
        return "/user/sign-up-info";
    }
}
