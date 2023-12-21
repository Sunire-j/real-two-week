package com.example.realtwoweek.Controller;

import com.example.realtwoweek.Mapper.ItemMapper;
import com.example.realtwoweek.domain.Member;
import com.example.realtwoweek.repository.MemberRepository;
import com.example.realtwoweek.vo.ItemVO;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
public class HomeController{

    @Autowired
    private MemberRepository memberRepository;

    private final ItemMapper itemMapper;

    public HomeController(ItemMapper itemMapper) {
        this.itemMapper = itemMapper;
    }

    @GetMapping("/")
    public String home (Model model, OAuth2AuthenticationToken authentication){
        if (authentication != null) {
            String name = "";
            Map<String, Object> attributes = authentication.getPrincipal().getAttributes();
            String provider = authentication.getAuthorizedClientRegistrationId();

            System.out.println("Email: " + authentication.getName());  // Print email
            System.out.println("Provider: " + provider);  // Print provider

            if ("google".equals(provider)) {
                name = (String) attributes.get("name");
            } else if ("naver".equals(provider)) {
                Map<String, Object> response = (Map<String, Object>) attributes.get("response");
                name = (String) response.get("name");
            }

            Optional<Member> optionalMember = memberRepository.findByEmailAndProvider(authentication.getName(), provider);
            if(optionalMember.isPresent()){
                System.out.println("들어옴?");
                Member member = optionalMember.get();
                model.addAttribute("member", member.getName());
                System.out.println(member.getName());
            }
        }
        //신상품 리스트 가져오기
        List<ItemVO> Newlist = itemMapper.getNewItems();
        List<ItemVO> RecommendList = itemMapper.getRecommendItems();
        model.addAttribute("newitems", Newlist);
        return "index";
    }
}
