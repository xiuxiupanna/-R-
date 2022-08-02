package com.lezijie.note.service;

import cn.hutool.core.util.StrUtil;
import cn.hutool.db.Page;
import com.lezijie.note.dao.NoteDao;
import com.lezijie.note.po.Note;
import com.lezijie.note.util.PageUtil;
import com.lezijie.note.vo.NoteVo;
import com.lezijie.note.vo.ResultInfo;

import javax.xml.transform.Result;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NoteService {

    private NoteDao noteDao = new NoteDao();

    /**
     * 添加或修改云记
     *
     * @param typeId
     * @param title
     * @param content
     * @param noteId
     * @param lon
     * @param lat
     * @return
     */
    public ResultInfo<Note> addOrUpdate(String typeId, String title, String content,
                                        String noteId, String lon, String lat) {
        ResultInfo<Note> resultInfo = new ResultInfo<>();

        //参数非空判断
        if (StrUtil.isBlank(typeId)) {
            resultInfo.setCode(0);
            resultInfo.setMsg("请选择云记类型! ");
            return resultInfo;

        }
        if (StrUtil.isBlank(title)) {
            resultInfo.setCode(0);
            resultInfo.setMsg("云记标题不能为空! ");
            return resultInfo;

        }
        if (StrUtil.isBlank(content)) {
            resultInfo.setCode(0);
            resultInfo.setMsg("云记内容不能为空! ");
            return resultInfo;

        }

        //设置经纬度的默认值，默认值为北京 116.404, 39.915
        if (lon == null || lat == null) {
            lon = "116.404";
            lat = "39.915";
        }

        // 设置回显对象
        Note note = new Note();
        note.setTitle(title);
        note.setContent(content);
        note.setTypeId(Integer.parseInt(typeId));
        note.setLon(Float.parseFloat(lon));
        note.setLat(Float.parseFloat(lat));

        //判断云记ID是否为空
        if (!StrUtil.isBlank(noteId)) {
            note.setNoteId(Integer.parseInt(noteId));

        }
        resultInfo.setResult(note);

        //调用Dao层,添加云记记录,返回受影响的行数
        int row = noteDao.addOrUpdate(note);

        if (row > 0) {
            resultInfo.setCode(1);
        } else {
            resultInfo.setCode(0);
            resultInfo.setResult(note);

        }
        return resultInfo;

    }

    /**
     * 分页查询云记列表
     *
     * @params
     * @param pageNumStr
     * @param pageSizeStr
     * @param userId
     * @param title
     * @param typeId
     * @return
     */
    public PageUtil<Note> findNoteListByPage(String pageNumStr, String pageSizeStr, Integer userId,
                                             String title, String date, String typeId) {

        //设置分页参数的默认值
        Integer pageNum = 1; //默认当前页是第一页
        Integer pageSize = 10; //默认每页显示10条数据

        //参数的非空校验(如果参数不为空,则设置参数)
        if (StrUtil.isNotBlank(pageNumStr)) {
            //设置当前页
            pageNum = Integer.parseInt(pageNumStr);

        }
        if (StrUtil.isNotBlank(pageSizeStr)) {
            //设置每页显示的数量
            pageSize = Integer.parseInt(pageSizeStr);

        }

        //查询当前用户的云记数量,返回总记录数
        Long count = noteDao.findNoteCount(userId, title, date, typeId);

        //判断总记录数是否大于0
        if (count < 1) {
            return null;

        }

        //如果总记录数大于0,调用Page类的带参构造,得到其他分页参数的值,返回Page对象
        PageUtil<Note> page = new PageUtil<>(userId, pageSize, count);

        //得到数据库中分页查询的开始下标
        Integer index = (pageNum - 1) * pageSize;

        //查询当前登录用户下当前页的数据列表,返回note集合
        List<Note> noteList = noteDao.findNoteListByPage(userId, index, pageSize, title, date, typeId);
        //将note集合设置到page对象中
        page.setDataList(noteList);
        //返回Page对象
        return page;

    }

    /**
     * 通过日期分组查询当前登录用户下云记数量
     *
     * @param userId
     * @return
     */
    public List<NoteVo> findNoteListByDate(Integer userId) {
        return noteDao.findNoteListByDate(userId);

    }

    /**
     * 通过类型分组查询当前登录用户下的云记数量
     *
     * @param userId
     * @return
     */
    public List<NoteVo> findNoteCountByType(Integer userId) {
        return noteDao.findNoteCountByType(userId);
    }

    /**
     * 查询云记详情
     *
     * @param noteId
     * @return
     */
    public Note findNoteById(String noteId) {
        if (StrUtil.isBlank(noteId)) {
            return null;
        }
        Note note = noteDao.findNoteById(noteId);
        return note;

    }

    /**
     * 删除云记
     *
     * @param noteId
     * @return
     */
    public Integer deleteNote(String noteId) {
        //判断参数
        if (StrUtil.isBlank(noteId)) {
            return 0;

        }
        int row = noteDao.deleteNoteById(noteId);

        if (row > 0) {
            return 1;

        }
        return 0;

    }

    /**
     * 通过月份查询对应的云记数量
     * @param userId
     * @return
     */
    public ResultInfo<Map<String, Object>> queryNoteCountByMonth(Integer userId) {

        ResultInfo<Map<String, Object>> resultInfo = new ResultInfo<>();
        //通过月份分类查询云记数量
        List<NoteVo> noteVoList = noteDao.findNoteListByDate(userId);
        //判断集合是否存在
        if (noteVoList != null && noteVoList.size() > 0) {
            //得到月份
            List<String> noteMonthList = new ArrayList<>();
            //得到云记数量
            List<Long> noteCounts = new ArrayList<>();

            for (NoteVo noteVo : noteVoList) {
                noteMonthList.add(noteVo.getGroupName());
                noteCounts.add(((Long)noteVo.getNoteCount()));
            }

            //准备Map对象，封装对应的月份和云记数量
            Map<String, Object> map = new HashMap<>();
            map.put("monthArray", noteMonthList);
            map.put("dataArray", noteCounts);

            //将map对象设置到resultInfo对象中
            resultInfo.setCode(1);
            resultInfo.setResult(map);



        }


        return resultInfo;


    }

    /**
     * 查询用户发布云记时的坐标
     * @param userId
     * @return
     */
    public ResultInfo<List<Note>> queryNoteLonAndLat(Integer userId) {
        ResultInfo<List<Note>> resultInfo = new ResultInfo<>();


        //通过用户id查询云记记录
        List<Note> noteList = noteDao.queryNoteList(userId);

        //判断是否为空
        if (noteList != null && noteList.size() > 0) {

            resultInfo.setCode(1);
            resultInfo.setResult(noteList);
        }




        return resultInfo;




    }
}
