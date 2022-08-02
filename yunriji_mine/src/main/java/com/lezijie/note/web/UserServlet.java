package com.lezijie.note.web;

import com.lezijie.note.po.User;
import com.lezijie.note.service.UserService;
import com.lezijie.note.vo.ResultInfo;
import org.apache.commons.io.FileUtils;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.Result;
import java.io.File;
import java.io.IOException;

@WebServlet("/user")
@MultipartConfig
public class UserServlet extends HttpServlet {

    private UserService userService = new UserService();

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //接收用户行为
        String actionName = request.getParameter("actionName");
        //判断用户行为，调用对应的方法
        if ("login".equals(actionName)) {
            //用户登录
            userLogin(request, response);

        } else if ("logout".equals(actionName)) {
            //用户退出
            userLogout (request, response);

        } else if ("userCenter".equals(actionName)) {
            //进入用户中心
            userCenter(request, response);

        } else if ("userHead".equals(actionName)) {
            //加载头像
            userHead(request, response);

        } else if ("checkNick".equals(actionName)) {

            //验证昵称的唯一性
            checkNick(request, response);

        } else if ("updateUser".equals(actionName)) {
            //修改用户信息
            updateUser(request, response);

        }

    }



    /**
     * 修改用户信息
     * @param request
     * @param response
     */
    private void updateUser(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //调用Service层方法,传递request 对象作为参数,返回resultInfo对象
        ResultInfo<User> resultInfo = userService.updateUser(request);
        //将resultInfo 对象存在request作用域中
        request.setAttribute("resultInfo", resultInfo);
        //请求转发到个人中心页面
        request.getRequestDispatcher("user?actionName=userCenter").forward(request, response);

    }

    /**
     * 验证昵称的唯一性
     * @param request
     * @param response
     */
    private void checkNick(HttpServletRequest request, HttpServletResponse response) throws IOException {
        //获取参数(昵称)
        String nick = request.getParameter("nick");
        //从session作用域中获取用户对象,得到用户id
        User user = ((User) request.getSession().getAttribute("user"));
        //调用Service层的方法,得到返回结果
        Integer code = userService.checkNick(nick, user.getUserId());
        response.getWriter().write(code + "");
        response.getWriter().close();

    }

    /**
     * 加载头像
     * @param request
     * @param response
     */
    private void userHead(HttpServletRequest request, HttpServletResponse response) throws IOException {
        //获取参数 (图片名称)
        String head = request.getParameter("imageName");
        //得到图片存放路径
        String realPath = request.getServletContext().getRealPath("/WEB-INF/upload/");
        //通过图片的完整路径,得到file对象
        File file = new File(realPath + "/" + head);
        //通过截取,得到图片的后缀
        String pic = head.substring(head.lastIndexOf(".") +1);
        //通过不同的图片后缀,设置不同的响应的类型
        if("PNG".equalsIgnoreCase(pic)) {
            response.setContentType("image/png");

        } else if ("JPG".equalsIgnoreCase(pic) || "JPEG".equalsIgnoreCase(pic)) {
            response.setContentType("image/jpeg");

        } else if ("GIF".equalsIgnoreCase(pic)) {
            response.setContentType("image/gif");

        }
        //利用FileUtils的copyFile() 方法,将图片拷贝给浏览器
        FileUtils.copyFile(file, response.getOutputStream());





    }

    /**
     * 进入个人中心
     *    1.设置首页动态包含的页面值
     *    2.请求转发跳转到index
     * @param request
     * @param response
     */
    private void userCenter(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        //设置首页动态包含的页面值
        request.setAttribute("menu_page", "user");
        //请求转发跳转到index
        request.getRequestDispatcher("index.jsp").forward(request, response);

    }

    /**
     *
     * 用户退出
     * @param request
     * @param response
     */
    private void userLogout(HttpServletRequest request, HttpServletResponse response) throws IOException {
        //销毁Session对象
        request.getSession().invalidate();
        //删除Cookie对象
        Cookie cookie = new Cookie("user", null);
        cookie.setMaxAge(0);
        response.sendRedirect("login.jsp");


    }

    /**
     * 用户登录
     * @param request
     * @param response
     */
    private void userLogin(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {


        //获取参数
        String userName = request.getParameter("userName");
        String userPwd = request.getParameter("userPwd");

        //调用Service层的方法，返回ResultInfo对象
        ResultInfo<User> resultInfo = userService.userLogin(userName, userPwd);

        if (resultInfo.getCode() == 1) {
            //将用户信息设置到session作用域中
            request.getSession().setAttribute("user", resultInfo.getResult());
            //判断用户是否选择记住密码（rem值是1）
            String rem = request.getParameter("rem");
            //如果勾选记住框，将用户姓名和密码存到Cookie中，设置失效时间，并响应给客户端
            if ("1".equals(rem)) {
                //得到Cookie 对象
                Cookie cookie = new Cookie("user", userName + "-" + userPwd);
                //设置失效时间
                cookie.setMaxAge(3*24*60*60);
                //响应给客户端
                response.addCookie(cookie);
            } else {
                //如果否，清空原有cookie对象
                Cookie cookie = new Cookie("user", null);
                cookie.setMaxAge(0);
                //响应客户端
                response.addCookie(cookie);
            }
            //重定向跳转到index首页
            response.sendRedirect("index.jsp");

        } else {
            //将resultInfo 对象设置到request作用域中
            request.setAttribute("resultInfo", resultInfo);
            //请求转发跳转到登录页面
            request.getRequestDispatcher("login.jsp").forward(request, response);

        }

    }
}
