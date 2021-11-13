package com.leizijie.note.filter;

import com.leizijie.note.po.User;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by HMF on 2021/07/12 09:21
 */

/**
 * 非法访问拦截
 * --拦截所有资源：
 * ----所有的资源   /*
 * <p>
 * ----需要被放行的资源：
 * ------1、指定页面，放行（用户无需登录即可访问的页面；例如：登录页面login.jsp、注册页面register.jsp
 * ------2、静态资源，放行（存放在statics目录下的资源；例如：js、css、images等）
 * ------3、指定行为，放行（用户无需登录即可执行的操作；例如：登录操作actionName=login等)
 * ------4、登录状态，放行（判断session作用域中是否存在user对象；存在则放行，不存在，则拦截跳转到登录页面）
 * --
 * 免登陆（自动登录）
 * --通过Cookie对象实现
 * --
 * --什么时候需要免登陆：
 * ----当用户处于未登录状态，且去请求需要登录才能访问的资源时，调用自动登录功能
 * --
 * ----目的：让用户处于登录状态（自动调用免登录方法）
 * --
 * ----实现：
 * ------从Cookie对象中获取用户的姓名与密码，自动执行登录操作
 * --------1、获取Cookie数据， request.getCookies()
 * --------2、判断Cookie数组
 * --------3、遍历Cookie数组，获取指定的Cookie对象（name为user的Cookie对象）
 * --------4、得到对应的cookie对象的value（姓名与密码：userName-userPwd)
 * --------5、通过split()方法将value字符串分割成数组
 * --------6、从数组中分别得到对应的姓名与密码值
 * --------7、请求转发到登录操作 /user?actionName=login&userName=姓名&userPwd=密码
 * --------8、return
 * <p>
 * --如果以上判断都不满足，则拦截跳转到登录页面
 */
@WebFilter("/*")
public class LoginAccessFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        //
    }

    @Override
    public void destroy() {
        //
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        // 转一下，基于http
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        // 得到访问的路径
        String path = request.getRequestURI(); // 格式：项目路径/资源路径

        // 1、指定页面，放行（用户无需登录即可访问的页面；例如：登录页面login.jsp、注册页面register.jsp
        if (path.contains("/login.jsp")) { // path.contains(s)方法：当且仅当字符串path包含s时，返回true，否则返回false
            filterChain.doFilter(request, response); // 放行
            return; //后面代码没必要走了，所以直接return掉
        }

        // 2、静态资源，放行（存放在statics目录下的资源；例如：js、css、images等）
        if (path.contains("/static")) {
            filterChain.doFilter(request, response); // 放行
            return; //后面代码没必要走了，所以直接return掉
        }

        // 3、指定行为，放行（用户无需登录即可执行的操作；例如：登录操作actionName = login等)
        if (path.contains("/user")) {
            // 得到用户行为
            String actionName = request.getParameter("actionName");
            // 判断是否是登录操作
            if ("login".equals(actionName)) {
                filterChain.doFilter(request, response); // 放行
                return; //后面代码没必要走了，所以直接return掉
            }
        }

        // 4、登录状态，放行（判断session作用域中是否存在user对象；存在则放行，不存在，则拦截跳转到登录页面）
        // 获取Session作用域中的user对象，因为登录成功后，会把user对象存在session中
        User user = (User) request.getSession().getAttribute("user");
        // 判断user对象是否为空
        if (user != null) {
            filterChain.doFilter(request, response); // 放行
            return;
        }

        /**
         * 免登录（自动登录）：
         *   从Cookie对象中获取用户的姓名与密码，自动执行登录操作
         */
        // 1、获取Cookie数据， request.getCookies()
        Cookie[] cookies = request.getCookies();
        // 2、判断Cookie数组
        if (cookies != null && cookies.length > 0) {
            // 3、遍历Cookie数组，获取指定的Cookie对象（name为user的Cookie对象）
            for (Cookie cookie : cookies) {
                if ("user".equals(cookie.getName())) { // 获取指定name的cookie
                    // 4、得到对应的cookie对象的value（姓名与密码：userName-userPwd)
                    String value = cookie.getValue(); // 如：admin-123456
                    // 5、通过split()方法将value字符串分割成数组
                    String[] val = value.split("-");
                    // 6、从数组中分别得到对应的姓名与密码值
                    String userName = val[0];
                    String userPwd = val[1];
                    // 7、请求转发到登录操作 /ser?actionName=login&userName=姓名&userPwd=密码，拼接请求地址和请求参数
                    String url = "user?actionName=login&rem=1&userName=" + userName + "&userPwd=" + userPwd;
//                    request.getRequestDispatcher(url).forward(request, response); // 请求转发时，在userServlet中获取不到请求参数
                    response.sendRedirect(url); // 这里要使用重定向 ---------------
                    // 8、return
                    return;
                }
            }
        }

        // 如果前面的判断都没有走，就拦截请求，重定向跳转到登录页面login.jsp
        response.sendRedirect("login.jsp");
    }
}
