package cn.itcast.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Component;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

@Component
public class QueueProducer {

    //注入jmsTemplate
    @Autowired
    private JmsTemplate jmsTemplate;
    //注入Destination
    @Autowired
    private Destination queueTextDestination;

    //发送文本消息
    public void sendTextMessage(final String text){
        //使用jmsTemplate发送消息
        jmsTemplate.send(queueTextDestination, new MessageCreator() {
            public Message createMessage(Session session) throws JMSException {
                return session.createTextMessage(text);
            }
        });
    }








}
