package com.xuecheng.framework.exception;

import com.xuecheng.framework.model.response.ResultCode;

/**
 *自定义可预知的异常类
 */
public class CustomException extends RuntimeException {


    //定义成员变量 通用返回结果类
    ResultCode resultCode;

    //使用构造方法得到 异常信息
    public CustomException(ResultCode resultCode){

        this.resultCode=resultCode;
    }

    //get 方法
    public ResultCode getResultCode() {
        return resultCode;
    }
}
