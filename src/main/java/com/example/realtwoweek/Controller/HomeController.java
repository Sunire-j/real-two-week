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

    private final ItemMapper itemMapper;

    public HomeController(ItemMapper itemMapper) {
        this.itemMapper = itemMapper;
    }

    @GetMapping("/")
    public String home (Model model){
        //신상품 리스트 가져오기
        List<ItemVO> Newlist = itemMapper.getNewItems();
        List<ItemVO> RecommendList = itemMapper.getRecommendItems();
        model.addAttribute("newitems", Newlist);
        return "index";
    }

    @GetMapping("/error")
    public String errorPage(){
        return "error";
    }
}
