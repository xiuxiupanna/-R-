package com.lezijie.note.service;

import cn.hutool.core.util.StrUtil;
import com.lezijie.note.dao.NoteTypeDao;
import com.lezijie.note.po.NoteType;
import com.lezijie.note.vo.ResultInfo;

import java.util.List;

public class NoteTypeService {

    private NoteTypeDao noteTypeDao = new NoteTypeDao();

    /**
     * 查询类型列表
     * @param userId
     * @return
     */
    public List<NoteType> findTypeList(Integer userId) {
        List<NoteType> typeList = noteTypeDao.findTypeListByUserId(userId);
        return typeList;

    }

    /**
     * 删除类型
     * @param typeId
     * @return
     */
    public ResultInfo<NoteType> deleteType(String typeId) {
        ResultInfo<NoteType> resultInfo = new ResultInfo<>();
        //判断参数是否为空
        if (StrUtil.isBlank(typeId)) {
            resultInfo.setCode(0);
            resultInfo.setMsg("系统异常,请重试!");
            return resultInfo;

        }
        Long noteTypeCount = noteTypeDao.findNoteCountByTypeId(typeId);
        //如果云记数量大于0,说明存在子记录,不可删除
        if(noteTypeCount > 0) {
            resultInfo.setCode(0);
            resultInfo.setMsg("该类型存在子记录,不可删除!");
            return resultInfo;

        }
        //如果不存在子记录,调用Dao层更新方法,通过类型ID删除指定类型记录,返回受影响的行数
        int row = noteTypeDao.deleteTypeById(typeId);

        //判断受影响的行数是否大于0
        if (row > 0) {
            resultInfo.setCode(1);
        } else {
            resultInfo.setCode(0);
            resultInfo.setMsg("删除类型失败!");
        }

        return resultInfo;

    }

    /**
     * 添加或修改类型
     * @param typeId
     * @param typeName
     * @param userId
     * @return
     */
    public ResultInfo<Integer> addOrUpdate(String typeId, String typeName, Integer userId) {
        ResultInfo<Integer> resultInfo = new ResultInfo<>();
        //判断参数是否为空
        if (StrUtil.isBlank(typeName)) {
            resultInfo.setCode(0);
            resultInfo.setMsg("类型名称不能为空!");
            return resultInfo;

        }
        //调用Dao层,查询当前登录用户下,类型名称是否唯一,返回0或1
        Integer code = noteTypeDao.checkTypeName(typeName, userId, typeId);
        if (code == 0) {
            resultInfo.setCode(0);
            resultInfo.setMsg("类型名称已存在,请重新输入!");
            return resultInfo;
        }
        //返回的结果
        Integer key = null; //主键或受影响的行数
        if (StrUtil.isBlank(typeId)) {
            //如果为空,调用Dao层的添加方法,返回主键 (前台页面需要显示添加成功之后的类型ID)
            key = noteTypeDao.addType(typeName, userId);
        } else {
            //如果不为空,调用Dao的修改方法,返回受影响的行数
            key = noteTypeDao.updateType(typeName, typeId);

        }
        if (key > 0) {
            resultInfo.setCode(1);
            resultInfo.setResult(key);

        } else {
            resultInfo.setCode(1);
            resultInfo.setMsg("更新失败!");
        }

        return resultInfo;
    }
}
