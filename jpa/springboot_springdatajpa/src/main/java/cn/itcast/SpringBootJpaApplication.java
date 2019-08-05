package cn.itcast;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 启动类 需要放在需要扫描的包的同级目录或者上级目录 才能扫描到其他包
 */
@SpringBootApplication
public class SpringBootJpaApplication {
    public static void main(String[] args) {
        SpringApplication.run(SpringBootJpaApplication.class,args);
    }

}
