package com.pinyougou.pay.service;

import java.util.Map;

//微信支付接口
public interface WinxinPayService {

    /**
     * 生成微信支付二维码
     * @param out_trade_no 订单号
     * @param total_fee 金额
     * @return
     */
    public Map creatNative(String out_trade_no,String total_fee);


    /**
     * 根据订单号查询订单的支付状态
     * @param out_trade_no
     * @return
     */
    public Map queryPayStatus(String out_trade_no);
}
