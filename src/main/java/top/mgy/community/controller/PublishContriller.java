package top.mgy.community.controller;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import top.mgy.community.cache.TagCache;
import top.mgy.community.dto.QuestionDTO;
import top.mgy.community.model.Question;
import top.mgy.community.model.User;
import top.mgy.community.service.QuestionService;

import javax.servlet.http.HttpServletRequest;

@Controller
public class PublishContriller {

    @Autowired
    private QuestionService questionService;

    /**
     * 问题页面请求接口
     * @return
     */
    @GetMapping("/publish")
    public String publish(Model model){
        model.addAttribute("tags",TagCache.get());
        return "publish";
    }

    /**
     * 问题提交接口
     * @param title
     * @param discription
     * @param tag
     * @param id
     * @param request
     * @param model
     * @return
     */
    @PostMapping("/publish")
    public String doPublisgh(
    @RequestParam("title") String title,
    @RequestParam("discription") String discription,
    @RequestParam("tag") String tag,
    @RequestParam("id") Long id,
    HttpServletRequest request,
    Model model
        ){
        model.addAttribute("title",title);
        model.addAttribute("discription",discription);
        model.addAttribute("tag",tag);
        model.addAttribute("tags",TagCache.get());


        String invalid = TagCache.FilterInvalid(tag);
        if(StringUtils.isNotBlank(invalid)){
            model.addAttribute("error","输入非法标签! "+invalid);
            return "publish";
        }

        User user = (User) request.getSession().getAttribute("user");
        if(user == null){
            model.addAttribute("error","用户尚未登录");
            return "publish";
        }



        Question question = new Question();
        question.setTitle(title);
        question.setDescription(discription);
        question.setTag(tag);
        question.setCreator(user.getId());

        question.setId(id);
        questionService.createOrUpdate(question);
        return "redirect:/";
    }

    /**
     * 问题编辑接口
     * @param id
     * @param model
     * @return
     */
    @GetMapping("/publish/{id}")
    public String edit(@PathVariable("id") Long id,Model model){

        QuestionDTO question = questionService.getById(id);
        model.addAttribute("title",question.getTitle());
        model.addAttribute("discription",question.getDescription());
        model.addAttribute("tag",question.getTag());
        model.addAttribute("id",question.getId());
        model.addAttribute("tags",TagCache.get());
        return "publish";
    }
}
