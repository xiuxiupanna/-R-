package com.leyou.common.advice;

import com.leyou.common.exceptions.LyException;
import com.leyou.common.vo.ExceptionResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class BasicExceptionAdvice {

    @ExceptionHandler(LyException.class)
    public ResponseEntity<ExceptionResult> handlerException(LyException e) {
        //构建返回对象
        //返回异常信息
        return ResponseEntity.status(e.getStatus()).body(new ExceptionResult(e));


    }


}
