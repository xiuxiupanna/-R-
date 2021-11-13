package com.lezijie.note;

import cn.hutool.crypto.digest.DigestUtil;
import com.leizijie.note.dao.BaseDao;
import com.leizijie.note.dao.UserDao;
import com.leizijie.note.po.User;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by HMF on 2021/07/10 22:28
 */
public class TestUser {
    // 写测试方法时，不要忘了加上 @Test 注解，不然没有运行按钮
    @Test
    public void testQueryUserByName() {
        UserDao userDao = new UserDao();
        User user = userDao.queryUserByName("admin");
        System.out.println(user.getUpwd());
    }

    /**
     * 测试使用 BaseDao 类的 更新操作方法
     */
    @Test
    public void testAdd() {
        String sql = "insert into tb_user (uname, upwd, nick, head, mood) values(?, ?, ?, ?, ?)";

        List<Object> params = new ArrayList<>();
        params.add("hmf");
        params.add(DigestUtil.md5Hex("123456")); // 使用hutool工具类加密密码
        params.add("朝夕");
        params.add("404.jpg");
        params.add("hello world！");

        int row = BaseDao.executeUpdate(sql, params);

        System.out.println("BaseDao类测试添加数据，返回受影响行数为： " + row);
    }
}
