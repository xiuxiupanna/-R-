package com.leyou.sms.utils;

import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.leyou.sms.config.SmsProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@EnableConfigurationProperties(SmsProperties.class)
public class SmsUtil {

    @Autowired
    private IAcsClient acsClient;

    public void sendMessage(String phone, String signName, String templateCode, String templateParam) {
        try {
            // TODO 判断redis中是否有这个手机号,有说明最近一分钟发过信息,返回
            // 组装请求对象
            SendSmsRequest request = new SendSmsRequest();
            // 使用Post提交
            request.setMethod(MethodType.POST);
            // 待发送手机号
            request.setPhoneNumbers(phone);
            // 短信签名
            request.setSignName(signName);
            // 短信模板
            request.setTemplateCode(templateCode);
            // 模板中的变量
            request.setTemplateParam(templateParam);
            SendSmsResponse sendSmsResponse = acsClient.getAcsResponse(request);
            // TODO 发送成功以后,向redis存储当前手机号的信息,并且设置有效时间为一分钟


        } catch (ServerException e) {
            log.error("【短信服务】 发送短信失败, e");
        } catch (ClientException e) {
            e.printStackTrace();
        }

    }


}





