package com.pinyougou.search.service.impl;

import com.pinyougou.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import java.util.Arrays;

@Component
public class ItemDeleteListener implements MessageListener {
    @Autowired
    private ItemSearchService itemSearchService;

    @Override
    public void onMessage(Message message) {

        try {
            //将message强转为对象
            ObjectMessage objectMessage=(ObjectMessage)message;
            //将对象强转为long数组 获取商品id
            Long[] goodsIds = (Long[]) objectMessage.getObject();

            System.out.println("监听器接收到消息:"+goodsIds);

            ////调用删除方法删除sole中的商品
            itemSearchService.deleteByGoodsId(Arrays.asList(goodsIds));//将long类型的数组转为list


            System.out.println("删除成功");
        } catch (JMSException e) {
            e.printStackTrace();
        }


    }
}
