package com.leizijie.note.web;

import com.leizijie.note.po.Note;
import com.leizijie.note.po.User;
import com.leizijie.note.service.NoteService;
import com.leizijie.note.util.Page;
import com.leizijie.note.vo.NoteVo;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Created by HMF on 2021/07/11 19:05
 */
@WebServlet("/index")
public class IndexServlet extends HttpServlet {


    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 设置首页导航高亮
        request.setAttribute("menu_page", "index");

        // 得到用户行为actionName（判断是什么条件查询：标题查询、日期查询、类型查询）
        String actionName = request.getParameter("actionName");

        // 将用户行为设置到request作用域中（分页导航中需要获取）
        request.setAttribute("action", actionName);

        // 判断用户行为
        if ("searchTitle".equals(actionName)) { // 标题查询

            // 得到查询条件：标题（即表单输入提交的input标签的内容）
            String title = request.getParameter("title");
            // 查询条件设置到request请求域中，（用于查询条件的回显）
            request.setAttribute("title", title);

            // 标题搜索
            noteList(request, response, title, null, null);

        } else if ("searchDate".equals(actionName)) { // 日期查询

            // 得到查询条件：日期（从请求地址中传过来的请求参数）
            String date = request.getParameter("date");
            // 查询条件设置到request请求域中，（用于查询条件的回显）
            request.setAttribute("date", date);

            // 日期搜索
            noteList(request, response, null, date, null);

        } else if ("searchType".equals(actionName)) { // 类型查询

            // 得到查询条件：类型Id（从请求地址中传过来的请求参数）
            String typeId = request.getParameter("typeId");
            // 查询条件设置到request请求域中，（用于查询条件的回显）
            request.setAttribute("typeId", typeId);

            // 类型搜索
            noteList(request, response, null, null, typeId);

        } else {
            // 分页查询云记列表
            noteList(request, response, null, null, null); //////////////// 云记列表分页展示
        }

        // 设置首页动态包含的页面
        request.setAttribute("changePage", "note/list.jsp");

        // 请求转发到 index.jsp
        request.getRequestDispatcher("index.jsp").forward(request, response);
    }

    /**
     * 分页查询云记列表：
     * --1.接收参数 （当前页、每页显示的数量）
     * --2.获取Session作用域中的user对象
     * --3.调用Service层查询方法，返回Page对象
     * --4.将page对象设置到request作用域中
     *
     * @param request
     * @param response
     * @param title
     */
    private void noteList(HttpServletRequest request, HttpServletResponse response,
                          String title, String date, String typeId) {
        // 1.接收参数 （当前页、每页显示的数量）
        String pageNum = request.getParameter("pageNum");
        String pageSize = request.getParameter("pageSize");

        // 2.获取Session作用域中的user对象
        User user = (User) request.getSession().getAttribute("user");

        // 3.调用Service层查询方法，返回Page对象
        Page<Note> page = new NoteService().findNoteListByPage(pageNum, pageSize, user.getUserId(), title, date, typeId);

        // 4.将page对象设置到request作用域中
        request.setAttribute("page", page);

        // ------------------------------------------------
        // 通过日期分组查询当前登录用户下的云记数量
        List<NoteVo> dataInfo = new NoteService().findNoteCountByDate(user.getUserId());
        // 设置集合存放在session作用域中，因为request请求会经常改变
        request.getSession().setAttribute("dataInfo", dataInfo);

        // 通过类型分组查询当前登录用户下的云记类型数量
        List<NoteVo> typeInfo = new NoteService().findNoteCountByType(user.getUserId());
        // 设置集合存放在session作用域中，因为request请求会经常改变
        request.getSession().setAttribute("typeInfo", typeInfo);
        // ------------------------------------------------
    }
}
