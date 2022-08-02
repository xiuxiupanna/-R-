package com.lezijie.note.dao;

import cn.hutool.core.util.StrUtil;
import com.lezijie.note.po.Note;
import com.lezijie.note.po.NoteType;
import com.lezijie.note.vo.NoteVo;

import java.util.ArrayList;
import java.util.List;

public class NoteDao {
    /**
     * 添加或修改云记,返回受影响的行数
     * @param note
     * @return
     */
    public int addOrUpdate(Note note) {
        String sql = "";
        //设置参数
        List<Object> params = new ArrayList<>();
        params.add(note.getTypeId());
        params.add(note.getTitle());
        params.add(note.getContent());

        //判断noteId 是否为空;如果为空,则为添加操作;如果不为空,则为修改操作
        if (note.getNoteId() == null) { //添加操作
            sql = "insert into tb_note (typeId, title, content, pubTime, lon, lat) values (?, ?, ?, now(), ?, ?)";
            params.add(note.getLon());
            params.add(note.getLat());

        } else { //修改操作
            sql = "update tb_note set typeId = ?, title = ?, content = ? where noteId = ?";
            params.add(note.getNoteId());

        }

        //调用BaseDao的更新方法
        int row = BaseDao.executeUpdate(sql, params);
        return row;
    }

    /**
     * 查询当前登录用户的云记数量,返回总记录数
     * @param userId
     * @param title
     * @param date
     * @param typeId
     * @return
     */
    public Long    findNoteCount(Integer userId, String title, String date, String typeId) {
        String sql = "select count(1) from tb_note n INNER JOIN " +
                "tb_note_type t on n.typeId = t.typeId" +
                " where userId = ?";

        List<Object> params = new ArrayList<>();
        params.add(userId);
        //判断条件查询得到参数是否为空
        if (StrUtil.isNotBlank(title)) { // 标题查询
            //拼接条件查询的sql语句
            sql += "and title like concat('%, ?, %')";
            params.add(title);
        } else if (StrUtil.isNotBlank(date)) { // 日期查询
            sql += "and date_format(pubTime, '%Y年%m月') = ? ";
            params.add(date);

        } else if (StrUtil.isNotBlank(typeId)) { // 类型查询
            sql += "and n.typeId = ? ";
            params.add(typeId);

        }

        Long count = ((Long) BaseDao.findSingleValue(sql, params));
        return count;

    }

    /**
     * 分页查询云记列表
     * @param userId
     * @param index
     * @param pageSize
     * @param title
     * @param date
     * @param typeId
     * @return
     */
    public List<Note> findNoteListByPage(Integer userId, Integer index, Integer pageSize, String title, String date, String typeId) {
        // 定义SQL语句
        String sql = " SELECT noteId,title,pubTime FROM tb_note n INNER JOIN " +
                " tb_note_type t on n.typeId = t.typeId WHERE userId = ? ";
        //设置参数
        List<Object> params = new ArrayList<>();
        params.add(userId);
        //判断条件查询得到参数是否为空
        if (StrUtil.isNotBlank(title)) {
            //拼接条件查询的sql语句
            sql += " and title like concat('%, ?, %')";
            params.add(sql);
        } else if (StrUtil.isNotBlank(date)) { // 日期查询
            sql += "and date_format(pubTime, '%Y年%m月') = ? ";
            params.add(date);

        } else if (StrUtil.isNotBlank(typeId)) { // 类型查询
            sql += "and n.typeId = ? ";
            params.add(typeId);

        }

        //拼接分页的sql语句
        sql += " order by putTime desc limit ?, ?";
        params.add(pageSize);
        List<Note> noteList = BaseDao.queryRows(sql, params, Note.class);
        return noteList;

    }

    /**
     * 通过日期分组查询当前登录用户下云记数量
     * @param userId
     * @return
     */
    public List<NoteVo> findNoteListByDate(Integer userId) {
        //定义SQL语句
        String sql = "SELECT\n" +
                "\tcount( 1 ),\n" +
                "\tDATE_FORMAT( pubTime, '%Y年%m月' ) \n" +
                "FROM\n" +
                "\ttb_note n\n" +
                "\tINNER JOIN tb_note_type t ON n.typeId = t.typeId \n" +
                "WHERE\n" +
                "\tuserId = ? \n" +
                "GROUP BY\n" +
                "\tDATE_FORMAT( pubTime, '%Y年%m月' ) \n" +
                "ORDER BY\n" +
                "\tDATE_FORMAT( pubTime, '%Y年%m月' ) DESC;";

        List<Object> params = new ArrayList<>();
        params.add(userId);

        List<NoteVo> noteVoList = BaseDao.queryRows(sql, params, NoteVo.class);
        return noteVoList;

    }

    /**
     * 通过类型分组查询当前登录用户下的云记数量
     * @param userId
     * @return
     */
    public List<NoteVo> findNoteCountByType(Integer userId) {
        //定义SQL语句
        String sql = "SELECT\n" +
                "\tcount( noteId ) noteCount,\n" +
                "\tt.typeId,\n" +
                "\tt.typeName groupName \n" +
                "FROM\n" +
                "\ttb_note n\n" +
                "\tRIGHT JOIN tb_note_type t ON n.typeId = t.typeId \n" +
                "WHERE\n" +
                "\tuserId = ? \n" +
                "GROUP BY\n" +
                "\tCOUNT( noteId ) DESC";


        List<Object> params = new ArrayList<>();
        params.add(userId);

        List<NoteVo> noteVoList = BaseDao.queryRows(sql, params, NoteVo.class);
        return noteVoList;



    }

    /**
     * 查询云记详情
     * @param noteId
     * @return
     */
    public Note findNoteById(String noteId) {
        String sql = "select noteId, title, content, pubTime, typeName, n.typeId from tb_note n " +
                " inner join tb_note_type t on n.typeId = t.typeId where noteId = ?";
        //设置参数
        List<Object> params = new ArrayList<>();
        params.add(noteId);
        Note note = ((Note) BaseDao.queryRow(sql, params, Note.class));
        return note;

    }

    /**
     * 通过noteId删除云记记录,返回受影响的行数
     * @param noteId
     * @return
     */
    public int deleteNoteById(String noteId) {
        String sql = "delete from tb_note where noteId = ? ";

        List<Object> params = new ArrayList<>();
        params.add(noteId);
        //调用BaseDao
        int row = BaseDao.executeUpdate(sql, params);

        return row;
    }

    /**
     * 通过用户id查询云记记录
     * @param userId
     * @return
     */
    public List<Note> queryNoteList(Integer userId) {
        //定义SQL语句
        String sql = "select lon, lat from tb_note n inner join tb_note_type on n.typeId = t.typeId";
        //设置参数
        List<Object> params = new ArrayList<>();
        params.add(userId);
        List<Note> list = BaseDao.queryRows(sql, params, Note.class);
        return list;

    }
}
