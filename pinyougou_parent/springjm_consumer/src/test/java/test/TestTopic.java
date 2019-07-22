package test;

import cn.itcast.demo.MyMessageListener;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:applicationContext-jms-consumer.xml")
public class TestTopic {
    //注入点对点消息生产类对象
    @Autowired
    private MyMessageListener myMessageListener;

    @Test
    public void testTopic(){
        while (true){

        }
    }
}
