package top.mgy.community.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import top.mgy.community.dto.CommentCreateDTO;
import top.mgy.community.dto.CommentDTO;
import top.mgy.community.dto.ResultDTO;
import top.mgy.community.enums.CommentTypeEunm;
import top.mgy.community.exception.CustomErrorCode;
import top.mgy.community.mapper.CommentMapper;
import top.mgy.community.model.Comment;
import top.mgy.community.model.User;
import top.mgy.community.service.CommentService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 评论Controller
 */

@Controller
public class CommentController {

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private CommentService commentService;


    /**
     * 保存用户名评论接口
     * @param commentCreateDTO
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/comment",method = RequestMethod.POST)
    public Object post(@RequestBody CommentCreateDTO commentCreateDTO, HttpServletRequest request){

        //获取user信息
        User user = (User) request.getSession().getAttribute("user");
        if(user == null){
            //用户未登录
            return ResultDTO.errorOf(CustomErrorCode.ON_LOGIN);
        }


        Comment comment = new Comment();
        //保存评论数据
        comment.setParentId(commentCreateDTO.getParentId());
        comment.setContent(commentCreateDTO.getContent());
        comment.setType(commentCreateDTO.getType());
        comment.setGmtModified(System.currentTimeMillis());
        comment.setGmtCreate(comment.getGmtModified());
        comment.setCommentator(user.getId());
        comment.setLikeCount(0L);
        commentService.insert(comment);
        return ResultDTO.okOf();
    }


    /**
     * 获取二级评论接口
     * @param id
     * @return
     */
    @ResponseBody
    @GetMapping("/comment/{id}")
    public ResultDTO<List<CommentDTO>> comments(@PathVariable("id")Long id){

        List<CommentDTO> commentDTOList = commentService.listByTargetId(id, CommentTypeEunm.COMMENT);
        return ResultDTO.okOf(commentDTOList);

    }
}
