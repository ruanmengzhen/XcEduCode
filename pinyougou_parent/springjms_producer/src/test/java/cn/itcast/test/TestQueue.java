package cn.itcast.test;

import cn.itcast.demo.QueueProducer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:applicationContext-jms-producer.xml")
public class TestQueue {
    //注入点对点消息生产类对象
    @Autowired
    private QueueProducer queueProducer;

    @Test
    public void testSend(){
        queueProducer.sendTextMessage("springjms-点对点");
    }
}
