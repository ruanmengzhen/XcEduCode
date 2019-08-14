package com.xuecheng.manage_cms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

//spring boot 启动类，启动时会扫描Bean 注入spring 容器

@SpringBootApplication//标志是个启动类
@EntityScan("com.xuecheng.framework.domain.cms")//扫描 cms 实体类
@ComponentScan(basePackages = {"com.xuecheng.api"})//扫描api 下的所有的包
@ComponentScan(basePackages = {"com.xuecheng.framework"})
@ComponentScan(basePackages = {"com.xuecheng.manage_cms"})//扫描本项目下的所有包
public class ManageCmsApplication {
    public static void main(String[] args) {
        SpringApplication.run(ManageCmsApplication.class,args);
    }




    //配置RestTemplate 用户请求http接口 获取数据模型
    @Bean
    public RestTemplate restTemplate(){
        return new RestTemplate(new OkHttp3ClientHttpRequestFactory());
    }

}
