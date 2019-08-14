package com.xuecheng.framework.exception;

import com.google.common.collect.ImmutableMap;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.framework.model.response.ResultCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.naming.ldap.PagedResultsControl;
import javax.xml.stream.FactoryConfigurationError;
import java.io.IOException;


/**
 * 异常捕获类
 */
@ControllerAdvice//全局捕获异常
public class ExceptionCatch {
    //定义日志
    private static final Logger LOGGER=LoggerFactory.getLogger(ExceptionCatch.class);

    //定义map集合存储系统抛出的异常 使用使用ImmutableMap 因为他的特点是一旦创建不可改变，并且线程安全 ，集合的泛型是 异常的类型和异常信息
    private static  ImmutableMap<Class<? extends Throwable>,ResultCode> EXCEPTIONS;
    //使用builder来构建map集合
    private static ImmutableMap.Builder<Class<? extends Throwable>,ResultCode> builder=ImmutableMap.builder();
    //在静态代码块中将异常类型 和信息 添加到builder中
    static {
        builder.put(NullPointerException.class, CommonCode.NULL_POINTER);//空指针
        builder.put(IOException.class,CommonCode.FILE_READ_WRITE);//读写异常
        builder.put(HttpMessageNotReadableException.class,CommonCode.INVAILD_PARAMS);//非法参数
    }

    //定义获取可 预知异常的方法
    @ExceptionHandler(CustomException.class)//捕获CustomException 异常
    @ResponseBody//响应json格式数据
    public ResponseResult customException(CustomException custom){
        //打印日志信息
        LOGGER.error("catch exception:{}",custom.getMessage(),custom );
        //获取结果信息
        ResultCode resultCode = custom.getResultCode();
        //返回响应结果
        return new ResponseResult(resultCode);
    }



    //定义获取不可预知异常的方法
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResponseResult exception(Exception e){
        //打印日志信息
        LOGGER.error("catch exception:{}",e.getMessage(),e );
        //当集合为空时，构建集合
        if (EXCEPTIONS==null){

           EXCEPTIONS = builder.build();
        }
        //获取异常信息
        ResultCode resultCode = EXCEPTIONS.get(e.getClass());
        //判断异常信息是否存在
        if (resultCode!=null){
            //存在，返回给响应信息
            return new ResponseResult(resultCode);
        }
        //不存在，返回通用的99999 错误代码异常信息
        return new ResponseResult(CommonCode.SERVER_ERROR);

    }

}
