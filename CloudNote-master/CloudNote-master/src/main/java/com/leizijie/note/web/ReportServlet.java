package com.leizijie.note.web;

import com.leizijie.note.po.Note;
import com.leizijie.note.po.User;
import com.leizijie.note.service.NoteService;
import com.leizijie.note.util.JsonUtil;
import com.leizijie.note.util.Page;
import com.leizijie.note.vo.ResultInfo;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by HMF on 2021/07/16 13:56
 */
@WebServlet("/report")
public class ReportServlet extends HttpServlet {
    private NoteService noteService = new NoteService();

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // 设置首页导航栏选中数据报表的高亮值
        request.setAttribute("menu_page", "report");

        // 得到用户行为
        String actionName = request.getParameter("actionName");

        // 判断用户行为
        if ("info".equals(actionName)) {
            // 进入报表页面
            reportInfo(request, response);

        } else if ("month".equals(actionName)) {
            // 通过月份查询对应的云记数量
            queryNoteCountByMonth(request, response);

        } else if ("location".equals(actionName)) {
            // 查询用户发布云记时的经纬度坐标
            queryNoteLonAndLat(request, response);
        }
    }

    /**
     * 进入报表页面
     *
     * @param request
     * @param response
     */
    private void reportInfo(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 设置首页动态包含的页面值
        request.setAttribute("changePage", "report/info.jsp");

        // 请求转发跳转到index.jsp
        request.getRequestDispatcher("index.jsp").forward(request, response);
    }

    /**
     * 通过月份查询对应的云记数量
     *
     * @param request
     * @param response
     */
    private void queryNoteCountByMonth(HttpServletRequest request, HttpServletResponse response) {
        // 从session作用域中获取用户对象
        User user = (User) request.getSession().getAttribute("user");
        // 调用service层的查询方法，返回ResultInfo对象
        ResultInfo<Map<String, Object>> resultInfo = noteService.queryNoteCountByMonth(user.getUserId());
        // 将ResultInfo对象转换成JSON格式字符串，响应给ajax的回调函数（因为是对象，所以不能用流直接输出）
        JsonUtil.toJson(response, resultInfo);
    }

    /**
     * 查询用户发布云记时的经纬度坐标
     *
     * @param request
     * @param response
     */
    private void queryNoteLonAndLat(HttpServletRequest request, HttpServletResponse response) {
        // 从session作用域中获取user对象
        User user = (User) request.getSession().getAttribute("user");

        // 调用service层的方法，返回ResultInfo对象
        ResultInfo<List<Note>> resultInfo = noteService.queryNoteLonAndLat(user.getUserId());

        // 将ResultInfo对象转换成JSON格式的字符串，响应给AJAX的回调函数
        JsonUtil.toJson(response, resultInfo);
    }
}
