package cn.itcast.onsumer.web;

import cn.itcast.onsumer.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("consumer")
public class ConsumerController {

    @Autowired
    private RestTemplate restTemplate;


    @GetMapping("{id}")
    public User queryById(@PathVariable("id") Long id) {

        String url = "http://user-service/user" + id;
        return restTemplate.getForObject(url, User.class);

    }


}
