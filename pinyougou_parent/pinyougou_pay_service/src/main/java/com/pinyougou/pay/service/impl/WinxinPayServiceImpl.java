package com.pinyougou.pay.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.wxpay.sdk.WXPayUtil;
import com.pinyougou.common.HttpClient;
import com.pinyougou.pay.service.WinxinPayService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.support.ManagedMap;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

@Service
public class WinxinPayServiceImpl implements WinxinPayService {


    @Value("${appid}")
    private String appid;//公众账号ID
    @Value("${partner}")
    private String partner;//商户号
    @Value("${partnerkey}")
    private String partnerkey;//商户秘钥
    @Value("${notifyurl}")
    private String notifyurl;
    /**
     * 生成微信支付二维码
     * @param out_trade_no 订单号
     * @param total_fee 金额
     * @return
     */
    @Override
    public Map creatNative(String out_trade_no, String total_fee) {
        //1.创建参数
        Map<String,String> param=new HashMap<>();
        param.put("appid",appid);//公众账号ID
        param.put("mch_id",partner);//商户号
        param.put("body","品优购");//商品描述
        param.put("out_trade_no",out_trade_no);//订单号
        param.put("total_fee",total_fee);//标价金额
        param.put("spbill_create_ip","127.0.0.1");//终端ip
        param.put("notify_url","http://com.baidu");//通知地址
        param.put("trade_type","NATIVE");//交易类型
        param.put("nonce_str", WXPayUtil.generateNonceStr());//随机字符串

        try {
             //2.生成要发送的xml,调用微信的工具生成xml参数 生产xml参数时自动生成签名
            String xmlParam = WXPayUtil.generateSignedXml(param, partnerkey);

            System.out.println("生成的参数:"+xmlParam);
            //2.1通过回httpclient 实现对微信统一下单接口的调用
            HttpClient client = new HttpClient("https://api.mch.weixin.qq.com/pay/unifiedorder");
            //2.2设置使用https
            client.setHttps(true);
            //2.3，设置请求的xml参数
            client.setXmlParam(xmlParam);
            //2.4发送请求
            client.post();

            //3。获取结果
            String xmlResult = client.getContent();//获取的结果是xml形式

            System.out.println("返回结果:"+xmlResult);
            //3.1将xml形式的结果转为map集合
            Map<String, String> mapReault = WXPayUtil.xmlToMap(xmlResult);
            //3.2创建新的集合 将支付地址，支付金额，订单号 返回前端页面展示给用户
            Map<String,String> map=new HashMap<>();
            map.put("code_url",mapReault.get("code_url"));
            map.put("total_fee",total_fee);
            map.put("out_trade_no",out_trade_no);
            return map;//添加成功时 返回

        } catch (Exception e) {
            e.printStackTrace();
            return new HashMap();//添加师表时返回 空集合
        }
    }


    /**
     * 根据订单号查询订单的支付状态
     * @param out_trade_no
     * @return
     */
    @Override
    public Map queryPayStatus(String out_trade_no) {
        //1.创建参数
        Map<String,String> param=new HashMap<>();
        param.put("appid",appid);//公众号
        param.put("mch_id",partner);//商户号
        param.put("out_trade_no",out_trade_no);//订单号
        param.put("nonce_str",WXPayUtil.generateNonceStr());//随机字符串
        try {
            //2.生成要发送的xml参数
            String xmlParam = WXPayUtil.generateSignedXml(param, partnerkey);
            //2.使用httpclient工具调用微信的查询订单接口
            HttpClient client = new HttpClient("https://api.mch.weixin.qq.com/pay/orderquery");
            //2.1使用https
            client.setHttps(true);
            //2.2设置请求参数
            client.setXmlParam(xmlParam);
            //2.3发送请求
            client.post();
            //3.获取返回结果
            String xmlResult = client.getContent();
            //3.1将xml格式的结果转为map集合
            Map<String, String> map = WXPayUtil.xmlToMap(xmlResult);

            System.out.println("trade_state:"+map.get("trade_state"));
            //返回
            return map;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
