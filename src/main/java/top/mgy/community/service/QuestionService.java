package top.mgy.community.service;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.mgy.community.dto.QuestionDTO;
import top.mgy.community.mapper.QuestionMapper;
import top.mgy.community.mapper.UserMapper;
import top.mgy.community.model.Question;
import top.mgy.community.model.User;

import java.util.ArrayList;
import java.util.List;

@Service
public class QuestionService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private QuestionMapper questionMapper;

    public List<QuestionDTO> list() {

        List<QuestionDTO> questionDTOList = new ArrayList<>();
        //获取问题
        List<Question> list = questionMapper.list();
        for (Question question : list) {
            User user = userMapper.findById(question.getCreator());
            QuestionDTO questionDTO = new QuestionDTO();
            BeanUtils.copyProperties(question,questionDTO);
            questionDTO.setUser(user);
            questionDTOList.add(questionDTO);
        }
        return questionDTOList;
    }
}
