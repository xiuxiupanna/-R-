package com.lezijie.note.filter;

import cn.hutool.core.util.StrUtil;
import com.sun.org.apache.bcel.internal.generic.IF_ACMPEQ;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Struct;

/**
 * 请求乱码解决
 *     乱码原因：
 *         服务器默认的解析编码为IOS-8859-1,不支持中文
 *     乱码情况：
 *         POST请求
 *              tomcat7及以下版本       乱码
 *              tomcat8及以上版本       乱码
 *         GET请求
 *              tomcat7及以下版本       乱码
 *              tomcat8及以上版本       不会乱码
 *
 *
 */
@WebFilter("/*")
public class EncodingFilter implements Filter {


    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        //基于HTTP
        HttpServletRequest request =(HttpServletRequest) servletRequest;
        HttpServletResponse response =(HttpServletResponse) servletResponse;

        //处理POST请求
        request.setCharacterEncoding("utf-8");
        //得到请求类型
        String method =request.getMethod();

        //如果是GET请求,则判断服务器版本
        if ("GET".equalsIgnoreCase(method)) {
            //得到服务器版本
            String serverInfo = request.getServletContext().getServerInfo(); // Apache Tomcat/7.0.79
            //通过截取字符串,得到具体的版本号
            String version = serverInfo.substring(serverInfo.lastIndexOf("/") + 1, serverInfo.indexOf("."));
            //判断服务器版本是否是tomcat7 以下
            if (version != null && Integer.parseInt(version) < 8 ) {
                //tomcat7及以下版本服务器的Get请求
                MyWrapper myRequest = new MyWrapper(request);
                filterChain.doFilter(myRequest, response);
                return;
            }

        }
        filterChain.doFilter(request, response);


    }
    /**
     * 定义内部类(其本质是request对象)
     */
    class MyWrapper extends HttpServletRequestWrapper {
        //定义成员变量 HttpServletRequest 对象 (提升构造器中request 对象的作用域)
        private HttpServletRequest request;

        /**
         * 带参构造
         *   可以得到需要处理的 request对象
         * @param request
         */
        public MyWrapper(HttpServletRequest request) {
            super(request);
        }

        /**
         * 重写 getParameter 方法, 处理乱码问题
         * @param name
         * @return
         */
        @Override
        public String getParameter(String name) {
            //获取参数 (乱码的参数值)
            String value = request.getParameter(name);
            //判断参数是否为空
            if (StrUtil.isBlank(value)) {
                return value;

            }
            //通过new String() 处理乱码
            try {
                value = new String(value.getBytes("ISO-8859-1"),"UTF-8");

            } catch (Exception e) {
                e.printStackTrace();

            }

            return value;
        }
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void destroy() {

    }
}
