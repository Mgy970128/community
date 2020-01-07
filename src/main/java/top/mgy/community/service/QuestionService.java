package top.mgy.community.service;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.mgy.community.dto.PaginationDTO;
import top.mgy.community.dto.QuestionDTO;
import top.mgy.community.dto.QuestionQueryDTO;
import top.mgy.community.exception.CustomErrorCode;
import top.mgy.community.exception.CustomizeException;
import top.mgy.community.mapper.QuestionExtMapper;
import top.mgy.community.mapper.QuestionMapper;
import top.mgy.community.mapper.UserMapper;
import top.mgy.community.model.Question;
import top.mgy.community.model.QuestionExample;
import top.mgy.community.model.User;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class QuestionService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private QuestionExtMapper questionExtMapper;

    public PaginationDTO list(String search, Integer page, Integer size) {

        //判断查询关键字是否传入
        if(StringUtils.isNotBlank(search)){
            String[] tags = StringUtils.split(search, " ");
            search = Arrays.stream(tags).collect(Collectors.joining("|"));
        }


        if(page < 1){
            page = 1;
        }
        PaginationDTO paginationDTO =new  PaginationDTO();
        //问题列表
        List<QuestionDTO> questionDTOList = new ArrayList<>();
        //查询总问题数
        //Integer totalCount = (int) questionMapper.countByExample(new QuestionExample());
        QuestionQueryDTO questionQueryDTO = new QuestionQueryDTO();
        questionQueryDTO.setSearch(search);
        Integer totalCount = questionExtMapper.countBySearch(questionQueryDTO);

        paginationDTO.setPagination(totalCount,page,size);
        //总页码
        Integer totalPage = 0;
        //计算总页码
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
        //分页查询
        QuestionExample questionExample = new QuestionExample();
        questionExample.setOrderByClause("gmt_create desc");
        questionQueryDTO.setSize(size);
        questionQueryDTO.setPage(offset);
        //List<Question> list = questionMapper.selectByExampleWithRowbounds(questionExample, new RowBounds(offset, size));
        List<Question> list = questionExtMapper.selectBySearch(questionQueryDTO);


        //将question转为QuestionDTO
        for (Question question : list) {
            User user = userMapper.selectByPrimaryKey(question.getCreator());
            QuestionDTO questionDTO = new QuestionDTO();
            BeanUtils.copyProperties(question,questionDTO);
            questionDTO.setUser(user);
            questionDTOList.add(questionDTO);
        }
        paginationDTO.setData(questionDTOList);
        //获取问题总数

        return paginationDTO;
    }

    //查询我的提问问题列表
    public PaginationDTO list(Long userId, Integer page, Integer size) {
        if(page < 1){
            page = 1;
        }
        PaginationDTO paginationDTO =new  PaginationDTO();
        //问题列表
        List<QuestionDTO> questionDTOList = new ArrayList<>();
        //查询单个用户问题
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
        paginationDTO.setData(questionDTOList);
        //获取问题总数

        return paginationDTO;
    }

    public QuestionDTO getById(Long id) {

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
            question.setViewCount(0);
            question.setLikeCount(0);
            question.setCommentCount(0);
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

    public void incView(Long id) {
        Question question = new Question();
        question.setId(id);
        question.setViewCount(1);
        questionExtMapper.incView(question);

    }

    /**
     * 查询相关问题
     * @param quueryDTO
     * @return
     */
    public List<QuestionDTO> selectRelated(QuestionDTO quueryDTO) {

        if(StringUtils.isBlank(quueryDTO.getTag())){
            return new ArrayList<>();
        }

        String[] tags = StringUtils.split(quueryDTO.getTag(), ",");
        //将tags用 |分割，方便sql中正则使用
        String regexTag = Arrays.stream(tags).collect(Collectors.joining("|"));
        Question question = new Question();
        question.setId(quueryDTO.getId());
        question.setTag(regexTag);
        List<Question> questions = questionExtMapper.selectRelated(question);
        //将 question 转为 questionDTO
        List<QuestionDTO> questionDTOS = questions.stream().map(q -> {
            QuestionDTO questionDTO = new QuestionDTO();
            BeanUtils.copyProperties(q, questionDTO);
            return questionDTO;
        }).collect(Collectors.toList());
        return questionDTOS;

    }
}
