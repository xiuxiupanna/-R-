package com.bjpowernode.dubbo.service.impl;

import com.bjpowernode.dubbo.service.SomeService;

public class SomeServiceImpl implements SomeService {
    public String hello(String msg) {
        //调用数据持久层
        return "Hello " + msg;
    }
}
