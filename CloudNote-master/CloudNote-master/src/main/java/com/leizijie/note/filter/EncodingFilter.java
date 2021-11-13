package com.leizijie.note.filter;

import cn.hutool.core.util.StrUtil;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by HMF on 2021/07/11 21:21
 */

/**
 * ****************************  字符乱码过滤器 *******************************
 *
 * 请求乱码解决：
 * --乱码原因：
 * ----服务器默认的解析编码为 ISO-8850-1，不支持中文
 * <p>
 * --乱码情况：
 * ----GET请求：
 * ------Tomcat7及以下版本       乱码
 * ------Tomcat8及以上版本       不乱码（因为Tomcat8及以上自身处理过）
 * --
 * ----POST请求：
 * ------Tomcat7及以下版本       乱码
 * ------Tomcat8及以上版本       乱码
 * <p>
 * --解决方案：
 * ----GET请求：
 * ------Tomcat7及以下版本，会乱码，需要单独处理
 * --------new String(request.getParameater("xxx").getBytes("ISO-8859-1", "UTF-8");（这种方式比较麻烦，我们要通过重写底层代码来实现）
 * ------Tomcat8及以上版本，不会出现乱码，不需要处理，处理反而会出现乱码
 * --
 * ----POST请求：
 * ------无论是什么版本的服务器，都会出现乱码，需要通过request.setCharacterEncoding("UTF-8");设置编码格式（只针对post请求）
 */
@WebFilter("/*") // 过滤所有资源
public class EncodingFilter implements Filter { // 注意：添加的是servlet包下的Filter

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // 过滤器的初始化方法
    }

    @Override
    public void destroy() {
        // 过滤器销毁时的方法
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        // 转一下，基于HTTP
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        // 处理POST请求（只针对POST请求有效，GET请求不受影响）
        request.setCharacterEncoding("UTF-8");

        // 处理GET
        // 得到请求类型（GET|POST）
        String method = request.getMethod();

        // 如果是GET请求，则判断服务器版本
        if ("GET".equalsIgnoreCase(method)) { // 忽略大小写比较
            // 得到服务器版本，Apache Tomcat/7.0.79
            String serverInfo = request.getServletContext().getServerInfo();
            // 通过截取字符串，得到具体的版本号，从第一个 / 开始，不包含 / 所以 +1，到 . 结束
            String version = serverInfo.substring(serverInfo.lastIndexOf("/") + 1, serverInfo.indexOf("."));
            // 判断服务器版本是否是Tomcat7及以下
            if (version != null && Integer.parseInt(version) < 8) {
                // Tomcat7及以下版本的服务器的GET请求
                MyWrapper myRequest = new MyWrapper(request); // 生成下面的内部类对象，传入request参数

                // 放行资源
                filterChain.doFilter(myRequest, response);

                // return一下，不让他往后面走了
                return;
            }
        }

        // 过滤器放行
        filterChain.doFilter(request, response);
    }

    /**
     * 内部类
     * <p>
     * 1、定义内部类（类的本质是 request 对象）
     * 2、继承 HttpServletRequestWrapper 包装类
     * 3、重写 getParameter()方法
     */
    class MyWrapper extends HttpServletRequestWrapper { // HttpServletRequestWrapper 本质就是 HttpServletRequest，看他的继承关系就能知道
        // 定义成员变量 HttpServletRequest（作用：提升构造器中 request 对象的作用域）
        private HttpServletRequest request;

        /**
         * 带参构造
         * --可以得到需要处理的request对象
         *
         * @param request
         */
        public MyWrapper(HttpServletRequest request) {
            super(request);
            this.request = request; // 初始化成员变量
        }

        /**
         * 重写 getParameter()方法，处理乱码问题
         *
         * @param name
         * @return
         */
        @Override
        public String getParameter(String name) {
            // 获取参数（会乱码的参数）
            String value = request.getParameter(name);

            // 判断参数值是否为空，使用hutool工具判空
            if (StrUtil.isBlank(value)) { // 为空时
                return value;
            }

            try {
                // 通过 new String() 处理乱码
                value = new String(value.getBytes("ISO-8859-1"), "UTF-8");
            } catch (Exception e) {
                e.printStackTrace();
            }
            return value;
        }
    }

}
