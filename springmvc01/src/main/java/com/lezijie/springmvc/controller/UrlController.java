package com.lezijie.springmvc.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("url")
public class UrlController {
    @RequestMapping("u01")
    public ModelAndView url01() {

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("hello", "hello world!");
        modelAndView.setViewName("hello");
        return modelAndView;

    }

    @RequestMapping(params = "u02")
    public ModelAndView url02() {

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("hello", "hello world!");
        modelAndView.setViewName("hello");
        return modelAndView;
    }


}
