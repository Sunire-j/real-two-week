package com.example.realtwoweek.Controller;

import com.example.realtwoweek.Mapper.AdminMapper;
import com.example.realtwoweek.Mapper.BasketMapper;
import com.example.realtwoweek.Mapper.ItemMapper;
import com.example.realtwoweek.vo.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final ItemMapper itemMapper;
    private final AdminMapper adminMapper;
    private final BasketMapper basketMapper;

    public AdminController(ItemMapper itemMapper, AdminMapper adminMapper, BasketMapper basketMapper) {
        this.itemMapper = itemMapper;
        this.adminMapper = adminMapper;
        this.basketMapper = basketMapper;
    }


    @GetMapping("/home")
    public String adminhome(){
        return "/admin/home";
    }

    @GetMapping("/items")
    public String adminItems(Model model, PagingVO pvo){
        List<ItemVO> list = itemMapper.getAllItems();
        pvo.setTotalRecord(list.size());
        list = itemMapper.getAllItemsPaging(pvo);
        System.out.println(pvo.toString());
        model.addAttribute("itemList", list);
        model.addAttribute("pVO",pvo);
        return "admin/items";
    }

    @PostMapping("/items/del")
    @ResponseBody
    public int itemDelete(int items_id){
        adminMapper.deleteItem(items_id);
        return 0;
    }

    @GetMapping("/items/add")
    public String itemAdd(){
        return "admin/item-add";
    }

    @PostMapping("/items/add")
    public String addItem(String name,
                          int price,
                          int delivery,
                          MultipartFile file,
                          String seller,
                          int category,
                          String detail){
        ItemVO ivo = new ItemVO();
        ivo.setName(name);
        ivo.setPrice(price);
        ivo.setDelivery(delivery);
        ivo.setSeller(seller);
        ivo.setCategory(String.valueOf(category));
        ivo.setDetail(detail);

        // 파일저장시작
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyMMddHHmmss");
        String fileName = now.format(formatter);

        String extension = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
        String newFileName = fileName + extension;
        String projectDir = new File("").getAbsolutePath();
        File directory = new File(projectDir + "/upload/items");
        if (!directory.exists()) {
            directory.mkdirs(); // 디렉토리 생성
        }

        Path path = Paths.get(directory.getAbsolutePath(), newFileName); // 절대 경로를 사용

        try {
            file.transferTo(new File(path.toString()));
        } catch (IOException e) {
            e.printStackTrace();
            return "404pages";
        }
        // 파일저장 끝

        String imgsrc = "upload/items/"+fileName+extension;
        ivo.setImg(imgsrc);

        adminMapper.InsertItem(ivo);



        return "redirect:/admin/items";
    }

    @GetMapping("/items/manage")
    private String itemEdit(int items_id, Model model){
        ItemVO itemVO = itemMapper.getItemDetail(items_id);
        model.addAttribute(itemVO);
        return "/admin/item-edit";
    }
    @PostMapping("/items/edit")
    private String itemEditOk(String name,
                              int price,
                              int delivery,
                              @RequestParam(required = false) MultipartFile file,
                              String seller,
                              int category,
                              String detail,
                              int items_id){

        ItemVO ivo = new ItemVO();
        ivo.setName(name);
        ivo.setPrice(price);
        ivo.setDelivery(delivery);
        ivo.setSeller(seller);
        ivo.setCategory(String.valueOf(category));
        ivo.setDetail(detail);
        ivo.setItems_id(items_id);

        if(file!=null) {
            // 파일저장시작
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyMMddHHmmss");
            String fileName = now.format(formatter);

            String extension = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
            String newFileName = fileName + extension;
            String projectDir = new File("").getAbsolutePath();
            File directory = new File(projectDir + "/upload/items");
            if (!directory.exists()) {
                directory.mkdirs(); // 디렉토리 생성
            }

            Path path = Paths.get(directory.getAbsolutePath(), newFileName); // 절대 경로를 사용

            try {
                file.transferTo(new File(path.toString()));
            } catch (IOException e) {
                e.printStackTrace();
                return "404pages";
            }
            // 파일저장 끝

            String imgsrc = "upload/items/" + fileName + extension;
            ivo.setImg(imgsrc);
        }
        ivo.setImg("upload/items/1.jpg");
        adminMapper.UpdateItem(ivo);

        return "redirect:/admin/items";
    }

    @GetMapping("/order/waiting")
    private String orderListWaiting(PagingVO pvo, Model model){
        int total = adminMapper.getOrderTotalRecord();
        pvo.setTotalRecord(total);
        model.addAttribute("pVO", pvo);
        List<OrderVO> list = adminMapper.getOrderList(pvo);
        model.addAttribute("list", list);
        return "admin/orders-waiting";
    }

    @PostMapping("/order/nextStep")
    @ResponseBody
    private int orderNextStep(String orderNum){
        int result = adminMapper.orderNextStep(orderNum);
        return result;
    }

    @GetMapping("/order/detail")
    private String orderDetail(String no, Model model){
        OrderVO ovo = adminMapper.getOrderDetail(no);
        System.out.println(ovo.toString());
        model.addAttribute("ovo",ovo);
        List<BasketVO> items = basketMapper.getOrderItemList(ovo.getIdorder());
        model.addAttribute("blist",items);
        String method = basketMapper.getMethodName(ovo.getMethod());
        model.addAttribute("method", method);
        if(ovo.getMethod()==1){
            String methodDetail = basketMapper.getMethodDetailName(ovo.getMethodDetails());
            String bank = methodDetail.substring(0, methodDetail.indexOf("("));
            String account = methodDetail.substring(methodDetail.indexOf("(")+1, methodDetail.indexOf(")"));
            model.addAttribute("bank", bank);
            model.addAttribute("account", account);
        }
        return "admin/order-detail";
    }

    @GetMapping("/order/complete")
    private String orderListComplete(PagingVO pvo, Model model){
        int total = adminMapper.getOrderCompleteTotalRecord();
        pvo.setTotalRecord(total);
        model.addAttribute("pVO", pvo);
        List<OrderVO> list = adminMapper.getOrderListComplete(pvo);
        model.addAttribute("list", list);
        return "admin/orders-finish";
    }

    @GetMapping("/payment")
    private String payment(PagingVO pvo, Model model){
        int totalRecord = adminMapper.getMethodDetailCount();
        pvo.setTotalRecord(totalRecord);
        List<MethodDetailVO> methodlist = adminMapper.getAllMethodDetail(pvo);
        for(MethodDetailVO newlist : methodlist){
            newlist.setBank(newlist.getType().substring(0,newlist.getType().indexOf("(")));
            newlist.setAccount(newlist.getType().substring(newlist.getType().indexOf("(")+1,newlist.getType().indexOf(")")));;
        }
        model.addAttribute("list",methodlist);

        model.addAttribute("pVO", pvo);
        return "admin/payment";
    }

    @GetMapping("/payment/edit")
    private String paymentEdit(Model model, int id){
        MethodDetailVO methodDetailVO = adminMapper.getMethodDetailDetail(id);
        methodDetailVO.setBank(methodDetailVO.getType().substring(0,methodDetailVO.getType().indexOf("(")));
        methodDetailVO.setAccount(methodDetailVO.getType().substring(methodDetailVO.getType().indexOf("(")+1,methodDetailVO.getType().indexOf(")")));;
        model.addAttribute("vo", methodDetailVO);
        return "admin/payment-edit";
    }

    @PostMapping("/payment/edit")
    private String paymentEditOk(MethodDetailVO mdvo){
        System.out.println(mdvo);
        mdvo.setType(mdvo.getBank()+"("+mdvo.getAccount()+")");
        adminMapper.editMethodDetail(mdvo);
        return "redirect:/admin/payment";
    }

    @GetMapping("payment/add")
    private String paymentAdd(){
        return "admin/payment-add";
    }

    @PostMapping("payment/add")
    private String paymentAddOk(MethodDetailVO mdvo){
        mdvo.setType(mdvo.getBank()+"("+mdvo.getAccount()+")");
        adminMapper.addMethodDetail(mdvo);
        return "redirect:/admin/payment/";
    }

    @PostMapping("/payment/del")
    @ResponseBody
    private int paymentDel(int id){
        return adminMapper.deleteMethodDetail(id);
    }

    @GetMapping("/user")
    private String user(PagingVO pvo, Model model){
        pvo.setTotalRecord(adminMapper.getTotalUserCount());
        List<MemberVO> list = adminMapper.getUser(pvo);
        model.addAttribute("List", list);
        model.addAttribute("pVO", pvo);
        for(MemberVO newList : list){
            newList.setTotalBuy(adminMapper.getTotalBuy(newList.getMember_id()));
            System.out.println(newList.getTotalBuy());
        }
        return "admin/user";
    }

    @PostMapping("/user/del")
    @ResponseBody
    private int userDel(Long member_id){
        return adminMapper.deleteMember(member_id);
    }


}
