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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.security.Security;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Controller
public class UserController {

    private final MemberService memberService;
    private final MemberMapper memberMapper;
    private final BasketMapper basketMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    public UserController(MemberService memberService, MemberMapper memberMapper, BasketMapper basketMapper) {
        this.memberService = memberService;
        this.memberMapper = memberMapper;
        this.basketMapper = basketMapper;
    }

    @GetMapping("/signin")
    public String Login() {
        return "th/user/sign-in";
    }

    @GetMapping("/signup/checkemail")
    private ModelAndView EmailCheck(String email) {

        ModelAndView mav = new ModelAndView();
        if (email == null) {
            mav.addObject("msg", "이메일을 입력 후 시도해주세요");
            mav.addObject("isCheck", 0);
            mav.setViewName("th/user/popUp/idCheck");
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
        mav.setViewName("th/user/popUp/emailCheck");
        return mav;
    }


    @PostMapping("/signup")
    @ResponseBody
    public Long signup(@RequestBody Member member) {
        return memberService.join(member);
    }

    @GetMapping("/signup")
    private String signup() {
        return "th/user/sign-up";
    }

    @GetMapping("/signup/info")
    private String inputInfo() {
        return "th/user/sign-up-info";
    }

    @GetMapping("/signup/end")
    private String signupEnd() {
        return "th/user/sign-up-end";
    }

    @GetMapping("/user/address")
    @ResponseBody
    private List<Object> getAddress() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();

        Long userid = null;
        if (principal instanceof Member) {
            userid = ((Member) principal).getId();
        }

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
    private String myPage(Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();

        Long userid = null;
        if (principal instanceof Member) {
            userid = ((Member) principal).getId();
        }


        List<OrderVO> orderVOList = memberMapper.getOrderList(userid);
        System.out.println(orderVOList);
        model.addAttribute(orderVOList);
        return "th/user/myPageHome";
    }

    @GetMapping("/mypage/order")
    private String orderDetail(Long no, Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();

        Long userid = null;
        if (principal instanceof Member) {
            userid = ((Member) principal).getId();
        }
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
        return "th/user/myPage-orderDetail";
    }

    @GetMapping("/mypage/delete")
    private String deleteAccount(){
        return "th/user/DeleteAccount";
    }

    @GetMapping("/byebye")
    private String realDelete(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();

        Long userid = null;
        if (principal instanceof Member) {
            userid = ((Member) principal).getId();
        }

        //탈퇴하기 전에 유저에 대한 주문정보같은건 분리를 해야함
        //멤버아이디만 null로 바꿔버림
        basketMapper.deleteAccount(userid);
        //바스켓같은건 날려도 괜찮

        memberMapper.deleteAccount(userid);
        return "redirect:/logout";
    }
    @GetMapping("/mypage/edit")
    private String editInfo( Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();

        Long userid = null;
        if (principal instanceof Member) {
            userid = ((Member) principal).getId();
        }
        MemberVO memberVO = memberMapper.getUserInfo(userid);

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


        if(!memberVO.getProvider().equals("none") && memberVO.getPassword()==null){
            return "th/user/myPage-Edit-Social";
        }


        //수정가능범위
        //이름, 비번, 휴대폰번호, 주소, 어느아이디인지 띄워주기
        return "th/user/myPage-Edit";
    }

    @PostMapping("/mypage/checkpwd")
    @ResponseBody
    private boolean checkpwd(String pwd){

        pwd = passwordEncoder.encode(pwd);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();

        Long userid = null;
        if (principal instanceof Member) {
            userid = ((Member) principal).getId();
        }
        String encodedpwd = memberMapper.getUserpwd(userid);

        return encodedpwd.equals(pwd);
    }

    @PostMapping("/mypage/edit")
    @ResponseBody
    private int changeInfo(MemberVO mvo){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();

        Long userid = null;
        if (principal instanceof Member) {
            userid = ((Member) principal).getId();
        }
        mvo.setMember_id(userid);

        System.out.println(mvo.getPassword());

        if(mvo.getPassword()==null || mvo.getPassword().equals("")){
            mvo.setPassword(memberMapper.getUserpwd(userid));
            System.out.println("기존비번진입");
        }else{
            mvo.setPassword(passwordEncoder.encode(mvo.getPassword()));
            System.out.println("새 비번 진입");
        }

        System.out.println(mvo.getPassword());


        int result =  memberMapper.changeInfo(mvo);
        if(result>0 && mvo.getPassword()!=null){
            Authentication newAuth = new UsernamePasswordAuthenticationToken(
                    authentication.getPrincipal(),
                    mvo.getPassword(),
                    authentication.getAuthorities()
            );
            SecurityContextHolder.getContext().setAuthentication(newAuth);
        }
        return result;
    }

    @PostMapping("mypage/order/cancle")
    @ResponseBody
    private int cancleOrder(String orderNum){
        OrderVO orderVO = memberMapper.getOrderDetailWithOrderNum(orderNum);
        int change = orderVO.getStatus()==0?-2:-1;
        return memberMapper.cancleOrder(orderNum, change);
    }

    @PostMapping("/mypage/social/edit")
    @ResponseBody
    private int infoEditSocial(MemberVO mvo){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();

        Long userid = null;
        if (principal instanceof Member) {
            userid = ((Member) principal).getId();
        }
        mvo.setMember_id(userid);
        return memberMapper.editInfoSocial(mvo);
    }

}
