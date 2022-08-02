package cn.itcast.service;

import cn.itcast.mapper.UserMapper;
import cn.itcast.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;

    @Transactional(readOnly = true) // 查询时事务可以不用配置. 如果想配置 @Transactional(readOnly = true)
    public User queryUserById(Long id) {
        userMapper.selectByPrimaryKey(id);

        return null;
    }


    @Transactional
    public int saveUser(User user) {
        return userMapper.insertSelective(user);

    }
}
