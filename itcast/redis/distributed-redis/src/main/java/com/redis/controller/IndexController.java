package com.redis.controller;

import com.redis.service.RedisLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@Controller
public class IndexController {

    @Autowired
    private RedisLock redisLock;

    int count = 0;
    @RequestMapping("/idnex")
    @ResponseBody
    public String index() {
        int clientcount = 1000;
        CountDownLatch countDownLatch = new CountDownLatch(clientcount);
        ExecutorService executorService = Executors.newFixedThreadPool(clientcount);

        long start = System.currentTimeMillis();
        for (int i = 0; i < clientcount; i++) {
            executorService.execute(()-> {


                    }

            );

        }

        return null;

    }





}
