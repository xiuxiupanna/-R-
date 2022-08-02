package com.leyou.user.service;

import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exceptions.LyException;
import com.leyou.common.utils.NumberUtils;
import com.leyou.user.mapper.UserMapper;
import com.leyou.user.pojo.User;
import com.leyou.user.utils.CodecUtils;
import lombok.Data;
import org.apache.commons.lang.StringUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private AmqpTemplate amqpTemplate;

    private static final String KEY_PREFIX = "user:verify:phone:";

    /**
     * 校验数据是否可用
     * @param data
     * @param type
     * @return
     */
    public Boolean checkData(String data, Integer type) {
        User user = new User();
        switch (type) {
            case 1:
                user.setUsername(data);
                break;
            case 2:
                user.setPhone(data);
            default:
                throw new LyException(ExceptionEnum.INVALID_PARAM_ERROR);
        }
        int count = userMapper.selectCount(user);
        return count == 0;
    }

    /**
     * 发送验证码
     * @param phone
     */
    public void sendCode(String phone) {
        // 验证手机号格式
        if (!phone.matches("^1([38][0-9]|4[579]|5[0-3,5-9]|6[6]|7[0135678]|9[89])\\d{8}$")) {
            throw new LyException(ExceptionEnum.INVALID_PHONE_NUMBER);
        }

        String key = KEY_PREFIX + phone;
        String code = NumberUtils.generateCode(6);
        // 保存验证码到redis
        redisTemplate.opsForValue().set(key, code, 5, TimeUnit.MINUTES);
        // 发送Rabbitmq 消息到 ly-sms
        Map<String, String> msg = new HashMap<>();
        msg.put("phone", phone);
        msg.put("code", code);
        amqpTemplate.convertAndSend("ly.sms.exchange", "sms.verify.code", msg);

    }

    /**
     * 注册服务
     */
    public void register(User user, String code) {
        // TODO 校验用户数据(数据格式)
        // 校验验证码
        // 取出redis的验证码
        String cacheCode = redisTemplate.opsForValue().get(KEY_PREFIX + user.getPhone());
        // 验证code
        if (!StringUtils.equals(cacheCode, code)) {
            throw new LyException((ExceptionEnum.INVALID_PARAM_ERROR));
        }
        // 生成盐
        String salt = CodecUtils.generateSalt();
        // 对密码加密
        user.setPassword(CodecUtils.md5Hex(user.getPassword(), salt));
        // 写入数据库
        user.setCreated(new Date());
        user.setSalt(salt);
        userMapper.insertSelective(user);

    }

    /**
     * 根据用户名密码查询用户
     * @param username
     * @param password
     * @return
     */
    public User queryByUserNameAndPassword(String username, String password) {
        User u = new User();
        u.setUsername(username);
        User user = userMapper.selectOne(u);
        // 判断是否存在
        if (user == null) {
            // 用户名错误
            throw new LyException(ExceptionEnum.INVALID_USERNAME_PASSWORD);

        }
        // 对密码加密
        String pw = CodecUtils.md5Hex(password, user.getSalt());
        // 用户名正确,校验密码
        if (!StringUtils.equals(user.getPassword(), pw)) {
            // 密码错误
            throw new LyException(ExceptionEnum.INVALID_USERNAME_PASSWORD);

        }
         return user;

    }
}
