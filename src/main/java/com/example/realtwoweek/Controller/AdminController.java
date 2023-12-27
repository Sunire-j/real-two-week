package com.example.realtwoweek.Controller;

import com.example.realtwoweek.Mapper.AdminMapper;
import com.example.realtwoweek.Mapper.ItemMapper;
import com.example.realtwoweek.vo.ItemVO;
import com.example.realtwoweek.vo.PagingVO;
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

    public AdminController(ItemMapper itemMapper, AdminMapper adminMapper) {
        this.itemMapper = itemMapper;
        this.adminMapper = adminMapper;
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


}
