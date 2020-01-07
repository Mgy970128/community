package top.mgy.community.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import top.mgy.community.dto.NotificationDTO;
import top.mgy.community.enums.NotificationStatusEnum;
import top.mgy.community.enums.NotificationTypeEnum;
import top.mgy.community.model.User;
import top.mgy.community.service.NotificationServies;

import javax.servlet.http.HttpServletRequest;

/**
 * 通知处理器
 */
@Controller
public class NotificationController {

    @Autowired
    private NotificationServies notificationServies;

    @GetMapping("/notification/{id}")
    public String profile(HttpServletRequest request, @PathVariable("id")Long id){
        User user = (User) request.getSession().getAttribute("user");
        if(user == null){
            return "redirect:/";
        }

        NotificationDTO notificationDTO = notificationServies.read(id,user);

        if(NotificationTypeEnum.REPLY_COMMENT.getType() == notificationDTO.getType()
                ||NotificationTypeEnum.REPLY_QUESTION.getType() == notificationDTO.getType()){
            return "redirect:/question/"+notificationDTO.getOuterid();
        }else {
            return "redirect:/";
        }

    }

}
