package cn.itcast.config;

import lombok.Data;
import org.apache.catalina.User;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

//@Data
//@Component
//@EnableConfigurationProperties(JdbcProperties.class)
//@ConfigurationProperties(prefix = "jdbc")
public class JdbcProperties {

    private String driverClassName;
    private String url;
    private String userName;
    private String password;

    private User user = new User();

    @Data
    class User{
        String name;
        int age;
        List<String> girls;
    }


}
