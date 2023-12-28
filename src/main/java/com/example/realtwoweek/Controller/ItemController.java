package com.example.realtwoweek.Controller;

import com.example.realtwoweek.Mapper.ItemMapper;
import com.example.realtwoweek.vo.ItemVO;
import com.example.realtwoweek.vo.PagingVO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
public class ItemController {

    private final ItemMapper itemMapper;

    public ItemController(ItemMapper itemMapper) {
        this.itemMapper = itemMapper;
    }

    @GetMapping("/goods/view") //회원
    public String itemView(@RequestParam(required = false, defaultValue = "0")int no, Model model){
        if(no==0){
            return "404";
        }
        ItemVO ivo = itemMapper.getItemDetail(no);
        System.out.println(ivo.toString());
        model.addAttribute("ivo",ivo);
        return "th/items/items-view";
    }

    @GetMapping("/goods/list") //모두
    public String goodsList(int category, Model model, PagingVO pvo){//나중에 매개변수 받아야함
        List<ItemVO> ilist = itemMapper.getGoodsList(category);//매개변수 보내줘야함 일단 싸그리 가져옴
        pvo.setTotalRecord(ilist.size());
        pvo.setPage(pvo.getPage());
        pvo.setCategory(category);
        List<ItemVO> newlist = itemMapper.getGoodsListPaging(pvo);
        model.addAttribute("ilist",newlist);
        model.addAttribute("pVO", pvo);
        String cat = null;
        if(category==1){
            cat="남성용";
        }else if(category==2){cat="여성용";}else if(category==3){cat="악세사리";}else if(category==4){cat="소품";}
        model.addAttribute("category", cat);
        return "th/items/items-list";
    }




}
