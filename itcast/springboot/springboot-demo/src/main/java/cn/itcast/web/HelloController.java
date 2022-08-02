package cn.itcast.web;

import cn.itcast.config.JdbcProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
@Slf4j
@RestController
public class HelloController {

//    @Autowired
//    private JdbcProperties jdbcProperties;

    @GetMapping("hello")
    public String hello() {
        log.info("hello method 正在执行!");
        return "hello, Springboot!";
    }


}
