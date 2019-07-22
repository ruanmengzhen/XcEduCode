package cn.itcast.demo;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

public class MyMessageListener implements MessageListener {

    public void onMessage(Message message) {
        //将messsge转为 TextMessage
        TextMessage textMessage=(TextMessage)message;
        //接收消息
        try {
            System.out.println("接收到的消息是："+textMessage.getText());
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
