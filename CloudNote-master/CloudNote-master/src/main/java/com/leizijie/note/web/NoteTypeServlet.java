package com.leizijie.note.web;

import com.leizijie.note.po.NoteType;
import com.leizijie.note.po.User;
import com.leizijie.note.service.NoteTypeService;
import com.leizijie.note.util.JsonUtil;
import com.leizijie.note.vo.ResultInfo;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Created by HMF on 2021/07/13 09:41
 */

/**
 * 类别管理模块
 */
@WebServlet("/type")
public class NoteTypeServlet extends HttpServlet {
    private NoteTypeService noteTypeService = new NoteTypeService();

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 设置首页导航的高亮值
        request.setAttribute("menu_page", "type");

        // 得到用户行为 actionName
        String actionName = request.getParameter("actionName");
        // 判断用户的行为，调用对应的方法
        if ("list".equals(actionName)) {
            // 查询类型列表
            typeList(request, response);
        } else if ("delete".equals(actionName)) {
            // 删除类型
            deleteType(request, response);
        } else if ("addOrUpdate".equals(actionName)) {
            // 添加 或 修改类型
            addOrUpdate(request, response);
        }
    }

    /**
     * 查询类型列表：
     * --1.获取Session作用域设置的user对象
     * --2.调用Service层的查询方法，查询当前登录用户的类型集合，返回集合
     * --3.将类型列表设置到request请求域中
     * --4.设置首页动态包含的页面值
     * --5.请求转发跳转到index.jsp页面
     *
     * @param request
     * @param response
     */
    private void typeList(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 1.获取Session作用域设置的user对象
        User user = (User) request.getSession().getAttribute("user");
        // 2.调用Service层的查询方法，查询当前登录用户的类型集合，返回集合
        List<NoteType> typeList = noteTypeService.findTypeList(user.getUserId());
        // 3.将类型列表设置到request请求域中，然后在页面中获取并展示请求到的数据
        request.setAttribute("typeList", typeList);
        // 4.设置首页动态包含的页面值
        request.setAttribute("changePage", "type/list.jsp");
        // 5.请求转发跳转到index.jsp页面
        request.getRequestDispatcher("index.jsp").forward(request, response);
    }

    /**
     * 删除类型：
     * --1.接收参数（类型ID）
     * --2.调用Service的更新操作，返回ResultInfo对象
     * --3.将ResultInfo对象转换成JSON格式的字符串，响应给ajax的回调函数
     *
     * @param request
     * @param response
     */
    private void deleteType(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 1.接收参数（类型ID）
        String typeId = request.getParameter("typeId");
        // 2.调用Service的更新操作，返回ResultInfo对象
        ResultInfo<NoteType> resultInfo = noteTypeService.deleteType(typeId);
        // 3.将ResultInfo对象转换成JSON格式的字符串，响应给ajax的回调函数，借助fastjson工具实现 json 转换
//        // 设置响应类型及编码格式（json类型）
//        response.setContentType("application/json;charset=UTF-8");
//        // 得到字符输出流，因为通过ajax响应，要通过流来输出
//        PrintWriter out = response.getWriter();
//        // 通过fastjson工具中的方法，将ResultInfo对象转换成JSON格式的字符串
//        String json = JSON.toJSONString(resultInfo);
//        // 通过输出流输出JSON格式的字符串
//        out.write(json);
//        // 关闭资源
//        out.close();
        // 上面的注释代码，直接用下面的这句代码代替，因为封装了 // -----------
        JsonUtil.toJson(response, resultInfo);
    }

    /**
     * 添加 或 修改 类型：
     * --1.接收参数 （类型名称、类型ID）
     * --2.获取Session作用域中的user对象，得到用户ID（因为登录成功后，就会把User设置到Session域中）
     * --3.调用Service层的更新方法，返回ResultInfo对象
     * --4.将ResultInfo转换成JSON格式的字符串，响应给ajax的回调函数（响应给ajax的数据，要转为JSON格式字符串）
     *
     * @param request
     * @param response
     */
    private void addOrUpdate(HttpServletRequest request, HttpServletResponse response) {
        // 1.接收参数 （类型名称、类型ID）
        String typeId = request.getParameter("typeId");
        String typeName = request.getParameter("typeName");
        // 2.获取Session作用域中的user对象，得到用户ID（因为登录成功后，就会把User设置到Session域中）
        User user = (User) request.getSession().getAttribute("user");
        // 3.调用Service层的更新方法，返回ResultInfo对象
        ResultInfo<Integer> resultInfo = noteTypeService.addOrUpdate(typeId, typeName, user.getUserId());
        // 4.将ResultInfo转换成JSON格式的字符串，响应给ajax的回调函数（响应给ajax的数据，要转为JSON格式字符串）
        JsonUtil.toJson(response, resultInfo); // 这里封装了转为Json的代码
    }
}
