package com.leyou.order.web;

import com.github.wxpay.sdk.WXPayConstants;
import com.leyou.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.openfeign.support.ResponseEntityDecoder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class NotifyController {

    @Autowired
    private OrderService orderService;

    /**
     * 接收微信的异步回调通知
     * @param data
     * @return
     */
    @PostMapping(value = "/wxpay/notify", produces = "application/xml")
    public ResponseEntity<Map<String, String>> handlerNotify(@RequestBody Map<String, String> data) {
        orderService.handlerNotify(data);
        HashMap<String, String> result = new HashMap<>();
        result.put("return_code", "SUCCESS");
        result.put("return_msg", "OK");

        return ResponseEntity.ok(result);


    }





}
