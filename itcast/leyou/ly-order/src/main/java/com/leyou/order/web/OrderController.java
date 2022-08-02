package com.leyou.order.web;

import com.leyou.common.dto.OrderDTO;
import com.leyou.order.pojo.Order;
import com.leyou.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("order")
public class OrderController {
    @Autowired
    private OrderService orderService;


    /**
     * 创建订单
     *
     * @param orderDTO
     * @return
     */
    @PostMapping
    public ResponseEntity<Long> createOrder(@RequestBody OrderDTO orderDTO) {
        Long orderId = orderService.createOrder(orderDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(orderId);

    }

    /**
     * 根据id查询订单
     *
     * @param orderId
     * @return
     */
    @GetMapping("{orderId}")
    public ResponseEntity<Order> queryOrderById(@PathVariable("orderId") Long orderId) {
        return ResponseEntity.ok(orderService.queryOrderById(orderId));


    }

    /**
     * 根据订单编号生成支付链接并返回
     *
     * @param orderId
     * @return
     */
    @GetMapping("/url/{orderId}")
    public ResponseEntity<String> queryPayUrl(@PathVariable("orderId") Long orderId) {
        return ResponseEntity.ok(orderService.queryPayUrl(orderId));
    }

    /**
     * 查询支付状态
     * @param orderId
     * @return
     */
    @GetMapping("/state/{orderId}")
    public ResponseEntity<Integer> queryPayState(@PathVariable("orderId") Long orderId) {
        return ResponseEntity.ok(orderService.queryPayState(orderId).getValue());
    }




}
