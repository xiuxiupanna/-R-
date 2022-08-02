package com.leyou.order.utils;

import com.github.wxpay.sdk.WXPay;
import com.github.wxpay.sdk.WXPayConstants;
import com.github.wxpay.sdk.WXPayUtil;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exceptions.LyException;
import com.leyou.order.config.PayConfig;
import com.leyou.order.enums.OrderStatusEnum;
import com.leyou.order.enums.PayState;
import com.leyou.order.mapper.OrderMapper;
import com.leyou.order.mapper.OrderStatusMapper;
import com.leyou.order.pojo.Order;
import com.leyou.order.pojo.OrderStatus;
import com.netflix.discovery.converters.Auto;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.leyou.order.enums.PayState.FAIL;

@Component
@Slf4j
public class PayHelper {

    @Autowired
    private WXPay wxPay;

    @Autowired
    private PayConfig payConfig;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderStatusMapper orderStatusMapper;

    private static final String TRADE_TYPE = "NATIVE";


    public PayHelper(PayConfig payConfig) {
        // 使用微信官方提供的SDK工具，WxPay，并且把配置注入进去
        wxPay = new WXPay(payConfig);
        this.payConfig = payConfig;
    }

    public PayState queryPayState(Long orderId) {
        try {
            // 组织请求参数
            Map<String, String> data = new HashMap<>();
            // 订单号
            data.put("out_trade_no", orderId.toString());
            // 查询状态
            Map<String, String> result = wxPay.orderQuery(data);

            // 校验通信状态
            isConnectSuccess(result);
            // 校验业务状态
            isBusinessSuccess(result);
            // 校验签名
            isSignatureValid(result);

            // 校验金额
            String totalFeeStr = result.get("total_fee");
            String tradeNo = result.get("out_trade_no");
            if(StringUtils.isEmpty(totalFeeStr) || StringUtils.isEmpty(tradeNo)){
                throw new LyException(ExceptionEnum.INVALID_ORDER_PARAM);
            }
            // 3.1 获取结果中的金额
            Long totalFee = Long.valueOf(totalFeeStr);
            // 3.2 获取订单金额
            Order order = orderMapper.selectByPrimaryKey(orderId);
            if(totalFee != order.getActualPay()){
                // 金额不符
                throw new LyException(ExceptionEnum.INVALID_ORDER_PARAM);
            }

            String state = result.get("trade_state");
            if("SUCCESS".equals(state)){
                // 支付成功
                // 修改订单状态
                OrderStatus status = new OrderStatus();
                status.setStatus(OrderStatusEnum.PAY_UP.value());
                status.setOrderId(orderId);
                status.setPaymentTime(new Date());
                int count = orderStatusMapper.updateByPrimaryKeySelective(status);
                if(count != 1){
                    throw new LyException(ExceptionEnum.UPDATE_ORDER_STATUS_ERROR);
                }
                // 返回成功
                return PayState.SUCCESS;
            }

            // 未支付
            if("NOTPAY".equals(state) || "USERPAYING".equals(state)){
                return PayState.NOT_PAY;
            }

            // 支付失败
            return FAIL;
        }catch (Exception e){
            return PayState.NOT_PAY;
        }
    }


    /**
     * 获取支付的url连接
     * @return
     */
    public String getPayUrl(String orderId, String desc, String totalPay) {
        // 准备请求参数
        HashMap<String, String> data = new HashMap<>();
        data.put("body", desc);
        data.put("out_trade_no", orderId);
        data.put("total_fee", totalPay);
        data.put("spbill_create_ip", "123.12.12.123");
        data.put("notify_url", payConfig.getNotifyUrl());
        data.put("trade_type", TRADE_TYPE);

        try {
            Map<String, String> resultMap = wxPay.unifiedOrder(data);
            // 校验通信标识
            isConnectSuccess(resultMap);

            // 校验业务标识
            isBusinessSuccess(resultMap);

            //校验签名
            isSignatureValid(resultMap);

        } catch (Exception e) {
            log.error("【微信支付】 微信支付下单失败 ", e);
            throw new LyException(ExceptionEnum.WX_ORDER_ERROR);
        }
        return null;


    }

    public void isSignatureValid(Map<String, String> resultMap) {
        try {
            boolean bool1 = WXPayUtil.isSignatureValid(resultMap, payConfig.getKey(), WXPayConstants.SignType.HMACSHA256);
            boolean bool2 = WXPayUtil.isSignatureValid(resultMap, payConfig.getKey(), WXPayConstants.SignType.MD5);
            if (!bool1 && !bool2) {
                // 签名有误
                log.error("【微信支付】 微信支付签名无效 ");
                throw new LyException(ExceptionEnum.WX_SIGNATURE_INVALID);
            }
        } catch (Exception e) {
            log.error("【微信支付】 微信支付签名无效 ");
            throw new LyException(ExceptionEnum.WX_SIGNATURE_INVALID);

        }

    }

    public void isConnectSuccess(Map<String, String> resultMap) {
        if ("FAIL".equals(resultMap.get("return_code"))) {
            log.error("【微信支付】 支付通信失败，原因：{}", resultMap.get("return_msg"));
            throw new LyException(ExceptionEnum.WX_CONNECTION_ERROR);
        }

    }

    public void isBusinessSuccess(Map<String, String> resultMap) {
        // 业务标识
        if ("FAIL".equals(resultMap.get("result_code"))) {
            log.error("【微信支付】 微信支付下单失败，原因：{}", resultMap.get("err_code_des"));
            throw new LyException(ExceptionEnum.WX_ORDER_ERROR);
        }
    }

    public String createOrder(Long orderId, Long totalPay, String desc){
        try {
            Map<String, String> data = new HashMap<>();
            // 商品描述
            data.put("body", desc);
            // 订单号
            data.put("out_trade_no", orderId.toString());
            //金额，单位是分
            data.put("total_fee", totalPay.toString());
            //调用微信支付的终端IP
            data.put("spbill_create_ip", "127.0.0.1");
            //回调地址
            data.put("notify_url", config.getNotifyUrl());
            // 交易类型为扫码支付
            data.put("trade_type", "NATIVE");

            // 利用wxPay工具,完成下单
            Map<String, String> result = wxPay.unifiedOrder(data);

            // 判断通信和业务标示
            isSuccess(result);

            // 校验签名
            isValidSign(result);

            // 下单成功，获取支付链接
            String url = result.get("code_url");
            return url;
        } catch (Exception e) {
            log.error("【微信下单】创建预交易订单异常失败", e);
            return null;
        }
    }
    public void isSuccess(Map<String, String> result) {
        // 判断通信标示
        String returnCode = result.get("return_code");
        if(FAIL.equals(returnCode)){
            // 通信失败
            log.error("[微信下单] 微信下单通信失败,失败原因:{}", result.get("return_msg"));
            throw new LyException(ExceptionEnum.WX_PAY_ORDER_FAIL);
        }

        // 判断业务标示
        String resultCode = result.get("result_code");
        if(FAIL.equals(resultCode)){
            // 通信失败
            log.error("[微信下单] 微信下单业务失败,错误码:{}, 错误原因:{}",
                    result.get("err_code"), result.get("err_code_des"));
            throw new LyException(ExceptionEnum.WX_PAY_ORDER_FAIL);
        }
    }

    public void isValidSign(Map<String, String> result) {
        // 校验签名
        try {
            boolean boo = WXPayUtil.isSignatureValid(result, payConfig.getKey(), WXPayConstants.SignType.HMACSHA256);
            boolean boo2 = WXPayUtil.isSignatureValid(result, payConfig.getKey(), WXPayConstants.SignType.MD5);
            if (!boo && !boo2) {
                throw new LyException(ExceptionEnum.WX_PAY_SIGN_INVALID);
            }
        } catch (Exception e) {
            log.error("【微信支付】校验签名失败，数据：{}", result);
            throw new LyException(ExceptionEnum.WX_PAY_SIGN_INVALID);
        }
    }
}
