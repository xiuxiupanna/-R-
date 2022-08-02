package com.lezijie.note.service;

import cn.hutool.aop.interceptor.SpringCglibInterceptor;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.lezijie.note.dao.UserDao;
import com.lezijie.note.po.User;
import com.lezijie.note.vo.ResultInfo;
import com.sun.org.apache.bcel.internal.generic.IF_ACMPEQ;

import javax.jws.soap.SOAPBinding;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;

public class UserService {

    private UserDao userDao = new UserDao();

    /**
     * 用户登录
     * @param userName
     * @param userPwd
     * @return
     */
    public ResultInfo<User> userLogin(String userName, String userPwd) {
        ResultInfo<User> resultInfo = new ResultInfo<>();

        //数据回显： 当登录实现时，将登录信息返回给页面显示
        User u = new User();
        u.setUname(userName);
        u.setUpwd(userPwd);
        //设置到resultInfo对象中
        resultInfo.setResult(u);


        if (StrUtil.isBlank(userName) || StrUtil.isBlank(userPwd)) {
            resultInfo.setCode(0);
            resultInfo.setMsg("用户姓名或密码不能为空！");

        }
        User user = userDao.queryUserByName(userName);
        if (user == null) {
            resultInfo.setCode(0);
            resultInfo.setMsg("该用户不存在！");
            return resultInfo;
        }
        userPwd = DigestUtil.md5Hex(userPwd);
        //判断加密后的密码是否与数据库中的一致
        if (!userPwd.equals(user.getUpwd())) {
            resultInfo.setCode(0);
            resultInfo.setMsg("用户密码不正确！");
            return resultInfo;
        }
        resultInfo.setCode(1);
        resultInfo.setResult(user);

        return resultInfo;

    }

    /**
     * 验证昵称的唯一性
     *
     * @param nick
     * @param userId
     * @return
     */
    public Integer checkNick(String nick, Integer userId) {
        if (StrUtil.isBlank(nick)) {
            return 0;
        }
        //调用Dao层,通过用户Id和昵称查询用户对象
        User user = userDao.queryUserByNickAndUserId(nick, userId);

        //判断用户对象存在
        if(user != null) {
            return 0;

        }
        return 1;
    }

    /**
     * 修改用户信息
     * @param request
     * @return
     */
    public ResultInfo<User> updateUser(HttpServletRequest request) {
        ResultInfo<User> resultInfo = new ResultInfo<>();
        //获取参数(昵称,心情)
        String nick = request.getParameter("nick");
        String mood = request.getParameter("mood");

        //参数的非空校验 (判断必填参数非空)
        if (StrUtil.isBlank(nick)) {
            resultInfo.setCode(0);
            resultInfo.setMsg("用户昵称不能为空!");
            return resultInfo;

        }
        User user = ((User) request.getSession().getAttribute("user"));
        user.setNick(nick);
        user.setMood(mood);
        try {
            //文件上传 获取Part对象 request.getPart("name")
            Part part = request.getPart("img");
            //通过Part 对象获取上传文件的文件名(从头部信息中获取上传的文件名)
            String header = part.getHeader("Content-Disposition");
            //获取具体的请求头对应的值
            String str = header.substring(header.lastIndexOf("=") + 2);
            //获取上传的文件名
            String fileName = str.substring(0, str.length() - 1);
            //判断文件名是否为空
            if (!StrUtil.isBlank(fileName)) {
                //如果用户上传了头像,则更新用户对象中的头像
                user.setHead(fileName);
                String filePath = request.getServletContext().getRealPath("/WEB-INF/upload/");
                part.write(filePath + "/" +fileName);
            }
            //调用Dao层的更新方法,返回受影响的行数
            int row = userDao.updateUser(user);
            if (row > 0) {
                resultInfo.setCode(1);
                //更新session中用户对象
                request.getSession().setAttribute("user", user);

            } else {
                resultInfo.setCode(0);
                resultInfo.setMsg("更新失败");
            }

            return resultInfo;


        } catch (Exception e) {
            e.printStackTrace();

        }

        return resultInfo;

    }
}
