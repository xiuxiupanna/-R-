package com.leizijie.note.po;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * Created by HMF on 2021/07/14 21:52
 */
@Getter
@Setter
public class Note {
    private Integer noteId; // 云记id
    private String title; // 云记标题
    private String content; // 云记内容
    private Integer typeId; // 云记类型
    private Date pubTime; // 发布时间 ---------- 注意这里的类型 Date

    private Float lon; // 经度
    private Float lat; // 纬度

    private String typeName; // 云记类型名
}
