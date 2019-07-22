package cn.itcast;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 消息消费者
 */
@Component
public class Consumer {

    @JmsListener(destination = "itcast")
    public void readMessage(String text){

        System.out.println("接收到的消息是："+text);
    }


    @JmsListener(destination = "itcast-map")
    public void readMap(Map map){
        System.out.println("收到的消息是："+map);
    }


}
