package com.lezijie.note.dao;

import com.lezijie.note.po.User;
import com.lezijie.note.util.DBUtil;

import java.sql.Array;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class UserDao {

    public User queryUserByName02(String userName) {
        User user = null;

        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
             connection = DBUtil.getConnection();
             String sql = "select * from tb_user where uname = ?";
             statement = connection.prepareStatement(sql);
             statement.setString(1,userName);
             resultSet = statement.executeQuery();

             if (resultSet.next()) {
                 user = new User();
                 user.setUserId(resultSet.getInt("userId"));
                 user.setUname(userName);
                 user.setHead(resultSet.getString("head"));
                 user.setMood(resultSet.getString("mood"));
                 user.setNick(resultSet.getString("nick"));
                 user.setUpwd(resultSet.getString("upwd"));
             }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(resultSet,statement,connection);
        }

        return user;

    }



    public User queryUserByName(String userName) {

        String sql = "select * from tb_user where uname = ?";
        //设置参数集合
        List<Object> params = new ArrayList<>();
        params.add(userName);
        User user = ((User) BaseDao.queryRow(sql, params, User.class));
        return user;

    }

    /**
     * 通过昵称与用户id查询用户对象
     * @param nick
     * @param userId
     * @return
     */
    public User queryUserByNickAndUserId(String nick, Integer userId) {
        String sql = "select * from tb_user where nick = ? and userId != ?";
        List<Object> params = new ArrayList<>();
        params.add(nick);
        params.add(userId);

        User user = ((User) BaseDao.queryRow(sql, params, User.class));
        return user;

    }

    /**
     * 通过用户修改用户信息
     * @param user
     * @return
     */
    public int updateUser(User user) {
        //定义sql语句
        String sql = "update tb_user set nick = ?, mood = ?, head = ? where userId = ?";
        //设置参数
        List<Object> params = new ArrayList<>();
        params.add(user.getNick());
        params.add(user.getMood());
        params.add(user.getHead());
        params.add(user.getUserId());
        //调用BaseDao 更新方法,返回受影响的行数
        int row = BaseDao.executeUpdate(sql, params);
        return row;

    }
}
