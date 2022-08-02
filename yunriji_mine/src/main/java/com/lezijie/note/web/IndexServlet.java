package com.lezijie.note.web;

import com.lezijie.note.po.Note;
import com.lezijie.note.po.User;
import com.lezijie.note.service.NoteService;
import com.lezijie.note.util.PageUtil;
import com.lezijie.note.vo.NoteVo;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/index")
public class IndexServlet extends HttpServlet {
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //设置首页导航高亮
        request.setAttribute("menu_page", "user");
        //得到用户行为 (判断是什么条件查询:标题查询,日期查询,类型查询)
        String actionName = request.getParameter("actionName");
        //将用户行为设置 request作用域中(分页导航中需要获取)
        request.setAttribute("action", actionName);

        //判断用户行为
        if ("searchTitle".equals(actionName)) { // 标题查询
            //得到查询条件: 标题
            String title = request.getParameter("title");
            //将查询条件设置到request请求域中 (查询条件的回显)
            request.setAttribute("title", title);
            //标题搜索
            noteList(request, response, title, null, null);
        } else if ("searchDate".equals(actionName)) { // 日期查询
            String date = request.getParameter("date");
            //日期搜索
            noteList(request, response, null, date, null);
            //将查询条件设置到request请求域中 (查询条件的回显)
            request.setAttribute("date", date);

        }  else if ("searchType".equals(actionName)) { // 类型查询
            String typeId = request.getParameter("typeId");
            //类型搜索
            noteList(request, response, null, null, typeId);
            //将查询条件设置到request请求域中 (查询条件的回显)
            request.setAttribute("date", typeId);

        } else {
            //分页查询云记列表
            noteList(request, response, null, null, null);

        }



        //设置首页动态包含的页面
        request.setAttribute("changePage","note/list.jsp");
        //请求转发到index.jsp
        request.getRequestDispatcher("index.jsp").forward(request, response );


    }

    /**
     * 分页查询云记列表
     * @param request
     * @param response
     * @param title
     * @param date
     * @param typeId
     */
    private void noteList(HttpServletRequest request, HttpServletResponse response, String title, String date, String typeId) {
        //接收参数
        String pageNum = request.getParameter("pageNum");
        String pageSize = request.getParameter("pageSize");

        //获取Session作用域中的user对象
        User user = (User) request.getSession().getAttribute("user");
        PageUtil<Note> page = new NoteService().findNoteListByPage(pageNum, pageSize, user.getUserId(), title, date, typeId);

        //将page对象设置到request作用域中
        request.setAttribute("page", page);

        //通过日期分组查询当前登录用户下的云记数量
        List<NoteVo> dateInfo = new NoteService().findNoteListByDate(user.getUserId());
        //设置集合存放在request作用域中
        request.getSession().setAttribute("dateInfo", dateInfo);

        //通过类型分组查询当前登录用户下的云记数量
        List<NoteVo> typeInfo = new NoteService().findNoteCountByType(user.getUserId());
        //设置集合存放在request作用域中
        request.getSession().setAttribute("typeInfo", typeInfo);

    }
}
