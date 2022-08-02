package com.example.demo;

import com.example.demo.pojo.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisTemplate;

@SpringBootTest
class DemoApplicationTests {

    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    void contextLoads() {

        //获取redis连接对象
//        RedisConnection connection = redisTemplate.getConnectionFactory().getConnection();

        redisTemplate.opsForValue().set("user", "saber");
        System.out.println(redisTemplate.opsForValue().get("user"));

    }

    @Test
    public void test01() throws JsonProcessingException {
        User user = new User("TY", 290);
        String jsonUser = new ObjectMapper().writeValueAsString(user);

        redisTemplate.opsForValue().set("user", jsonUser);
        System.out.println(redisTemplate.opsForValue().get("user"));


    }
}
