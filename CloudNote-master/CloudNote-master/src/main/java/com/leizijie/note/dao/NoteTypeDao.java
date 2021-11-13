package com.leizijie.note.dao;

import com.leizijie.note.po.NoteType;
import com.leizijie.note.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by HMF on 2021/07/13 09:39
 */
public class NoteTypeDao {
    /**
     * 通过用户ID查询类型集合:
     * --1.定义SQL语句
     * ----String sql = "select typeId,typeName,userId from tb_note_type where userId = ? ";
     * --2.设置参数列表
     * --3.调用BaseDao的查询方法，返回集合
     * --4.返回集合
     *
     * @param userId
     * @return
     */
    public List<NoteType> findTypeListByUserId(Integer userId) {
        // 1.定义SQL语句
        String sql = "select typeId,typeName,userId from tb_note_type where userId = ? ";
        // 2.设置参数列表
        List<Object> params = new ArrayList();
        params.add(userId);
        // 3.调用BaseDao的查询方法，返回集合
        List<NoteType> list = BaseDao.queryRows(sql, params, NoteType.class);
        // 4.返回集合
        return list;
    }

    /**
     * 通过类型ID查询云记记录的数量，返回云记数量:
     *
     * @param typeId
     * @return
     */
    public long findNoteCountByTypeId(String typeId) {
        // count(1) 和 count(*) 相同，不同的是，mysql不同的存储引擎，对count(*)做了不同的优化
        // 阿里开发手册中，强制要求用count(*)
//        String sql = "select count(1) from tb_note where typeId = ?"; // 视屏中使用的方法
        // 定义sql语句
        String sql = "select count(*) from tb_note where typeId = ?";
        // 设置参数集合
        List<Object> params = new ArrayList<>();
        params.add(typeId);
        // 调用BaseDao
        long count = (long) BaseDao.findSingleValue(sql, params);
        return count;
    }


    /**
     * 通过类型ID删除指定的类型记录，返回受影响的行数:
     *
     * @param typeId
     * @return
     */
    public int deleteTypeById(String typeId) {
        // 定义sql语句
        String sql = "delete from tb_note_type where typeId = ?";
        // 设置参数集合
        List<Object> params = new ArrayList<>();
        params.add(typeId);
        // 调用 BaseDao
        int row = BaseDao.executeUpdate(sql, params);
        return row;
    }

    /**
     * 添加 或 修改 类型：
     * -- 1.查询当前登录用户下，类型名称是否唯一
     * ----返回 1，表示成功
     * ----返回 2，表示失败
     *
     * @param typeId
     * @param typeName
     * @param userId
     * @return
     */
    public Integer checkTypeName(String typeId, String typeName, Integer userId) {
        // 定义sql语句
        String sql = "select * from tb_note_type where userId = ? and typeName = ?";
        // 设置参数集合
        List<Object> params = new ArrayList<>();
        params.add(userId);
        params.add(typeName);
        // 调用BaseDao,执行查询
        NoteType noteType = (NoteType) BaseDao.queryRow(sql, params, NoteType.class);
        // 如果对象为空，表示可用
        if (noteType == null) {
            return 1;
        } else { // 如果不为空
            // 如果是修改操作，则需要判断是否是当前记录本身
            if (typeId.equals(noteType.getTypeId().toString()))
                return 1;
        }
        return 0;
    }

    /**
     * 添加 或 修改 类型：
     * --添加方法，返回主键
     *
     * @param typeName
     * @param userId
     * @return
     */
    public Integer addType(String typeName, Integer userId) { // 因为返回的是主键，所以这里就不能使用 BaseDao 了
        Integer key = null;
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            // 得到数据库连接
            connection = DBUtil.getConnection();
            // 定义sql语句
            String sql = "insert into tb_note_type(typeName, userId) values (?, ?)";
            // 预编译，注意这里返回主键写法 hmf
            preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, typeName);
            preparedStatement.setInt(2, userId);
            // 执行更新，返回受影响的行数
            int row = preparedStatement.executeUpdate();
            // 判断受影响的行数
            if (row > 0) {
                // 获取返回主键的结果集
                resultSet = preparedStatement.getGeneratedKeys();
                // 从结果集中得到主键的值
                if (resultSet.next()) {
                    key = resultSet.getInt(1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 关闭资源
            DBUtil.close(resultSet, preparedStatement, connection);
        }
        return key;
    }

    /**
     * 添加 或 修改 类型：
     * --修改方法，返回受影响的行数
     *
     * @param typeId
     * @param typeName
     * @return
     */
    public Integer updateType(String typeId, String typeName) {
        // 定义sql语句
        String sql = "update tb_note_type set typeName = ? where typeId = ?";
        // 设置参数集合
        List<Object> params = new ArrayList<>();
        params.add(typeName);
        params.add(typeId);
        // 调用 BaeDao，执行更新操作
        int row = BaseDao.executeUpdate(sql, params);
        // 返回
        return row;
    }
}
