import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.HashMap;
import java.util.Map;

public class test {


    public static void main(String[] args) {

        String random = RandomStringUtils.randomNumeric(6);
        System.out.println(random);

        Map map=new HashMap();
        map.put("code","123");
        String s = JSON.toJSONString(map);//将map形式转为json字符串形式{"code":"123"}
        System.out.println(s);

    }
}
