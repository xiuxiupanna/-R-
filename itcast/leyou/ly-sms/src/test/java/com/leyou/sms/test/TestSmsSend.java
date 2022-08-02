package com.leyou.sms.test;

import com.leyou.sms.utils.SmsUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


import java.util.HashMap;
import java.util.Map;

@SpringBootTest
@RunWith(SpringRunner.class)
public class TestSmsSend {

    @Autowired
    private AmqpTemplate amqpTemplate;

    public void testSmsSend() {
        SmsUtil smsUtil = new SmsUtil();
//        smsUtil.sendMessage("13205698103", "谷粒商城", "SMS_200700741",  );


    }
    @Test
    public void sendMsg() {
        Map<String, String> msg = new HashMap<>();
        msg.put("phone", "13205698103");
        msg.put("code", "454367");
        amqpTemplate.convertAndSend("ly.sms.exchange", "sms.verify.code", msg);

    }





}
