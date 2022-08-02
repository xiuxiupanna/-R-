package com.lezijie.note.dao;

import com.lezijie.note.po.NoteType;
import com.lezijie.note.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class NoteTypeDao {

    public List<NoteType> findTypeListByUserId(Integer userId) {
        String sql = "select typeId, typeName, userId, from tb_note_type where userId = ?";
        List<Object> params = new ArrayList<>();
        params.add(userId);
        //调用BaseDao的查询方法,返回集合
        List<NoteType> list = BaseDao.queryRows(sql, params, NoteType.class);

        return list;

    }


    public Long findNoteCountByTypeId(String typeId) {
        //定义SQL语句
        String sql = "select count(1) from tb_note where typeId = ?";
        List<Object> params = new ArrayList<>();
        params.add(typeId);
        Long count = (Long) BaseDao.findSingleValue(sql, params);
        return count;

    }

    /**
     * 通过类型ID删除指定的类型记录, 返回受影响的行数
     * @param typeId
     * @return
     */
    public int deleteTypeById(String typeId) {
        String sql = "delete from tb_note_type where typeId = ?";
        List<Object> params = new ArrayList<>();
        params.add(typeId);
        int row = BaseDao.executeUpdate(sql, params);
        return row;

    }

    /**
     * 验证当前登录用户下,类型名称是否唯一
     *       返回1,表示成功
     *       返回0,表示失败
     * @param typeName
     * @param userId
     * @param typeId
     * @return
     */
    public Integer checkTypeName(String typeName, Integer userId, String typeId) {
        String sql = "select * from tb_note_type where userId = ? and typeName = ?";
        //设置参数
        List<Object> params = new ArrayList<>();
        params.add(userId);
        params.add(typeName);
        //执行查询操作
        NoteType noteType = ((NoteType) BaseDao.queryRow(sql, params, NoteType.class));
        if (noteType == null) {
            return 1;
        } else {
            //如果是修改操作,则需要判断是否是当前记录本身
            if (typeId.equals(noteType.getTypeId().toString())) {
                return 1;
            }
        }
        return 0;
    }

    /**
     * 添加方法,返回主键
     * @param typeName
     * @param userId
     * @return
     */
    public Integer addType(String typeName, Integer userId) {
        Integer key = null;
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try{
            connection =  DBUtil.getConnection();
            String sql = "insert into tb_note_type (typeName, userId) values (?, ?)";
            //预编译
            preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            //设置参数
            preparedStatement.setString(1, typeName);
            preparedStatement.setInt(2, userId);
            //执行更新,返回受影响的行数
            int row = preparedStatement.executeUpdate();
            //判断受影响的行数
            if (row > 0) {
                //获取返回主键的结果集
                resultSet = preparedStatement.getGeneratedKeys();
                //得到主键的值
                if (resultSet.next()) {
                    key = resultSet.getInt(1);

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(resultSet,preparedStatement,connection);
        }
        return key;

    }

    /**
     * 修改方法,返回受影响的行数
     * @param typeName
     * @param typeId
     * @return
     */
    public Integer updateType(String typeName, String typeId) {

        String sql = "update tb_note_type set typeName = ? where typeId = ?";
        //设置参数
        List<Object> params = new ArrayList<>();
        params.add(typeName);
        params.add(typeId);
        //调用BaseDao更新方法
        int row = BaseDao.executeUpdate(sql, params);
        return row;

    }
}
