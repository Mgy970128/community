package top.mgy.community.service;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.mgy.community.dto.CommentDTO;
import top.mgy.community.enums.CommentTypeEunm;
import top.mgy.community.exception.CustomErrorCode;
import top.mgy.community.exception.CustomizeException;
import top.mgy.community.mapper.CommentMapper;
import top.mgy.community.mapper.QuestionExtMapper;
import top.mgy.community.mapper.QuestionMapper;
import top.mgy.community.mapper.UserMapper;
import top.mgy.community.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CommentService {

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private QuestionExtMapper questionExtMapper;

    @Autowired
    private UserMapper userMapper;


    /**
     * 问题回复保存
     * @param comment
     */
    @Transactional(rollbackFor = Exception.class)
    public void insert(Comment comment) {
        if(comment.getParentId() == null || comment.getParentId() == 0){
            throw new CustomizeException(CustomErrorCode.TARGET_PARAM_NOT_FOUND);
        }

        if(comment.getType() == null || !CommentTypeEunm.isExist(comment.getType())){
            throw new CustomizeException(CustomErrorCode.TYPE_PARAM_NOT_WRONG);
        }

        if(comment.getType().equals(CommentTypeEunm.COMMENT.getType())){
            //回复评论
            //先查询该问题是否存在
            Comment dbComment = commentMapper.selectByPrimaryKey(comment.getParentId());
            if(dbComment == null){
                //回复的评论不存在
                throw new CustomizeException(CustomErrorCode.COMMENT_NOT_FOUND);
            }
            commentMapper.insert(comment);
        }else {
            //回复问题
            Question question = questionMapper.selectByPrimaryKey(comment.getParentId());
            if(question == null){
                throw new CustomizeException(CustomErrorCode.QUESTION_NOT_FOUND);
            }
            //保存评论
            commentMapper.insert(comment);
            //问题评论加1
            question.setCommentCount(1);
            questionExtMapper.incCommentCount(question);
        }

    }

    public List<CommentDTO> listByTargetId(Long id, CommentTypeEunm type) {
        CommentExample example = new CommentExample();
        example.createCriteria().andParentIdEqualTo(id).andTypeEqualTo(type.getType());
        example.setOrderByClause("gmt_create desc");
        List<Comment> comments = commentMapper.selectByExampleWithBLOBs(example);

        if(comments.size() == 0){
            return new ArrayList<>();
        }
        //获取所有去重评论用户id
        Set<Long> commentators = comments.stream().map(comment -> comment.getCommentator()).collect(Collectors.toSet());
        //转为List
        List<Long> userIds = commentators.stream().collect(Collectors.toList());
        UserExample userExample = new UserExample();
        userExample.createCriteria().andIdIn(userIds);
        List<User> users = userMapper.selectByExample(userExample);
        //将userid与user对象分装为一个map对象,避免双重for
        Map<Long, User> userMap = users.stream().collect(Collectors.toMap(user -> user.getId(), user -> user));

        //转换comment 为 commentDTO
        List<CommentDTO> commentDTOS = comments.stream().map(comment -> {
            CommentDTO commentDTO = new CommentDTO();
            BeanUtils.copyProperties(comment, commentDTO);
            commentDTO.setUser(userMap.get(comment.getCommentator()));
            commentDTO.getUser().setToken(null);
            commentDTO.getUser().setGmtModified(null);
            commentDTO.getUser().setId(null);
            return commentDTO;
        }).collect(Collectors.toList());

        return commentDTOS;
    }
}
