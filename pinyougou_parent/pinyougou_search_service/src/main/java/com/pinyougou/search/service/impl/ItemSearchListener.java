package com.pinyougou.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.util.List;
import java.util.Map;

@Component
public class ItemSearchListener implements MessageListener {

    @Autowired
    private ItemSearchService itemSearchService;


    @Override
    public void onMessage(Message message) {
        System.out.println("监听接收到消息--------");

        try {
            //将message转为TextMessage
            TextMessage textMessage=(TextMessage)message;
            //接收消息
            String text = textMessage.getText();
            //将接收到的信息转为list集合
            List<TbItem> itemList = JSON.parseArray(text, TbItem.class);
            //遍历集合
            for (TbItem item : itemList) {

                System.out.println(item.getId()+"  "+item.getTitle());//打印标题

                //将表中的spec字段转为map集合，
                Map map = JSON.parseObject(item.getSpec());
                //设置specMap的值，动态域，带注解的字段
                item.setSpecMap(map);
            }
            //将sku导入solr
            itemSearchService.importList(itemList);

            System.out.println("成功导入solr------");

        } catch (JMSException e) {
            e.printStackTrace();
        }




    }
}
