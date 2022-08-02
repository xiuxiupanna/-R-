package com.leyou.auth.config;

import com.leyou.auth.utils.RsaUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.security.PrivateKey;
import java.security.PublicKey;

@Component
@Slf4j
@Data
@ConfigurationProperties("ly.jwt")
public class JwtProperties {
    // 公钥地址
    private String publicFilePath;
    // 私钥地址
    private String privateFilePath;
    // 公钥
    private PublicKey publicKey;
    // 私钥
    private PrivateKey privateKey;
    //Cookie的名称
    private String cookieName;

    private int expire;

    // <bean init-method="" />
    @PostConstruct
    public void init(){
        try {
            this.publicKey = RsaUtils.getPublicKey(publicFilePath);
            this.privateKey = RsaUtils.getPrivateKey(privateFilePath);
        } catch (Exception e){
            log.error("【授权中心】加载公钥和私钥失败。", e);
            throw new RuntimeException("加载公钥和私钥失败。", e);
        }
    }
}