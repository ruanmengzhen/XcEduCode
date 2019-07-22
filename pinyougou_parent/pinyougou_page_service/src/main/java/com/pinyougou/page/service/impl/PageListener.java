package com.pinyougou.page.service.impl;

import com.pinyougou.page.service.ItemPageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;


@Component
public class PageListener implements MessageListener {

    @Autowired
    private ItemPageService itemPageService;

    @Override
    public void onMessage(Message message) {
        try {
            //将message转为TextMessage
            TextMessage textMessage=(TextMessage)message;
            //获取消息
            String text = textMessage.getText();

            System.out.println("接收到的消息是："+text);
            //调用生成静态页面的方法
            Boolean b = itemPageService.getItemHtml(Long.parseLong(text));//将text转为long类型


        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
