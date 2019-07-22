package com.pinyougou.page.service.impl;

import com.pinyougou.page.service.ItemPageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import java.io.Serializable;

@Component
public class PageDeleteListener implements MessageListener {

    @Autowired
    private ItemPageService itemPageService;

    @Override
    public void onMessage(Message message) {

        try {
            //将message转为ObjectMessage
            ObjectMessage objectMessage=(ObjectMessage)message;
            //接收消息
            Long[] goodIds = (Long[]) objectMessage.getObject();
            System.out.println("接收到的消息是："+goodIds);

            //调用删除页面的方法
            Boolean b = itemPageService.deleteItemHtml(goodIds);
            System.out.println("页面删除结果是："+b);


        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
