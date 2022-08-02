package com.leyou.common.vo;

import com.leyou.common.exceptions.LyException;
import lombok.Data;
import org.joda.time.DateTime;

@Data
public class ExceptionResult {
    private int status;
    private String message;
    private String timestamp;

    public ExceptionResult(LyException e) {
        this.status = status;
        this.message = message;
        this.timestamp = DateTime.now().toString("yyyy-MM-dd HH:mm:ss");
    }
}
