package com.leizijie.note.po;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by HMF on 2021/07/10 21:45
 */

@Getter
@Setter
public class User {
    private Integer userId; // 用户ID
    private String uname; // 用户姓名
    private String upwd; // 用户密码
    private String nick; // 用户昵称
    private String head; // 用户头像
    private String mood; // 用户签名
}
