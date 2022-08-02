package com.leyou.auth.service;

import com.leyou.auth.config.JwtProperties;
import com.leyou.auth.pojo.UserInfo;
import com.leyou.auth.utils.JwtUtils;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exceptions.LyException;
import com.leyou.user.client.UserClient;
import com.leyou.user.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserClient userClient;

    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 登录
     *
     * @param username
     * @param password
     * @return
     */
    public String login(String username, String password) {

        try {
            // 远程调用，校验用户名和密码
            User user = userClient.queryByUserNameAndPassword(username, password);
            // 组织载荷数据
            UserInfo userInfo = new UserInfo(user.getId(), user.getUsername());
            // 生成token
            String token = JwtUtils.generateToken(userInfo, jwtProperties.getPrivateKey(), jwtProperties.getExpire());
            // 返回
            return token;
        } catch (Exception e) {
            throw new LyException(ExceptionEnum.INVALID_USERNAME_PASSWORD);
        }
    }
}
