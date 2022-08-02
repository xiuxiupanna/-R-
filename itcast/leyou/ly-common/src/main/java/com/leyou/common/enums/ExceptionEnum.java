package com.leyou.common.enums;

import lombok.Getter;

@Getter
public enum ExceptionEnum {
    //枚举项,枚举项必须位于枚举类的最前面,最后一个枚举项之后如果有内容加;
    PRICE_CANNOT_BE_NULL(400, "价格不能为空"),
    CATEGORY_NOT_FOUND(404, "商品分类不存在"),
    BRAND_NOT_FOUND(404, "商品品牌不存在"),
    SPEC_GROUP_NOT_FOUND(404, "商品规格组不存在"),
    SPEC_PARAM_NOT_FOUND(404, "商品规格参数不存在"),
    SPU_GOODS_NOT_FOUND(404, "商品SPU类别不存在"),
    SKU_GOODS_NOT_FOUND(404, "商品SKU类别不存在"),
    BRAND_INSERT_ERROR(400, "商品品牌参数不正确"),
    GOODS_INSERT_ERROR(400, "商品新增失败"),
    GOODS_UPDATE_ERROR(400, "商品更新失败"),
    UPLOAD_FILE_ERROR(500, "文件上传失败"),
    INVALID_FILE_TYPE(400, "无效的文件类型"),
    INVALID_PARAM_ERROR(400, "无效的参数异常"),
    INVALID_PHONE_NUMBER(400, "无效的手机号"),
    INVALID_USERNAME_PASSWORD(400, "无效的用户名或密码"),
    CREATE_ORDER_ERROR(400, "创建订单失败"),
    UNAUTHORIZED(401, "没有访问权限"),
    CART_NOT_FOUND(404, "购物车不存在"),
    ORDER_NOT_FOUND(404, "订单不存在"),
    STOCK_NOT_ENOUGH(400, "商品库存不足"),
    WX_CONNECTION_ERROR(500,"微信支付通信失败"),
    WX_ORDER_ERROR(500,"微信支付下单失败"),
    WX_SIGNATURE_INVALID(500,"微信支付签名无效"),
    ORDER_STATUS_ERROR(400, "订单状态异常"),
    INVALID_ORDER_PARAM(400,"订单的参数异常"),
    UPDATE_ORDER_STATUS_ERROR(400,"修改订单状态异常")
    ;

    private int status;
    private String message;

    //构造默认就是私有的
    ExceptionEnum(int status, String message) {
        this.status = status;
        this.message = message;
    }
}
