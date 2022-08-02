package com.leyou.sms.config;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.profile.DefaultProfile;
import jdk.internal.org.objectweb.asm.tree.TryCatchBlockNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
@Slf4j
@Configuration
public class SmsConfig {

    @Bean
    public IAcsClient createIAcsClient (SmsProperties properties) {
        try{
            // 设置超时时间-可以自行调整
            System.setProperty("sun.net.client.defaultConnectTimeout", properties.getConnectTimeout()) ;
            System.setProperty("sun.net.client.defaultReadTimeout", properties.getReadTimeout());
            //产品名称:云通信短信API产品,开发者无需替换
            final String product = "Dysmsapi"; // 短信API产品名称
            //产品域名,开发者无需替换
            final String domain = "dysmsapi.aliyuncs.com"; // 短信API产品域名
            //替换为自己的AK
            final String accessKeyId = properties.getAccessKeyId();
            final String accessKeySecret = properties.getAccessKeySecret();
            DefaultProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKeyId, accessKeySecret);
            DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", product, domain);
            return new DefaultAcsClient(profile);
        } catch (Exception e) {
            log.error("【短信服务】 初始化短信客户端失败, e");
            throw new RuntimeException(e);
        }




    }


}
