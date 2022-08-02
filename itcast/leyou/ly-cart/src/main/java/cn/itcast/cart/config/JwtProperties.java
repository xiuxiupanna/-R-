package cn.itcast.cart.config;

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
    // 公钥
    private PublicKey publicKey;
    //Cookie的名称
    private String cookieName;

    // <bean init-method="" />
    @PostConstruct
    public void init(){
        try {
            this.publicKey = RsaUtils.getPublicKey(publicFilePath);
        } catch (Exception e){
            log.error("【授权中心】加载公钥失败。", e);
            throw new RuntimeException(e);
        }
    }
}