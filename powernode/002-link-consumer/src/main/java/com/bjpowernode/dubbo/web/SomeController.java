package com.bjpowernode.dubbo.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class SomeController {


    @Autowired
//    private SomeService someService;

    @RequestMapping(value = "/hello")
    public String hello(Model model) {
        return "hello";
    }


}
