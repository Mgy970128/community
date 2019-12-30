package top.mgy.community.service;

import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.mgy.community.dto.PaginationDTO;
import top.mgy.community.dto.QuestionDTO;
import top.mgy.community.exception.CustomErrorCode;
import top.mgy.community.exception.CustomizeException;
import top.mgy.community.mapper.QuestionExtMapper;
import top.mgy.community.mapper.QuestionMapper;
import top.mgy.community.mapper.UserMapper;
import top.mgy.community.model.Question;
import top.mgy.community.model.QuestionExample;
import top.mgy.community.model.User;

import java.util.ArrayList;
import java.util.List;

@Service
public class QuestionService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private QuestionExtMapper questionExtMapper;

    public PaginationDTO list(Integer page, Integer size) {

        if(page < 1){
            page = 1;
        }
        PaginationDTO paginationDTO =new  PaginationDTO();
        //问题列表
        List<QuestionDTO> questionDTOList = new ArrayList<>();
        Integer totalCount = (int) questionMapper.countByExample(new QuestionExample());
        paginationDTO.setPagination(totalCount,page,size);
        //计算总页码
        Integer totalPage = 0;
        if(totalCount % size == 0){
            totalPage = totalCount / size;
        }else {
            totalPage = totalCount /size + 1;
        }
        if(page > totalPage){
            page = totalPage;
        }
        //计算页码对应的偏移量  size * (page - 1)
        Integer offset = size*(page-1);
        //获取问题
        //List<Question> list = questionMapper.list(offset,size);
        //分页查询
        List<Question> list = questionMapper.selectByExampleWithRowbounds(new QuestionExample(), new RowBounds(offset, size));

        for (Question question : list) {
            User user = userMapper.selectByPrimaryKey(question.getCreator());
            QuestionDTO questionDTO = new QuestionDTO();
            BeanUtils.copyProperties(question,questionDTO);
            questionDTO.setUser(user);
            questionDTOList.add(questionDTO);
        }
        paginationDTO.setQuestion(questionDTOList);
        //获取问题总数

        return paginationDTO;
    }

    public PaginationDTO list(Integer userId, Integer page, Integer size) {
        if(page < 1){
            page = 1;
        }
        PaginationDTO paginationDTO =new  PaginationDTO();
        //问题列表
        List<QuestionDTO> questionDTOList = new ArrayList<>();
        //Integer totalCount = questionMapper.countBuUserId(userId);
        //查询单个用户贴完
        QuestionExample questionExample = new QuestionExample();
        questionExample.createCriteria().andCreatorEqualTo(userId);
        Integer totalCount = (int)questionMapper.countByExample(questionExample);

        paginationDTO.setPagination(totalCount,page,size);
        //计算总页码
        Integer totalPage = 0;
        if(totalCount % size == 0){
            totalPage = totalCount / size;
        }else {
            totalPage = totalCount /size + 1;
        }
        if(page > totalPage){
            page = totalPage;
        }
        //计算页码对应的偏移量  size * (page - 1)
        Integer offset = size*(page-1);
        //获取问题
        QuestionExample example = new QuestionExample();
        example.createCriteria().andCreatorEqualTo(userId);
        List<Question> list = questionMapper.selectByExampleWithRowbounds(example, new RowBounds(offset, size));
        //List<Question> list = questionMapper.listByUserId(userId,offset,size);
        for (Question question : list) {
            User user = userMapper.selectByPrimaryKey(question.getCreator());
            QuestionDTO questionDTO = new QuestionDTO();
            BeanUtils.copyProperties(question,questionDTO);
            questionDTO.setUser(user);
            questionDTOList.add(questionDTO);
        }
        paginationDTO.setQuestion(questionDTOList);
        //获取问题总数

        return paginationDTO;
    }

    public QuestionDTO getById(Integer id) {

       Question question = questionMapper.selectByPrimaryKey(id);
       if(question == null){
           throw new CustomizeException(CustomErrorCode.QUESTION_NOT_FOUND);
       }
       QuestionDTO questionDTO = new QuestionDTO();
       BeanUtils.copyProperties(question,questionDTO);
        User user = userMapper.selectByPrimaryKey(question.getCreator());
        questionDTO.setUser(user);
       return questionDTO;
    }

    public void createOrUpdate(Question question) {
        if(question.getId() == null){
            //创建问题
            question.setGmtCreate(System.currentTimeMillis());
            question.setGmtModified(question.getGmtCreate());
            questionMapper.insert(question);
        }else {
            //更新问题
            Question updateQuestion = new Question();
            updateQuestion.setGmtModified(System.currentTimeMillis());
            updateQuestion.setTitle(question.getTitle());
            updateQuestion.setDescription(question.getDescription());
            updateQuestion.setTag(question.getTag());
            QuestionExample example = new QuestionExample();
            example.createCriteria().andIdEqualTo(question.getId());
            int i = questionMapper.updateByExampleSelective(updateQuestion, example);
            if(i != 1){
                throw new CustomizeException(CustomErrorCode.QUESTION_NOT_FOUND);
            }

            //questionMapper.update(question);
        }
    }

    public void incView(Integer id) {
        Question question = new Question();
        question.setId(id);
        question.setViewCount(1);
        questionExtMapper.incView(question);

    }
}
