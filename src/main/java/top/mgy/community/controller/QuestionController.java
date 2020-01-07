package top.mgy.community.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import top.mgy.community.dto.CommentDTO;
import top.mgy.community.dto.QuestionDTO;
import top.mgy.community.enums.CommentTypeEunm;
import top.mgy.community.service.CommentService;
import top.mgy.community.service.QuestionService;

import java.util.List;

@Controller
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    @Autowired
    private CommentService commentService;

    /**
     * 获取问题与评论
     * @param id
     * @param model
     * @return
     */
    @GetMapping("/question/{id}")
    public String question(@PathVariable("id") Long id, Model model){
        //累加阅读数
        questionService.incView(id);
        QuestionDTO questionDTO = questionService.getById(id);

        List<QuestionDTO>  relatedQuestion= questionService.selectRelated(questionDTO);

        List<CommentDTO>  comments= commentService.listByTargetId(id, CommentTypeEunm.QUESTION);

        model.addAttribute("question",questionDTO);
        model.addAttribute("comments",comments);
        model.addAttribute("relatedQuestion",relatedQuestion);
        return "question";
    }
}
