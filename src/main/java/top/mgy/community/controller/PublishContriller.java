package top.mgy.community.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import top.mgy.community.mapper.QuestionMapper;
import top.mgy.community.mapper.UserMapper;
import top.mgy.community.model.Question;
import top.mgy.community.model.User;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

@Controller
public class PublishContriller {

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private UserMapper userMapper;

    @GetMapping("/publish")
    public String publish(){
        return "publish";
    }

    @PostMapping("/publish")
    public String doPublisgh(
    @RequestParam("title") String title,
    @RequestParam("discription") String discription,
    @RequestParam("tag") String tag,
    HttpServletRequest request,
    Model model
        ){
        model.addAttribute("title",title);
        model.addAttribute("discription",discription);
        model.addAttribute("tag",tag);

        User user = null;
        Cookie[] cookies = request.getCookies();
        //获取当前登录用户
        if(cookies!=null && cookies.length != 0){
            for (Cookie cookie : cookies) {
                if(cookie.getName().equals("token")){
                    user = userMapper.findToken(cookie.getValue());
                    break;
                }
            }
        }



        if(user == null){
            model.addAttribute("error","用户尚未登录");
            return "publish";
        }

        Question question = new Question();
        question.setTitle(title);
        question.setDescription(discription);
        question.setTag(tag);
        question.setCreator(user.getId());
        question.setGmtCreate(System.currentTimeMillis());
        question.setGmtModified(question.getGmtCreate());
        questionMapper.create(question);

        return "redirect:/";
    }
}
