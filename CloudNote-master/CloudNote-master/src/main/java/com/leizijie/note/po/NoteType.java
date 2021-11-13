package com.leizijie.note.po;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by HMF on 2021/07/13 09:37
 */
@Getter
@Setter
public class NoteType {
    private Integer typeId; // 类型ID
    private String typeName; // 类型名称
    private Integer userId; // 用户ID
}
