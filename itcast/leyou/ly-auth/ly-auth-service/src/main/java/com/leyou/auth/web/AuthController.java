package com.leyou.auth.web;

import com.leyou.auth.config.JwtProperties;
import com.leyou.auth.pojo.UserInfo;
import com.leyou.auth.service.AuthService;
import com.leyou.auth.utils.JwtUtils;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exceptions.LyException;
import com.leyou.common.utils.CookieUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private JwtProperties jwtProperties;


    /**
     * 登录
     *
     * @param username
     * @param password
     * @return
     */
    @PostMapping("login")
    public ResponseEntity<Void> login(@RequestParam("username") String username,
                                      @RequestParam("password") String password,
                                      HttpServletResponse response,
                                      HttpServletRequest request) {
        // 登录
        String token = authService.login(username, password);
        // 写入 Cookie
        CookieUtils.newBuilder().name(jwtProperties.getCookieName()).value(token)
                .httpOnly(true).request(request).response(response).build();
        // 返回
        return ResponseEntity.ok().build();
        
    }

    /**
     * 校验用户是否登录
     * @param token
     * @return
     */
    @GetMapping("verify")
    public ResponseEntity<UserInfo> verify(@CookieValue("LY_TOKEN") String token,
                                            HttpServletRequest request, HttpServletResponse response) {
        // 校验token
        try {
            UserInfo userInfo = JwtUtils.getInfoFromToken(token, jwtProperties.getPublicKey());
            // 登录有效, 生成新token
            String newToken = JwtUtils.generateToken(userInfo, jwtProperties.getPrivateKey(), jwtProperties.getExpire());
            //刷新用户的token
            CookieUtils.newBuilder().name(jwtProperties.getCookieName()).value(newToken)
                    .httpOnly(true).request(request).response(response).build();
            return ResponseEntity.ok(userInfo);

        } catch (Exception e) {
            throw new LyException(ExceptionEnum.UNAUTHORIZED);
        }

    }

}
