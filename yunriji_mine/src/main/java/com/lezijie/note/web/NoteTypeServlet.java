package com.lezijie.note.web;

import com.alibaba.fastjson.JSON;
import com.lezijie.note.po.NoteType;
import com.lezijie.note.po.User;
import com.lezijie.note.service.NoteTypeService;
import com.lezijie.note.util.JsonUtil;
import com.lezijie.note.vo.ResultInfo;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/type")
public class NoteTypeServlet extends HttpServlet{

     private NoteTypeService noteTypeService = new NoteTypeService();

     @Override
     protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

          //设置首页导航高亮值
          request.setAttribute("menu_page", "type");
          //得到用户行为
          String actionName = request.getParameter("actionName");
          //判断用户行为
          if ("list".equals(actionName)) {
               //查询类型列表
               queryTypeList(request, response);

          } else if ("delete".equals(actionName)) {

               deleteType(request, response);

          } else if ("addOrUpdate".equals(actionName)) {
               //添加或修改类型
               addOrUpdate(request, response);

          }

     }

     /**
      * 添加或修改类型
      * @param request
      * @param response
      */
     private void addOrUpdate(HttpServletRequest request, HttpServletResponse response) {
          String typeName = request.getParameter("typeName");
          String typeId = request.getParameter("typeId");
          User user = ((User) request.getSession().getAttribute("user"));
          ResultInfo<Integer> resultInfo = noteTypeService.addOrUpdate(typeId, typeName, user.getUserId());
          JsonUtil.toJson(response, resultInfo);

     }

     /**
      * 删除类型
      * @param request
      * @param response
      */
     private void deleteType(HttpServletRequest request, HttpServletResponse response) {

          String typeId = request.getParameter("typeId");
          ResultInfo<NoteType> resultInfo = noteTypeService.deleteType(typeId);
          JsonUtil.toJson(response, resultInfo);

     }

     /**
      * 查询类型列表
      * @param request
      * @param response
      */
     private void queryTypeList(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
          User user = (User) request.getSession().getAttribute("user");
          List<NoteType> typeList = noteTypeService.findTypeList(user.getUserId());
          request.setAttribute("typeList", typeList);
          //设置首页动态包含的页面值
          request.setAttribute("changePage", "type/list.jsp");
          //请求转发跳转到index.jsp
          request.getRequestDispatcher("index.jsp").forward(request, response);







     }
}
