package com.leyou.user.pojo;

import lombok.Data;

@Data
public class AddressDTO {
    private Long id;
    private String receiver;// 收件人姓名
    private String receiverMobile;// 电话
    private String receiverState;// 省份
    private String receiverCity;// 城市
    private String receiverDistrict;// 区
    private String receiverAddress;// 街道地址
    private String  receiverZip;// 邮编
    private Boolean isDefault;
}
