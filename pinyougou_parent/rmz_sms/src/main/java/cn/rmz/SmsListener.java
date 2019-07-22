package cn.rmz;

import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 消息监听类
 */
@Component
public class SmsListener  {

    @Autowired
    private SmsUtil smsUtil;

    @JmsListener(destination = "sms")
    public void sendSms(Map<String,String> map){
        //调用短信工具类发送消息
        try {
            SendSmsResponse sendSmsResponse = smsUtil.sendSms(
                    map.get("mobile"),
                    map.get("sign_name"),
                    map.get("template_code"),
                    map.get("param")
            );


            System.out.println("code="+sendSmsResponse.getCode());
            System.out.println("message="+sendSmsResponse.getMessage());
            System.out.println("RequestId="+sendSmsResponse.getRequestId());
            System.out.println("流水号BizId="+sendSmsResponse.getBizId());



        } catch (ClientException e) {
            e.printStackTrace();
        }

    }
}
