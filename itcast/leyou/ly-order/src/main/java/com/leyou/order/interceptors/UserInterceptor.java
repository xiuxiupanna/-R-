package com.leyou.order.interceptors;

import com.leyou.auth.pojo.UserInfo;
import com.leyou.auth.utils.JwtUtils;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exceptions.LyException;
import com.leyou.common.utils.CookieUtils;
import com.leyou.order.config.JwtProperties;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class UserInterceptor implements HandlerInterceptor {

    private JwtProperties properties;

    public UserInterceptor(JwtProperties properties) {
        this.properties = properties;
    }

    private static final ThreadLocal<UserInfo> TL = new ThreadLocal<>();


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        try {
            // 获取cookie 中的token
            String token = CookieUtils.getCookieValue(request, properties.getCookieName());
            // 解析token
            UserInfo userInfo = JwtUtils.getInfoFromToken(token, properties.getPublicKey());
            // 保存用户
            TL.set(userInfo);

            return true;
        } catch (Exception e) {
            throw new LyException(ExceptionEnum.UNAUTHORIZED);
        }

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        TL.remove();

    }

    public static UserInfo getUserInfo() {
        return TL.get();

    }

}
