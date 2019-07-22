package cn.itcast;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 消息生产者
 */
@RestController
public class QueueController {

    @Autowired
    private JmsMessagingTemplate jmsMessagingTemplate;

    @RequestMapping("/send")
    public void send(String text){
        jmsMessagingTemplate.convertAndSend("itcast",text);

    }


    @RequestMapping("/sendMap")
    private void sendMap(){
        Map map=new HashMap();
        map.put("mobile","12345678911");
        map.put("content","恭喜获得 10 元代金券");

        jmsMessagingTemplate.convertAndSend("itcast-map",map);
    }

    @RequestMapping("/sendSms")
    private void sendSms(){
        Map<String,String> map=new HashMap<>();
        //设置键的值
        map.put("mobile","15098869387");
        map.put("sign_name","品优购");
        map.put("template_code","SMS_171117382");
        map.put("param","{\"code\":\"123\"}");

        //发送信息
        jmsMessagingTemplate.convertAndSend("sms",map);

    }

}
