package com.leizijie.note.util;

import com.alibaba.fastjson.JSON;

import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

/**
 * Created by HMF on 2021/07/13 15:31
 */

/**
 * 将对象转换成JSON格式的字符串，响应给ajax的回调函数
 */
public class JsonUtil { // 专门给ajax返回响应数据时，提供的转为Json格式数据的方法
    public static void toJson(HttpServletResponse response, Object result) {
        try {
            // 设置响应类型及编码格式（json类型）
            response.setContentType("application/json;charset=UTF-8");
            // 得到字符输出流，因为通过ajax响应，要通过流来输出
            PrintWriter out = response.getWriter();
            // 通过fastjson工具中的方法，将ResultInfo对象转换成JSON格式的字符串
            String json = JSON.toJSONString(result);
            // 通过输出流输出JSON格式的字符串
            out.write(json);
            // 关闭资源
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
