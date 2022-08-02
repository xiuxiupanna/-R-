package com.lezijie;

import com.lezijie.note.dao.BaseDao;
import com.lezijie.note.dao.UserDao;
import com.lezijie.note.po.User;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class TestUser {


    @Test
    public void testQueryUserByName() {
        UserDao userDao = new UserDao();
        User user = userDao.queryUserByName("admin");
        System.out.println(user.getUpwd());

    }

    @Test
    public void testAdd() {
        String sql = "insert into tb_user (uname, upwd, nick, head, mood) values (?, ?, ?, ?, ?)";
        List<Object> params = new ArrayList<>();
        params.add("孙悟空");
        params.add("f83702534e9f1883fd513df47b134702");
        params.add("齐天大圣");
        params.add("404.jpg");
        params.add("恭喜发财，红包拿来！");

        int row = BaseDao.executeUpdate(sql, params);
        System.out.println(row);



    }


}
