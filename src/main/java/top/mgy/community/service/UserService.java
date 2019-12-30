package top.mgy.community.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.mgy.community.mapper.UserMapper;
import top.mgy.community.model.User;
import top.mgy.community.model.UserExample;

import java.util.List;


@Service
public class UserService {

    @Autowired
    UserMapper userMapper;

    public void createOrUpdate(User user) {

        UserExample userExample = new UserExample();
        userExample.createCriteria().andAccountIdEqualTo(user.getAccountId());
        List<User> users = userMapper.selectByExample(userExample);


        //User dbUser = userMapper.findByAccountId(user.getAccountId());
        if(users.size() == 0){
            //插入
            user.setGmtCreate(System.currentTimeMillis());
            user.setGmtModified(user.getGmtCreate());
            userMapper.insert(user);
        }else {
            //更新
            User dbUser = users.get(0);
            User updateUser = new User();
            updateUser.setAvatarUrl(user.getAvatarUrl());
            updateUser.setGmtModified(System.currentTimeMillis());
            updateUser.setName(user.getName());
            updateUser.setToken(user.getToken());
            UserExample example = new UserExample();
            //设置更新条件
            example.createCriteria().andIdEqualTo(dbUser.getId());
            //更新
            userMapper.updateByExampleSelective(updateUser,example);
            //userMapper.update(dbUser);
        }
    }
}
