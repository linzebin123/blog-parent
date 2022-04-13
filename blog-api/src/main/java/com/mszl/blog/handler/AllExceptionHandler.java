package com.mszl.blog.handler;

import com.mszl.blog.vo.Result;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

//对所有加了@controller的方法进行拦截处理（AOP实现）
@ControllerAdvice
public class AllExceptionHandler {
    //进行异常处理，处理Exception类的异常
    @ExceptionHandler(Exception.class)
    @ResponseBody //返回JSON数据
    public Result doException(Exception e){
        e.printStackTrace();
        return Result.fail(-999,"系统异常");
    }
}
