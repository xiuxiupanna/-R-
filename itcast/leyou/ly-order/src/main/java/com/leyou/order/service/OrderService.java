package com.leyou.order.service;

import com.ctc.wstx.util.WordResolver;
import com.leyou.auth.pojo.UserInfo;
import com.leyou.common.dto.CartDTO;
import com.leyou.common.dto.OrderDTO;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exceptions.LyException;
import com.leyou.common.utils.IdWorker;
import com.leyou.item.client.GoodsClient;
import com.leyou.item.pojo.Sku;
import com.leyou.order.enums.OrderStatusEnum;
import com.leyou.order.enums.PayState;
import com.leyou.order.interceptors.UserInterceptor;
import com.leyou.order.mapper.OrderDetailMapper;
import com.leyou.order.mapper.OrderMapper;
import com.leyou.order.mapper.OrderStatusMapper;
import com.leyou.order.pojo.Order;
import com.leyou.order.pojo.OrderDetail;
import com.leyou.order.pojo.OrderStatus;
import com.leyou.order.utils.PayHelper;
import com.leyou.user.client.AddressClient;
import com.leyou.user.pojo.AddressDTO;
import io.jsonwebtoken.lang.Collections;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class OrderService {


    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderDetailMapper orderDetailMapper;

    @Autowired
    private OrderStatusMapper orderStatusMapper;

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private AddressClient addressClient;

    @Autowired
    private GoodsClient goodsClient;

    @Autowired
    private PayHelper payHelper;

    /**
     * 新增订单
     *
     * @param orderDTO
     * @return
     */
    @Transactional
    public Long createOrder(OrderDTO orderDTO) {

        // 准备Order数据
        // 订单编号的生成
        Order order = new Order();
        long orderId = idWorker.nextId();
        order.setOrderId(orderId);

        // 买家信息
        UserInfo user = UserInterceptor.getUserInfo();
        order.setUserId(user.getId());
        order.setBuyerNick(user.getUsername());
        order.setBuyerRate(false);

        // 根据id查询收件人信息
        Long addressId = orderDTO.getAddressId();
        loadAddressInorder(addressId, order);

        // 订单金额
        List<CartDTO> carts = orderDTO.getCarts();
        // 获取sku的id集合
        List<Long> idList = carts.stream().map(CartDTO::getSkuId).collect(Collectors.toList());
        // 把carts 转成map, key是skuId，值是num
        Map<Long, Integer> cartMap = carts.stream().collect(Collectors.toMap(CartDTO::getSkuId, CartDTO::getNum));
        // 查询sku
        List<Sku> skuList = goodsClient.querySkuByIds(idList);
        // 计算总金额
        // 准备OrderDetail的集合
        long total = 0;
        List<OrderDetail> orderDetails = new ArrayList<>();
        for (Sku sku : skuList) {
            Integer num = cartMap.get(sku.getId());
            total += sku.getPrice() * num;
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrderId(orderId);
            orderDetail.setTitle(sku.getTitle());
            orderDetail.setPrice(sku.getPrice());
            orderDetail.setOwnSpec(sku.getOwnSpec());
            orderDetail.setSkuId(sku.getId());
            orderDetail.setNum(num);
            orderDetail.setImage(StringUtils.substringBefore(sku.getImages(), ","));
            orderDetails.add(orderDetail);
        }
        // 填数据
        order.setTotalPay(total);
        order.setPaymentType(orderDTO.getPaymentType());
        order.setActualPay(total + order.getPostFee());
        // 新增订单
        order.setCreateTime(new Date());
        int count = orderMapper.insertSelective(order);
        if (count != 1) {
            throw new LyException(ExceptionEnum.CREATE_ORDER_ERROR);

        }
        // 准备OrderDetail数据
        count = orderDetailMapper.insertList(orderDetails);
        if (count != orderDetails.size()) {
            throw new LyException(ExceptionEnum.CREATE_ORDER_ERROR);
        }
        // 准备OrderStatus数据
        OrderStatus orderStatus = new OrderStatus();
        orderStatus.setOrderId(orderId);
        orderStatus.setStatus(OrderStatusEnum.INIT.value());
        orderStatus.setCreateTime(order.getCreateTime());
        count = orderStatusMapper.insertSelective(orderStatus);
        if (count != 1) {
            throw new LyException(ExceptionEnum.CREATE_ORDER_ERROR);

        }
        // 减库存
        goodsClient.decreaseStock(orderDTO.getCarts());
        return orderId;

    }

    private void loadAddressInorder(Long addressId, Order order) {
        AddressDTO addressDTO = addressClient.queryAddressById(addressId);
        order.setReceiver(addressDTO.getReceiver());
        order.setReceiverMobile(addressDTO.getReceiverMobile());
        order.setReceiverState(addressDTO.getReceiverState());
        order.setReceiverCity(addressDTO.getReceiverCity());
        order.setReceiverDistrict(addressDTO.getReceiverDistrict());
        order.setReceiverAddress(addressDTO.getReceiverAddress());
        order.setReceiverZip(addressDTO.getReceiverZip());
    }

    /**
     * 根据orderId查询订单
     * @param orderId
     * @return
     */
    public Order queryOrderById(Long orderId) {
        // 查询订单
        Order order = orderMapper.selectByPrimaryKey(orderId);
        if (order == null) {
            throw new LyException(ExceptionEnum.ORDER_NOT_FOUND);

        }
        // 查询订单详情
        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setOrderId(orderId);
        List<OrderDetail> details = orderDetailMapper.select(orderDetail);
        if (Collections.isEmpty(details)) {
            throw new LyException(ExceptionEnum.ORDER_NOT_FOUND);

        }
        order.setOrderDetails(details);
        // 查询订单状态
        OrderStatus orderStatus = orderStatusMapper.selectByPrimaryKey(orderId);
        if (orderStatus == null) {
            throw new LyException(ExceptionEnum.ORDER_NOT_FOUND);

        }
        order.setOrderStatus(orderStatus);

        return order;
    }

    /**
     * 根据订单编号生成支付链接并返回
     * @param orderId
     * @return
     */
    public String queryPayUrl(Long orderId) {
        // 查询order
        Order order = queryOrderById(orderId);
        // 判断订单状态
        if (order.getOrderStatus().getStatus() != OrderStatusEnum.INIT.value()) {
            // 说明已经支付，抛出异常
            throw new LyException(ExceptionEnum.ORDER_STATUS_ERROR);
        }

        // 商品描述
        String desc = order.getOrderDetails().stream()
                .map(OrderDetail::getTitle)
                .collect(Collectors.joining(","));
        // 支付的实付金额
        Long totalPay = order.getActualPay();
        // 生成url并返回
        return payHelper.getPayUrl(orderId.toString(), desc, totalPay.toString());

    }

    public void handlerNotify(Map<String, String> data) {
        // 校验通信和业务标识
        payHelper.isConnectSuccess(data);
        // 校验签名
        payHelper.isSignatureValid(data);

        // 校验是否处理过该消息
        String tradeNo = data.get("out_trade_no");
        String totalFee = data.get("total_fee");
        if (StringUtils.isBlank(totalFee) || StringUtils.isBlank(tradeNo)) {
            return;
        }
        Long orderId = Long.valueOf(tradeNo);
        // 查询order
        Order order = queryOrderById(orderId);
        // 判断订单状态
        if (order.getOrderStatus().getStatus() != 1) {
            // 说明已经支付
           return;
        }
        // 校验金额
        Long totalPay = Long.valueOf(totalFee);
        if (order.getActualPay() == totalPay) {
            return;
        }

        // 业务标识
        if ("FAIL".equals(data.get("result_code"))) {
            // 支付失败
            return;
        }

        // 如果成功，修改订单的状态为已支付
        OrderStatus orderStatus = new OrderStatus();
        orderStatus.setOrderId(orderId);
        orderStatus.setStatus(OrderStatusEnum.PAY_UP.value());
        orderStatus.setPaymentTime(new Date());
        orderStatusMapper.updateByPrimaryKeySelective(orderStatus);

    }

    /**
     * 查询支付状态
     * @param orderId
     * @return
     */
    public PayState queryPayState(Long orderId) {

        // 先查询订单状态
        OrderStatus orderStatus = orderStatusMapper.selectByPrimaryKey(orderId);
        if (orderStatus.getStatus() != OrderStatusEnum.INIT.value()) {
            // 说明已经支付
            return PayState.SUCCESS;

        }
        // 未支付，再去查询微信系统
        return payHelper.queryPayState(orderId);





    }
}
