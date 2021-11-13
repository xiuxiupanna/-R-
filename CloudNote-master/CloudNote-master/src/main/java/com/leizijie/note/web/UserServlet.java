package com.leizijie.note.web;

import com.leizijie.note.po.User;
import com.leizijie.note.service.UserService;
import com.leizijie.note.vo.ResultInfo;
import org.apache.commons.io.FileUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;

/**
 * Created by HMF on 2021/07/10 21:54
 */

/**
 * 个人中心模块
 */
@WebServlet("/user")
@MultipartConfig  // 文件上传需要在提交到的servlet中添加该注解
public class UserServlet extends HttpServlet {  // 实现 servlet 规范
    // web(controller) 层调用 service 层
    private UserService userService = new UserService();

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 设置首页导航高亮
        request.setAttribute("menu_page", "user");

        // 处理请求乱码问题
//        request.setCharacterEncoding("utf-8");  // 统一在 EncodingFilter 里过滤了
        // 接收用户的行为 ----------------
        String actionName = request.getParameter("actionName"); // 参数与<input>标签的name属性一致

        // 一个标志，解决个人中心修改提交后，刷新页面出现空白的问题
        String updateUserFlag = request.getParameter("updateUserActionName"); // 这是从input标签提交上来的参数

        // 判断用户的行为，调用对应的方法
        if ("login".equals(actionName)) {
            // 用户登录
            userLogin(request, response);
        } else if ("logout".equals(actionName)) {
            // 用户退出
            userLogOut(request, response);
        } else if ("userCenter".equals(actionName) && ("".equals(updateUserFlag) || updateUserFlag == null)) {
            // 进入个人中心
            userCenter(request, response);
        } else if ("userHead".equals(actionName)) {
            // 加载头像
            userHead(request, response);
        } else if ("checkNick".equals(actionName)) {
            // 验证昵称的唯一性
            checkNick(request, response);
        } else if ("userCenter".equals(actionName) && "updateUser".equals(updateUserFlag)) {
            // 修改用户信息
            updateUser(request, response);
        }
    }


    /**
     * Web层：（控制层：接收参数、响应数据）
     * 1. 获取参数 （姓名、密码）
     * 2. 调用Service层的方法，返回ResultInfo对象
     * 3. 判断是否登录成功
     * 如果失败
     * 将resultInfo对象设置到request作用域中
     * 请求转发跳转到登录页面
     * 如果成功
     * 将用户信息设置到session作用域中
     * 判断用户是否选择记住密码（rem的值是1）
     * 如果是，将用户姓名与密码存到cookie中，设置失效时间，并响应给客户端
     * 如果否，清空原有的cookie对象
     * 重定向跳转到index页面
     *
     * @param request
     * @param response
     */
    private void userLogin(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 1.获取表单提交的请求参数（姓名、密码）
        String userName = request.getParameter("userName");
        String userPwd = request.getParameter("userPwd");

        // 2.调用Service层的方法，返回ResultInfo对象
        ResultInfo<User> resultInfo = userService.userLogin(userName, userPwd);

        // 3.判断是否登录成功
        if (resultInfo.getCode() == 1) { // 如果成功
            // 将用户信息设置到session作用域中
            request.getSession().setAttribute("user", resultInfo.getResult());
            // 判断用户是否选择记住密码（rem的值是1）
            String rem = request.getParameter("rem");
            // 如果是，将用户姓名与密码存到cookie中，设置失效时间，并响应给客户端
            if ("1".equals(rem)) {
                // 得到Cookie对象
                Cookie cookie = new Cookie("user", userName + "-" + userPwd);
                // 设置失效时间
                cookie.setMaxAge(3 * 24 * 60 * 60);
                // 响应给客户端
                response.addCookie(cookie);
            } else {
                // 如果否，清空原有的cookie对象
                Cookie cookie = new Cookie("user", null);
                // 删除cookie, 设置maxAge为0，即设置cookie失效
                cookie.setMaxAge(0);
                // 响应给客户端
                response.addCookie(cookie);
            }
            // 重定向跳转到index页面
            response.sendRedirect("index"); // 通过重定向懂IndexServlet控制器，设置请求域参数后，动态显示index.jsp右边的页面
        } else { // 如果失败
            // 将resultInfo对象设置到request作用域中
            request.setAttribute("resultInfo", resultInfo);
            // 请求转发跳到登录页面
            request.getRequestDispatcher("login.jsp").forward(request, response);
        }
    }

    /**
     * 用户退出：
     * --1. 销毁Session对象
     * --2. 删除Cookie对象
     * --3. 重定向跳转到登录页面
     *
     * @param request
     * @param response
     */
    private void userLogOut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 1. 销毁Session对象
        request.getSession().invalidate();
        // 2. 删除Cookie对象，由于cookie没有单独的删除方法，所以设置他的过期时间为0，就好了
        Cookie cookie = new Cookie("user", null);
        cookie.setMaxAge(0); // 设置0，表示删除cookie
        response.addCookie(cookie); // 把cookie响应出去
        // 3. 重定向跳转到登录页面
        response.sendRedirect("login.jsp");
    }

    /**
     * 进入个人中心
     * --1、设置首页动态包含的页面值
     * --2、请求转发跳转到index.jsp
     *
     * @param request
     * @param response
     */
    private void userCenter(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 1.设置首页动态包含的页面值
        request.setAttribute("changePage", "user/info.jsp"); // 设置需要的参数后再次请求转发到目标页面

        // 2.请求转发跳转到index.jsp
        request.getRequestDispatcher("index.jsp").forward(request, response);
    }

    /**
     * 加载头像：
     * --1. 获取参数 （图片名称）
     * --2. 得到图片的存放路径 （request.getServletContext().getealPathR("/")）
     * --3. 通过图片的完整路径，得到file对象
     * --4. 通过截取，得到图片的后缀
     * --5. 通过不同的图片后缀，设置不同的响应的类型
     * --6. 利用FileUtils的copyFile()方法，将图片拷贝给浏览器
     *
     * @param request
     * @param response
     */
    private void userHead(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 1. 获取参数 （图片名称），是从user对象的head字段中取的
        String head = request.getParameter("imageName");
        // 2. 得到图片的存放路径 （得到项目的真实路径：request.getServletContext().getealPathR("/")）
        String realPath = request.getServletContext().getRealPath("/WEB-INF/upload");
        // 3. 通过图片的完整路径，得到file对象
        File file = new File(realPath + "/" + head);
        // 4. 通过截取，得到图片的后缀，从最后一个 . 开始截取，不包含 . 在内，所以 +1
        String pic = head.substring(head.lastIndexOf(".") + 1);
        // 5. 通过不同的图片后缀，设置不同的响应的类型
        if ("PNG".equalsIgnoreCase(pic)) {
            response.setContentType("image/png");
        } else if ("JPG".equalsIgnoreCase(pic) || "JPEG".equalsIgnoreCase(pic)) {
            response.setContentType("image/jpeg");
        } else if ("GIF".equalsIgnoreCase(pic)) {
            response.setContentType("image/gif");
        }
        // 6. 利用org.apache.commons.io.FileUtils的copyFile()方法，将图片拷贝给浏览器
        FileUtils.copyFile(file, response.getOutputStream());
    }

    /**
     * 验证昵称的唯一性：
     * --1. 获取参数（昵称）
     * --2. 从session作用域获取用户对象，得到用户ID
     * --3. 调用Service层的方法，得到返回的结果
     * --4. 通过字符输出流将结果响应给前台的ajax的回调函数
     * --5. 关闭资源
     *
     * @param request
     * @param response
     */
    private void checkNick(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 1. 获取参数（昵称）
        String nick = request.getParameter("nick");
        // 2. 从session作用域获取用户对象，得到用户ID
        User user = (User) request.getSession().getAttribute("user");
        // 3. 调用Service层的方法，得到返回的结果
        Integer code = userService.checkNick(nick, user.getUserId());
        // 4. 通过字符输出流将结果响应给前台的ajax的回调函数
        response.getWriter().write(code + ""); // code需要转为字符串去响应
        // 5. 关闭资源
        response.getWriter().close();
    }

    /**
     * 修改用户信息：
     * --注：文件上传必须在Servlet类上加上注解！！！ @MultipartConfig
     * --1.调用Service层的方法，传递request对象作为参数，返回resultInfo对象
     * --2.将resultInfo对象存到request作用域中
     * --3.请求转发跳转到个人中心页面 （user?actionName=userCenter）
     *
     * @param request
     * @param response
     */
    private void updateUser(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 注：文件上传必须在Servlet类上加上注解！！！ @MultipartConfig
        // 1. 调用Service层的方法，传递request对象作为参数，返回resultInfo对象
        ResultInfo<User> resultInfo = userService.updateUser(request);
        // 2. 将resultInfo对象存到request作用域中
        request.setAttribute("resultInfo", resultInfo);
        // 3. 请求转发跳转到个人中心页面 （user?actionName=userCenter&updateUserActionName=）
        // 通过设置转发请求参数updateUserActionName= 为空来删除参数，阻断他再次传递
        request.getRequestDispatcher("user?actionName=userCenter&updateUserActionName=").forward(request, response);
    }
}
