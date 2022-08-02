package com.leyou.page.web;

import com.leyou.page.service.PageService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

@Controller
public class PageController {

    @Autowired
    private PageService pageService;

    /**
     * 跳转到商品的详情页
     *
     * @param spuId
     * @param model
     * @return
     */
    @GetMapping("/item/{id}.html")
    public String toItemPage(@PathVariable("id") Long spuId, Model model) {
        //查询商品模型数据
        Map<String, Object> data = pageService.loadItemModel(spuId);


        //返回视图
        model.addAllAttributes(data);
        return "item";


    }


}

