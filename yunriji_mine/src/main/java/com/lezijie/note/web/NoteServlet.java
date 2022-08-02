package com.lezijie.note.web;

import cn.hutool.core.util.StrUtil;
import com.lezijie.note.po.Note;
import com.lezijie.note.po.NoteType;
import com.lezijie.note.po.User;
import com.lezijie.note.service.NoteService;
import com.lezijie.note.service.NoteTypeService;
import com.lezijie.note.vo.ResultInfo;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/note")
public class NoteServlet extends HttpServlet {

    private NoteService noteService = new NoteService();

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //设置首页导航栏的高亮值
        request.setAttribute("menu_page", "note");

        //得到用户行为
        String actionName = request.getParameter("actionName");
        //判断用户行为
        if ("view".equals(actionName)) {
            //进入云记发布页面
            noteView(request, response);

        } else if ("addOrUpdate".equals(actionName)) {
            addOrUpdate(request, response);

        } else if ("detail".equals(actionName)) {
            //查询云记详情
            noteDetail(request, response);

        } else if ("delete".equals(actionName)) {
            noteDelete(request, response);

        }


    }

    /**
     * 删除云记
     *
     * @param request
     * @param response
     */
    private void noteDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String noteId = request.getParameter("noteId");
        Integer code = noteService.deleteNote(noteId);
        response.getWriter().write(code + "");
        response.getWriter().close();

    }

    /**
     * 查询云记详情
     *
     * @param request
     * @param response
     */
    private void noteDetail(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String noteId = request.getParameter("noteId");
        Note note = noteService.findNoteById(noteId);
        //将Note对象设置到request请求域中
        request.setAttribute("note", note);
        //设置首页动态包含的页面值
        request.setAttribute("changePage", "note/detail.jsp");
        //请求转发跳转到index.jsp
        request.getRequestDispatcher("index.jsp").forward(request, response);

    }

    /**
     * 添加或者修改操作
     *
     * @param request
     * @param response
     */
    private void addOrUpdate(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        //接收参数 (类型id,标题,内容)
        String typeId = request.getParameter("typeId");
        String title = request.getParameter("title");
        String content = request.getParameter("content");

        //获取经纬度
        String lon = request.getParameter("lon");
        String lat = request.getParameter("lat");


        //如果是修改操作,需要接收noteId
        String noteId = request.getParameter("noteId");

        //调用Service层方法,返回resultInfo对象
        ResultInfo<Note> resultInfo = noteService.addOrUpdate(typeId, title, content, noteId, lon, lat);
        //判断resultInfo 的 code值
        if (resultInfo.getCode() == 1) {
            //重定向跳转到首页 index
            response.sendRedirect("index");

        } else {
            //将resultInfo对象设置到request作用域
            request.setAttribute("resultInfo", resultInfo);
            //请求转发跳转
            String url = "note?actionName=view";
            //如果是修改操作,需要传递noteId
            if (!StrUtil.isBlank(noteId)) {
                url += "&noteId=" + noteId;

            }
            request.getRequestDispatcher("").forward(request, response);

        }


    }

    /**
     * 进入云记发布页面
     *
     * @param request
     * @param response
     */
    private void noteView(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        /*修改操作*/
        //得到要修改的云记id
        String noteId = request.getParameter("noteId");
        Note note = noteService.findNoteById(noteId);
        //将note对象设置到请求域中
        request.setAttribute("noteInfo", note);

        //从Session对象中获取用户对象
        User user = ((User) request.getSession().getAttribute("user"));
        //通过用户ID查询对应的类型列表
        List<NoteType> typeList = new NoteTypeService().findTypeList(user.getUserId());
        //将类型列表设置到request请求域中
        request.setAttribute("typeList", typeList);
        //设置首页动态包含的页面值
        request.setAttribute("changePage", "note/view.jsp");
        request.getRequestDispatcher("index.jsp").forward(request, response);


    }
}
