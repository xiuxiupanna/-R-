package com.leyou.gateway.filters;

import com.leyou.auth.pojo.UserInfo;
import com.leyou.auth.utils.JwtUtils;
import com.leyou.common.utils.CookieUtils;
import com.leyou.gateway.config.FilterProperties;
import com.leyou.gateway.config.JwtProperties;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Component
public class AuthFilter extends ZuulFilter {

    @Autowired
    private JwtProperties properties;

    @Autowired
    private FilterProperties filterProperties;


    @Override
    public String filterType() {
        return FilterConstants.PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        return FilterConstants.FORM_BODY_WRAPPER_FILTER_ORDER - 1;
    }

    /**
     * 过滤器是否生效
     * @return
     */
    @Override
    public boolean shouldFilter() {
        // 获取上下文
        RequestContext ctx = RequestContext.getCurrentContext();
        // 获取 request
        HttpServletRequest request = ctx.getRequest();
        // 获取请求路径
        String path = request.getRequestURI();
        // 判断是否允许通过
        boolean result = isAllowPath(path);



        // 如果需要放行,这里需要返回false;反之应该是true
        return !result;
    }

    /**
     *  判断是否放行
     * @param path
     * @return
     */
    private boolean isAllowPath(String path) {
        // 获取白名单
        List<String> allowPaths = filterProperties.getAllowPaths();
        for (String allowPath : allowPaths) {
            // 判断当前请求路径, 是否已白名单路径为前缀
            if (path.startsWith(allowPath)) {
                return true;

            }
        }
        return false;
    }

    @Override
    public Object run() throws ZuulException {
        // 获取上下文
        RequestContext ctx = RequestContext.getCurrentContext();
        // 获取用户的cookie中的token
        String token = CookieUtils.getCookieValue(ctx.getRequest(), properties.getCookieName());
        // 校验token是否正确
        try {
            UserInfo user = JwtUtils.getInfoFromToken(token, properties.getPublicKey());
            // TODO 校验用户权限

        } catch (Exception e) {
            // 证明登录过期或者没有登录
            ctx.setSendZuulResponse(false);
            // 返回 401
            ctx.setResponseStatusCode(401);
        }
        return null;
    }
}
