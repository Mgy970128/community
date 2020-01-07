package top.mgy.community.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import top.mgy.community.dto.PaginationDTO;
import top.mgy.community.model.Notification;
import top.mgy.community.model.User;
import top.mgy.community.service.NotificationServies;
import top.mgy.community.service.QuestionService;
import javax.servlet.http.HttpServletRequest;

@Controller
public class ProfileController {

    @Autowired
    private QuestionService questionService;

    @Autowired
    private NotificationServies notificationServies;

    @GetMapping("/profile/{action}")
    public String profile(@PathVariable("action") String action, Model model,HttpServletRequest request,
                          @RequestParam(name = "page",defaultValue = "1")Integer page,
                          @RequestParam(name = "size",defaultValue = "5")Integer size){


        User user = (User) request.getSession().getAttribute("user");
        //判断是否登录，未登录返回首页
        if(user==null){
            return "redirect:/";
        }


        //个人中心导航栏
        if("questions".equals(action)){
            model.addAttribute("section","questions");
            model.addAttribute("sectionName","我的提问");
            PaginationDTO paginationDTO = questionService.list(user.getId(), page, size);
            model.addAttribute("pagination",paginationDTO);
        }else if("repies".equals(action)){
            PaginationDTO paginationDTO = notificationServies.list(user.getId(),page,size);
            Long unreadCount = notificationServies.unreadCount(user.getId());
            model.addAttribute("pagination",paginationDTO);
            model.addAttribute("section","repies");
            model.addAttribute("sectionName","最新回复");


        }


        return "profile";
    }
}
