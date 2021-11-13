package com.leizijie.note.vo;

/**
 * Created by HMF on 2021/07/15 21:42
 */

import lombok.Getter;
import lombok.Setter;

/**
 * 专门接收主页云记日期分组数据
 */
@Getter
@Setter
public class NoteVo {

    private String groupName; // 分组名称
    private long noteCount; // 云记数量

    private Integer typeId; // 类型Id
}
