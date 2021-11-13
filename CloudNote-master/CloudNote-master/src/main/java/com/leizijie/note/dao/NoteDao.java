package com.leizijie.note.dao;

import cn.hutool.core.util.StrUtil;
import com.leizijie.note.po.Note;
import com.leizijie.note.vo.NoteVo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by HMF on 2021/07/14 21:57
 */
public class NoteDao {

    /**
     * 添加或修改云记：
     * --添加云记，返回受影响的行数
     *
     * @param note
     * @return
     */
    public int addOrUpdate(Note note) {
        // 定义sql语句
        String sql = "";

        // 设置参数列表
        List<Object> params = new ArrayList<>();
        params.add(note.getTypeId());
        params.add(note.getTitle());
        params.add(note.getContent());

        // 判断noteId是否为空，如果为空，则为添加操作，如果不为空，则为修改操作
        if (note.getNoteId() == null) { // 添加操作
            sql = "insert into tb_note (typeId, title, content, pubTime, lon, lat) values(?, ?, ?, now(), ?, ?) ";
            params.add(note.getLon());
            params.add(note.getLat());
        } else { // 修改操作
            sql = "update tb_note set typeId = ?, title = ?, content = ? where noteId = ? ";
            params.add(note.getNoteId());
        }

        // 调用BaseDao
        int row = BaseDao.executeUpdate(sql, params);

        return row;
    }

    /**
     * 查询当前登录用户的云记数量，返回总记录数
     *
     * @param userId
     * @param title
     * @param date
     * @param typeId
     * @return
     */
    public long findNoteCount(Integer userId, String title, String date, String typeId) {
        // 定义sql语句
        String sql = "select count(*) from tb_note n INNER JOIN " +
                "tb_note_type t on n.typeId = t.typeId " +
                "WHERE userId = ? ";

        // 设置参数集合
        List<Object> params = new ArrayList<>();
        params.add(userId);

        // 判断条件查询的参数title是否为空（如果查询的参数不为空，则拼接sql语句，并设置所需要的参数）
        if (!StrUtil.isBlank(title)) { // 标题查询
            // 拼接条件查询的sql语句，要使用 concat()方法来拼接sql语句
            sql += " and title like concat('%', ?, '%') ";
            // 设置sql语句的查询参数
            params.add(title);

        } else if (!StrUtil.isBlank(date)) { // 日期查询
            // 拼接条件查询的sql语句
            sql += " and DATE_FORMAT(pubTime, '%Y年%m月') = ? ";
            // 设置sql语句的查询参数
            params.add(date);

        } else if (!StrUtil.isBlank(typeId)) { // 类型查询
            // 拼接条件查询的sql语句
            sql += " and n.typeId = ? ";
            // 设置sql语句的查询参数
            params.add(typeId);
        }

        // 调用BaseDao的查询方法
        long count = (long) BaseDao.findSingleValue(sql, params);

        // 返回
        return count;
    }

    /**
     * 分页查询当前登录用户下当前页的数据列表，返回note集合，每次点一下都要查询一次
     *
     * @param userId
     * @param index
     * @param pageSize
     * @param title
     * @param date
     * @param typeId
     * @return
     */
    public List<Note> findNoteListByPage(Integer userId, Integer index, Integer pageSize,
                                         String title, String date, String typeId) {
        // 定义sql语句
        String sql = "SELECT noteId, title, pubTime FROM tb_note n " +
                "INNER JOIN tb_note_type t ON n.typeId = t.typeId " +
                "WHERE userId = ? ";

        // 设置参数
        List<Object> params = new ArrayList<>();
        params.add(userId);

        // 判断条件查询的参数title是否为空（如果查询的参数不为空，则拼接sql语句，并设置所需要的参数）
        if (!StrUtil.isBlank(title)) {
            // 拼接条件查询的sql语句，要使用 concat()方法来拼接sql语句
            sql += " and title like concat('%', ?, '%') ";
            // 设置sql语句的查询参数
            params.add(title);

        } else if (!StrUtil.isBlank(date)) { // 日期查询
            // 拼接条件查询的sql语句
            sql += " and DATE_FORMAT(pubTime, '%Y年%m月') = ? ";
            // 设置sql语句的查询参数
            params.add(date);

        } else if (!StrUtil.isBlank(typeId)) { // 类型查询
            // 拼接条件查询的sql语句
            sql += " and n.typeId = ? ";
            // 设置sql语句的查询参数
            params.add(typeId);
        }

        // 拼接分页的sql语句（因为limit语句需要写在sql语句最后）
        sql += " order by pubTime desc limit ?, ? "; // 【修复了云记列表乱序的情况使用order by pubTime desc 排序解决】

        params.add(index);
        params.add(pageSize);

        // 调用BaseDao的查询方法
        List<Note> noteList = BaseDao.queryRows(sql, params, Note.class);

        // 返回
        return noteList;
    }

    /**
     * 通过日期分组查询当前登录用户下的云记数量
     *
     * @param userId
     * @return
     */
    public List<NoteVo> findNoteCountByDate(Integer userId) {
        // 测试sql语句DATE_FORMAT()函数的使用：SELECT DATE_FORMAT(pubTime, '%Y年%m月') FROM tb_note
        // 定义sql语句
        String sql = "SELECT COUNT(*) noteCount, DATE_FORMAT(pubTime, '%Y年%m月') groupName FROM tb_note n " +
                "INNER JOIN tb_note_type t " +
                "ON n.typeId = t.typeId " +
                "WHERE userId = ? " +
                "GROUP BY DATE_FORMAT(pubTime, '%Y年%m月') " +
                "ORDER BY DATE_FORMAT(pubTime, '%Y年%m月') DESC ";
        // 设置参数
        List<Object> params = new ArrayList<>();
        params.add(userId);

        // 调用BaseDao执行查询
        List<NoteVo> noteVoList = BaseDao.queryRows(sql, params, NoteVo.class);

        // 返回
        return noteVoList;
    }

    /**
     * 通过类型分组查询当前登录用户下的云记类型数量
     *
     * @param userId
     * @return
     */
    public List<NoteVo> findNoteCountByType(Integer userId) {
        // 定义sql语句
        String sql = "SELECT COUNT(noteId) noteCount, t.typeId, typeName groupName FROM tb_note n " +
                "RIGHT JOIN tb_note_type t " +
                "ON n.typeId = t.typeId " +
                "WHERE userId = ? " +
                "GROUP BY t.typeId " +
                "ORDER BY COUNT(noteId) DESC";

        // 设置参数
        List<Object> params = new ArrayList<>();
        params.add(userId);

        // 调用BaseDao执行查询
        List<NoteVo> list = BaseDao.queryRows(sql, params, NoteVo.class);

        // 返回
        return list;
    }

    /**
     * 通过noteId查询云记对象note
     *
     * @param noteId
     * @return
     */
    public Note findNoteById(String noteId) {
        // 定义sql语句
        String sql = "select noteId, title, content, pubTime, typeName, n.typeId from tb_note n " +
                "INNER JOIN tb_note_type t ON n.typeId = t.typeId where noteId = ?";

        // 设置参数
        List<Object> params = new ArrayList<>();
        params.add(noteId);

        // 调用BaseDao的查询方法
        Note note = (Note) BaseDao.queryRow(sql, params, Note.class);

        // 返回note
        return note;
    }

    /**
     * 删除云记：
     * --通过noteId删除云记记录，返回受影响的行数
     *
     * @param noteId
     * @return
     */
    public int deleteNoteById(String noteId) {
        // 定义sql语句
        String sql = "delete from tb_note where noteId = ?";

        // 设置参数
        List<Object> params = new ArrayList<>();
        params.add(noteId);

        // 调用BaseDao执行sql语句
        int row = BaseDao.executeUpdate(sql, params);

        return row;
    }

    /**
     * 通过userId查询用户发布云记时的经纬度坐标
     *
     * @param userId
     * @return
     */
    public List<Note> queryNoteList(Integer userId) {
        // 定义sql语句
        String sql = "select lon, lat from tb_note n inner join tb_note_type t on n.typeId = t.typeId where userId = ?";

        // 设置参数
        List<Object> params = new ArrayList<>();
        params.add(userId);

        // 调用BaseDao查询
        List<Note> noteList = BaseDao.queryRows(sql, params, Note.class);

        return noteList;
    }
}
