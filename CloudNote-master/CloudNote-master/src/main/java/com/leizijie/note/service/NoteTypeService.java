package com.leizijie.note.service;

import cn.hutool.core.util.StrUtil;
import com.leizijie.note.dao.NoteTypeDao;
import com.leizijie.note.po.NoteType;
import com.leizijie.note.vo.ResultInfo;

import java.util.List;

/**
 * Created by HMF on 2021/07/13 09:40
 */
public class NoteTypeService {
    private NoteTypeDao noteTypeDao = new NoteTypeDao();

    /**
     * 查询类型列表：
     * --1.调用Dao层的查询方法，通过用户ID查询类型集合
     * --2.返回类型集合
     *
     * @param userId
     * @return
     */
    public List<NoteType> findTypeList(Integer userId) {
        // 1.调用Dao层的查询方法，通过用户ID查询类型集合
        List<NoteType> typeList = noteTypeDao.findTypeListByUserId(userId);
        // 2.返回类型集合
        return typeList;
    }

    /**
     * 删除类型：
     * --1.判断参数是否为空
     * --2.调用Dao层，通过类型ID查询云记记录的数量
     * --3.如果云记数量大于0，说明存在子记录，不可删除
     * ----code=0，msg="该类型存在子记录，不可删除"，返回resultInfo对象
     * --4.如果不存在子记录，调用Dao层的更新方法，通过类型ID删除指定的类型记录，返回受影响的行数
     * --5.判断受影响的行数是否大于0
     * ----大于0，code=1；否则，code=0，msg="删除失败"
     * --6.返回ResultInfo对象
     *
     * @param typeId
     * @return
     */
    public ResultInfo<NoteType> deleteType(String typeId) {
        ResultInfo<NoteType> resultInfo = new ResultInfo<>();
        // 1.判断参数是否为空
        if (StrUtil.isBlank(typeId)) {
            resultInfo.setCode(0);
            resultInfo.setMsg("系统异常，请重试！！！");
            return resultInfo;
        }
        // 2.调用Dao层，通过类型ID查询云记记录的数量
        long noteCount = noteTypeDao.findNoteCountByTypeId(typeId);
        // 3.如果云记数量大于0，说明存在子记录，不可删除
        if (noteCount > 0) {
            // 设置 code=0，msg="该类型存在子记录，不可删除"，返回resultInfo对象
            resultInfo.setCode(0);
            resultInfo.setMsg("该类型存在子记录，不可删除！！！");
            return resultInfo;
        }
        // 4.如果不存在子记录，调用Dao层的更新方法，通过类型ID删除指定的类型记录，返回受影响的行数
        int row = noteTypeDao.deleteTypeById(typeId);
        // 5.判断受影响的行数是否大于0，因为删除可能失败，所以要做判断
        if (row > 0) {
            // 大于0，code=1；
            resultInfo.setCode(1);
        } else {
            // 否则，code=0，msg="删除失败"
            resultInfo.setCode(0);
            resultInfo.setMsg("删除失败！！！");
        }
        // 6.返回ResultInfo对象
        return resultInfo;
    }

    /**
     * 添加 或 修改 类型：
     * --1.判断参数是否为空 （类型名称）
     * ----如果为空，code=0，msg=xxx，返回ResultInfo对象
     * --2.如果不为空，调用Dao层，查询当前登录用户下，类型名称是否唯一，返回0或1
     * ----如果不可用，code=0，msg=xxx，返回ResultInfo对象
     * --3.判断类型ID是否为空
     * ----如果为空，调用Dao层的添加方法，返回主键 （前台页面需要显示添加成功之后的类型ID）
     * ----如果不为空，调用Dao层的修改方法，返回受影响的行数
     * --4.判断 主键/受影响的行数 是否大于0
     * ----如果大于0，则更新成功
     * ------code=1，result=主键
     * --如果不大于0，则更新失败
     * ------code=0，msg=xxx
     *
     * @param typeId
     * @param typeName
     * @param userId
     * @return
     */
    public ResultInfo<Integer> addOrUpdate(String typeId, String typeName, Integer userId) {
        ResultInfo<Integer> resultInfo = new ResultInfo<>();
        // 1.判断参数是否为空 （类型名称）
        if (StrUtil.isBlank(typeName)) {
            // 如果为空，code = 0，msg = xxx，返回ResultInfo对象
            resultInfo.setCode(0);
            resultInfo.setMsg("类型名称不能为空！");
            return resultInfo;
        }
        // 2.如果不为空，调用Dao层，查询当前登录用户下，类型名称是否唯一，返回0或1（1=可用，0=不可用）
        Integer code = noteTypeDao.checkTypeName(typeId, typeName, userId);

        if (code == 0) {
            // 如果不可用，code=0，msg=xxx，返回ResultInfo对象
            resultInfo.setCode(0);
            resultInfo.setMsg("类型名称已存在，请重新输入！");
            return resultInfo;
        }
        // 3.判断类型ID是否为空，区分是添加 还是 修改 类型操作
        // 返回的结果
        Integer key = null; // 主键或受影响的行数
        if (StrUtil.isBlank(typeId)) {
            // 如果为空，调用Dao层的添加方法，返回主键 （前台页面需要显示添加成功之后的类型ID）
            key = noteTypeDao.addType(typeName, userId); // 添加操作：返回主键
        } else {
            // 如果不为空，调用Dao层的修改方法，返回受影响的行数
            key = noteTypeDao.updateType(typeId, typeName); // 修改操作：返回受影响行数
        }
        // 4.判断 主键/受影响的行数 是否大于0
        if (key > 0) {
            resultInfo.setCode(1);
            resultInfo.setResult(key);
        } else {
            resultInfo.setCode(0);
            resultInfo.setMsg("更新失败！");
        }
        // 返回
        return resultInfo;
    }
}
