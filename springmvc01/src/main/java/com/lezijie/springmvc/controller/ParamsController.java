package com.lezijie.springmvc.controller;

import com.lezijie.springmvc.vo.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 参数绑定
 *    基本类型
 *
 */
@Controller
public class ParamsController {
    /**
     * 基本类型
     *      参数值必须存在, 如果没有指定参数值,也没有配置默认值 此时方法500 错误
     *      防止500参数错误,可以使用 @RequestParam 配置参数
     * @param age
     * @param s
     */
    @RequestMapping("p01")
    public void p01(int age, double s) {
        System.out.println("age: " + age + " s:" + s);


    }

    @RequestMapping("p02")
    public void p02(@RequestParam(defaultValue = "20") int age, @RequestParam(defaultValue = "456.34") double s) {
        System.out.println("age: " + age + " s:" + s);


    }


    @RequestMapping("p03")
    public void p03(@RequestParam(defaultValue = "20") int age, @RequestParam(defaultValue = "456.34") double s) {
        System.out.println("age: " + age + " s:" + s);


    }

    /**
     * 传参形式 : ids=23&ids=234&ids=234
     * @param ids
     */
    @RequestMapping("p04")
    public void p04(Integer [] ids) {
        for (Integer id : ids) {
            System.out.println(id);
        }

    }

    /**
     * 客户端参数名称与user属性名一致即可
     * @param user
     */
    @RequestMapping("p05")
    public void p05(User user) {
        System.out.println(user);

    }









}
