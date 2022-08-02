package com.leyou.user.web;

import com.leyou.user.pojo.User;
import com.leyou.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.stream.Collectors;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 校验数据是否可用
     * @param data
     * @param type
     * @return
     */
    @GetMapping("/check/{data}/{type}")
    public ResponseEntity<Boolean> checkData(@PathVariable("data") String data,
                                             @PathVariable("type") Integer type) {
        return ResponseEntity.ok(userService.checkData(data, type));

    }

    @PostMapping("/code")
    public ResponseEntity<Void> sendCode(@RequestParam("phone") String phone) {
        userService.sendCode(phone);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();

    }

    /**
     * 注册用户
     * @param user
     * @param code
     * @return
     */
    @PostMapping("/register")
    public ResponseEntity<Void> register(@Valid User user, BindingResult result, @RequestParam("code") String code) {
        if (result.hasFieldErrors()) {
            // 有错
            String message = result.getFieldErrors()
                    .stream().map(e -> e.getDefaultMessage()).collect(Collectors.joining("|"));
            throw new RuntimeException(message);
        }
        userService.register(user, code);
        return ResponseEntity.status(HttpStatus.CREATED).build();

    }

    /**
     * 根据用户名密码查询用户
     * @param username
     * @param password
     * @return
     */
    @GetMapping("query")
    public ResponseEntity<User> queryByUserNameAndPassword(@RequestParam("username") String username,
                                                           @RequestParam("  password") String password) {
        return ResponseEntity.ok(userService.queryByUserNameAndPassword(username, password));

    }



}
