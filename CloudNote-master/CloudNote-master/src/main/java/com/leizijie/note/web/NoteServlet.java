package com.leizijie.note.web;

import cn.hutool.core.util.StrUtil;
import com.leizijie.note.po.Note;
import com.leizijie.note.po.NoteType;
import com.leizijie.note.po.User;
import com.leizijie.note.service.NoteService;
import com.leizijie.note.service.NoteTypeService;
import com.leizijie.note.vo.ResultInfo;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.Result;
import java.io.IOException;
import java.util.List;

/**
 * Created by HMF on 2021/07/14 21:59
 */
@WebServlet("/note")
public class NoteServlet extends HttpServlet {
    private NoteService noteService = new NoteService(); // servlet(controller)层调用service层

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 设置发布云记首页导航的高亮值
        request.setAttribute("menu_page", "note");

        // 得到用户行为
        String actionName = request.getParameter("actionName");

        // 一个标志，解决发布云记失败后，刷新页面出现空白的问题，为了保证保存失败后，地址不变而做的修改
        String updateNoteFlag = request.getParameter("updateNoteActionName"); // 这是从input标签提交上来的参数

        // 判断用户行为，进入相关的处理方法中
        if ("view".equals(actionName) && ("".equals(updateNoteFlag) || updateNoteFlag == null)) { // 说明是点击发布云记导航进来的
            // 进入发布云记页面
            noteView(request, response);

        } else if ("view".equals(actionName) && "addOrUpdate".equals(updateNoteFlag)) { // 说明是点击保存按钮提交表单进来的
            // 添加或修改云记
            addOrUpdate(request, response);

        } else if ("detail".equals(actionName) && ("".equals(updateNoteFlag) || updateNoteFlag == null)) { // 说明是点击云记列表
            // 查询云记详情
            noteDetail(request, response);
        } else if ("delete".equals(actionName) && ("".equals(updateNoteFlag) || updateNoteFlag == null)) { // 说明是点击删除云记
            // 删除云记
            noteDelete(request, response);
        }
    }

    /**
     * 进入发布云记页面：
     * --1.从Session对象中获取用户对象
     * --2.通过用户ID查询对应的 类型列表（用于下拉框的展示）
     * --3.将类型列表设置到request请求域中
     * --4.置首页动态包含的页面值
     * --5.请求转发跳转到index.jsp
     *
     * @param request
     * @param response
     */
    private void noteView(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        /* start--修改云记操作 需要的 */
        // 得到要修改的云记Id
        String noteId = request.getParameter("noteId");
        // 通过noteId查询云记对象
        Note note = noteService.findNoteById(noteId);
        // 将note对象设置到请求域中
        request.setAttribute("noteInfo", note);
        /* end--修改云记操作 需要的 */

        // 1.从Session对象中获取用户对象，因为登录成功后就会存在session中
        User user = (User) request.getSession().getAttribute("user");
        // 2.通过用户ID查询对应的 类型列表（用于下拉框的展示）
        // 调用类型管理右侧页面中已经写过的获取类型列表的方法
        List<NoteType> typeList = new NoteTypeService().findTypeList(user.getUserId());
        // 3.将类型列表设置到request请求域中
        request.setAttribute("typeList", typeList);

        // 4.设置首页动态包含的右侧页面值
        request.setAttribute("changePage", "note/view.jsp");
        // 5.请求转发跳转到index.jsp
        request.getRequestDispatcher("index.jsp").forward(request, response);

    }

    /**
     * 添加或修改云记:
     * --1.接收参数 （类型ID、标题、内容）
     * --2.调用Service层方法，返回resultInfo对象
     * --3. 判断resultInfo的code值
     * ----如果code=1，表示成功
     * ------重定向跳转到首页 index
     * ----如果code=0，表示失败
     * ------将resultInfo对象设置到request作用域
     * ------请求转发跳转到note?actionName=view
     *
     * @param request
     * @param response
     */
    private void addOrUpdate(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        // 1.接收参数 （类型ID、标题、内容）
        String typeId = request.getParameter("typeId");
        String title = request.getParameter("title");
        String content = request.getParameter("content");

        // 获取设置到隐藏域中并作为参数提交过来的经纬度
        String lon = request.getParameter("lon");
        String lat = request.getParameter("lat");

        // 如果是修改操作，需要接收要修改云记的noteId
        String noteId = request.getParameter("noteId");

        // 2.调用Service层方法，返回resultInfo对象
        ResultInfo<Note> resultInfo = noteService.addOrUpdate(typeId, title, content, noteId, lon, lat);

        // 3. 判断resultInfo的code值
        if (resultInfo.getCode() == 1) { // 如果code=1，表示成功
            // 重定向跳转到首页 index
            response.sendRedirect("index");
        } else { // 如果code=0，表示失败
            // 将resultInfo对象设置到request作用域
            request.setAttribute("resultInfo", resultInfo);

            // 默认是添加失败操作的url地址
            // 请求转发跳转到note?actionName=view
            //  通过设置请求转发参数updateUserActionName= 为空来删除参数，阻断他再次传递
            String url = "note?actionName=view&updateNoteActionName=";
            if (!StrUtil.isBlank(noteId)) { // 如果是修改操作，需要传递noteId参数
                url += "&noteId=" + noteId;
            }
            request.getRequestDispatcher(url).forward(request, response);
        }
    }

    /**
     * 查询云记详情：
     * --1.接收参数 （noteId）
     * --2.调用Service层的查询方法，返回Note对象
     * --3.将Note对象设置到request请求域中
     * --4.设置首页动态包含的页面值
     * --5.请求转发跳转到index.jsp
     *
     * @param request
     * @param response
     */
    private void noteDetail(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 1.接收参数 （noteId）
        String noteId = request.getParameter("noteId");
        // 2.调用Service层的查询方法，返回Note对象
        Note note = noteService.findNoteById(noteId);
        // 3.将Note对象设置到request请求域中
        request.setAttribute("note", note);
        // 4.设置首页动态包含的页面值
        request.setAttribute("changePage", "note/detail.jsp");
        // 5.请求转发跳转到index.jsp
        request.getRequestDispatcher("index.jsp").forward(request, response);
    }

    /**
     * 删除云记：
     * --1.接收参数 （noteId）
     * --2.调用Service层删除方法，返回状态码code （1=成功，0=失败）
     * --3. 过流将结果响应给ajax的回调函数 （输出字符串）(ajax请求要通过流响应出去)
     *
     * @param request
     * @param response
     */
    private void noteDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 1.接收参数 （noteId）
        String noteId = request.getParameter("noteId");
        // 2.调用Service层删除方法，返回状态码code （1=成功，0=失败）
        Integer code = noteService.deleteNote(noteId);
        // 3.通过流将结果响应给ajax的回调函数 （输出字符串）(ajax请求要通过流响应出去)
        response.getWriter().write(code + ""); // code要转为字符串输出
        response.getWriter().close();
    }
}
