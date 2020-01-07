package top.mgy.community.service;

import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.mgy.community.dto.NotificationDTO;
import top.mgy.community.dto.PaginationDTO;
import top.mgy.community.dto.QuestionDTO;
import top.mgy.community.enums.NotificationStatusEnum;
import top.mgy.community.enums.NotificationTypeEnum;
import top.mgy.community.exception.CustomErrorCode;
import top.mgy.community.exception.CustomizeException;
import top.mgy.community.mapper.NotificationMapper;
import top.mgy.community.mapper.UserMapper;
import top.mgy.community.model.*;

import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Service
public class NotificationServies {

    @Autowired
    private NotificationMapper notificationMapper;

    @Autowired
    private UserMapper userMapper;

    public PaginationDTO list(Long userId, Integer page, Integer size) {

        if(page < 1){
            page = 1;
        }
        PaginationDTO<NotificationDTO> paginationDTO =new  PaginationDTO();
        //查询单个用户贴完
        NotificationExample notificationExample = new NotificationExample();
        notificationExample.createCriteria().andReceiverEqualTo(userId);
        Integer totalCount = (int)notificationMapper.countByExample(notificationExample);

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
        //获取通知消息
        NotificationExample example = new NotificationExample();
        example.createCriteria().andReceiverEqualTo(userId);
        example.setOrderByClause("gmt_create desc");
        List<Notification> notifications = notificationMapper.selectByExampleWithRowbounds(example, new RowBounds(offset, size));

        if(notifications.size() == 0){
            return paginationDTO;
        }
        List<NotificationDTO> notificationDTOS = new ArrayList<>();

        for (Notification notification : notifications) {
            NotificationDTO notificationDTO = new NotificationDTO();
            BeanUtils.copyProperties(notification,notificationDTO);
            notificationDTO.setTypeName(NotificationTypeEnum.nameOfType(notification.getType()));
            notificationDTOS.add(notificationDTO);
        }
        paginationDTO.setData(notificationDTOS);
        //获取问题总数
        return paginationDTO;

    }

    public Long unreadCount(Long userId) {
        NotificationExample notificationExample = new NotificationExample();
        notificationExample.createCriteria().andReceiverEqualTo(userId).andStatusEqualTo(NotificationStatusEnum.UNREAD.getStatus());
        return notificationMapper.countByExample(notificationExample);

    }

    /**
     * 读取消息处理
     * @param id
     * @param user
     * @return
     */
    public NotificationDTO read(Long id, User user) {

        Notification notification = notificationMapper.selectByPrimaryKey(id);
        if(notification == null){
            throw new CustomizeException(CustomErrorCode.NOTIFICATION_NOT_FOUND);
        }
        if(!Objects.equals(notification.getReceiver(),user.getId())){
            throw  new CustomizeException(CustomErrorCode.RDAD_NOTIFICATION_FAIL);
        }
        //设置已读
        notification.setStatus(NotificationStatusEnum.READ.getStatus());
        notificationMapper.updateByPrimaryKey(notification);

        NotificationDTO notificationDTO = new NotificationDTO();
        BeanUtils.copyProperties(notification,notificationDTO);
        notificationDTO.setTypeName(NotificationTypeEnum.nameOfType(notification.getType()));
        return notificationDTO;
    }
}
